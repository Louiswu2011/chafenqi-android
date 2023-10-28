//
// Created by Louis Wu on 2023/10/28.
//
#include "tun2http.h"

extern struct ng_session *ng_session;
extern FILE *pcap_file;

int get_udp_timeout(const struct udp_session *u, int sessions, int maxsessions) {
    int timeout = (ntohs(u->dest) == 53 ? UDP_TIMEOUT_53 : UDP_TIMEOUT_ANY);

    int scale = 100 - sessions * 100 / maxsessions;
    timeout = timeout * scale / 100;

    return timeout;
}

int check_udp_session(const struct arguments *args, struct ng_session *s,
                      int sessions, int maxsessions) {
    time_t now = time(NULL);

    char source[INET6_ADDRSTRLEN + 1];
    char dest[INET6_ADDRSTRLEN + 1];
    if (s->udp.version == 4) {
        inet_ntop(AF_INET, &s->udp.saddr.ip4, source, sizeof(source));
        inet_ntop(AF_INET, &s->udp.daddr.ip4, dest, sizeof(dest));
    } else {
        inet_ntop(AF_INET6, &s->udp.saddr.ip6, source, sizeof(source));
        inet_ntop(AF_INET6, &s->udp.daddr.ip6, dest, sizeof(dest));
    }

    // Check session timeout
    int timeout = get_udp_timeout(&s->udp, sessions, maxsessions);
    if (s->udp.state == UDP_ACTIVE && s->udp.time + timeout < now) {
        log_android(ANDROID_LOG_WARN, "UDP idle %d/%d sec state %d from %s/%u to %s/%u",
                    now - s->udp.time, timeout, s->udp.state,
                    source, ntohs(s->udp.source), dest, ntohs(s->udp.dest));
        s->udp.state = UDP_FINISHING;
    }

    // Check finished sessions
    if (s->udp.state == UDP_FINISHING) {
        log_android(ANDROID_LOG_INFO, "UDP close from %s/%u to %s/%u socket %d",
                    source, ntohs(s->udp.source), dest, ntohs(s->udp.dest), s->socket);

        if (close(s->socket))
            log_android(ANDROID_LOG_ERROR, "UDP close %d error %d: %s",
                        s->socket, errno, strerror(errno));
        s->socket = -1;

        s->udp.time = time(NULL);
        s->udp.state = UDP_CLOSED;
    }

    if (s->udp.state == UDP_CLOSED && (s->udp.sent || s->udp.received)) {
        s->udp.sent = 0;
        s->udp.received = 0;
    }

    // Cleanup lingering sessions
    if ((s->udp.state == UDP_CLOSED || s->udp.state == UDP_BLOCKED) &&
        s->udp.time + UDP_KEEP_TIMEOUT < now)
        return 1;

    return 0;
}

void check_udp_socket(const struct arguments *args, const struct epoll_event *ev) {
    struct ng_session *s = (struct ng_session *) ev->data.ptr;

    // Check socket error
    if (ev->events & EPOLLERR) {
        s->udp.time = time(NULL);

        int serr = 0;
        socklen_t optlen = sizeof(int);
        int err = getsockopt(s->socket, SOL_SOCKET, SO_ERROR, &serr, &optlen);
        if (err < 0)
            log_android(ANDROID_LOG_ERROR, "UDP getsockopt error %d: %s",
                        errno, strerror(errno));
        else if (serr)
            log_android(ANDROID_LOG_ERROR, "UDP SO_ERROR %d: %s", serr, strerror(serr));

        s->udp.state = UDP_FINISHING;
    } else {
        // Check socket read
        if (ev->events & EPOLLIN) {
            s->udp.time = time(NULL);

            uint8_t *buffer = malloc(s->udp.mss);
            ssize_t bytes = recv(s->socket, buffer, s->udp.mss, 0);
            if (bytes < 0) {
                // Socket error
                log_android(ANDROID_LOG_WARN, "UDP recv error %d: %s",
                            errno, strerror(errno));

                if (errno != EINTR && errno != EAGAIN)
                    s->udp.state = UDP_FINISHING;
            } else if (bytes == 0) {
                log_android(ANDROID_LOG_WARN, "UDP recv eof");
                s->udp.state = UDP_FINISHING;

            } else {
                // Socket read data
                char dest[INET6_ADDRSTRLEN + 1];
                if (s->udp.version == 4)
                    inet_ntop(AF_INET, &s->udp.daddr.ip4, dest, sizeof(dest));
                else
                    inet_ntop(AF_INET6, &s->udp.daddr.ip6, dest, sizeof(dest));
                log_android(ANDROID_LOG_INFO, "UDP recv bytes %d from %s/%u for tun",
                            bytes, dest, ntohs(s->udp.dest));

                s->udp.received += bytes;

                // Process DNS response
                if (ntohs(s->udp.dest) == 53)
                    parse_dns_response(args, &s->udp, buffer, (size_t *) &bytes);

                // Forward to tun
                if (write_udp(args, &s->udp, buffer, (size_t) bytes) < 0)
                    s->udp.state = UDP_FINISHING;
                else {
                    // Prevent too many open files
                    if (ntohs(s->udp.dest) == 53)
                        s->udp.state = UDP_FINISHING;
                }
            }
            free(buffer);
        }
    }
}

int has_udp_session(const struct arguments *args, const uint8_t *pkt, const uint8_t *payload) {
    // Get headers
    const uint8_t version = (*pkt) >> 4;
    const struct iphdr *ip4 = (struct iphdr *) pkt;
    const struct ip6_hdr *ip6 = (struct ip6_hdr *) pkt;
    const struct udphdr *udphdr = (struct udphdr *) payload;

    if (ntohs(udphdr->dest) == 53 && !args->fwd53)
        return 1;

    // Search session
    struct ng_session *cur = ng_session;
    while (cur != NULL &&
           !(cur->protocol == IPPROTO_UDP &&
             cur->udp.version == version &&
             cur->udp.source == udphdr->source && cur->udp.dest == udphdr->dest &&
             (version == 4 ? cur->udp.saddr.ip4 == ip4->saddr &&
                             cur->udp.daddr.ip4 == ip4->daddr
                           : memcmp(&cur->udp.saddr.ip6, &ip6->ip6_src, 16) == 0 &&
                             memcmp(&cur->udp.daddr.ip6, &ip6->ip6_dst, 16) == 0)))
        cur = cur->next;

    return (cur != NULL);
}

void block_udp(const struct arguments *args,
               const uint8_t *pkt, size_t length,
               const uint8_t *payload,
               int uid) {
    // Get headers
    const uint8_t version = (*pkt) >> 4;
    const struct iphdr *ip4 = (struct iphdr *) pkt;
    const struct ip6_hdr *ip6 = (struct ip6_hdr *) pkt;
    const struct udphdr *udphdr = (struct udphdr *) payload;

    char source[INET6_ADDRSTRLEN + 1];
    char dest[INET6_ADDRSTRLEN + 1];
    if (version == 4) {
        inet_ntop(AF_INET, &ip4->saddr, source, sizeof(source));
        inet_ntop(AF_INET, &ip4->daddr, dest, sizeof(dest));
    } else {
        inet_ntop(AF_INET6, &ip6->ip6_src, source, sizeof(source));
        inet_ntop(AF_INET6, &ip6->ip6_dst, dest, sizeof(dest));
    }

    log_android(ANDROID_LOG_INFO, "UDP blocked session from %s/%u to %s/%u",
                source, ntohs(udphdr->source), dest, ntohs(udphdr->dest));

    // Register session
    struct ng_session *s = malloc(sizeof(struct ng_session));
    s->protocol = IPPROTO_UDP;

    s->udp.time = time(NULL);
    s->udp.uid = uid;
    s->udp.version = version;

    if (version == 4) {
        s->udp.saddr.ip4 = (__be32) ip4->saddr;
        s->udp.daddr.ip4 = (__be32) ip4->daddr;
    } else {
        memcpy(&s->udp.saddr.ip6, &ip6->ip6_src, 16);
        memcpy(&s->udp.daddr.ip6, &ip6->ip6_dst, 16);
    }

    s->udp.source = udphdr->source;
    s->udp.dest = udphdr->dest;
    s->udp.state = UDP_BLOCKED;
    s->socket = -1;

    s->next = ng_session;
    ng_session = s;
}

jboolean handle_udp(const struct arguments *args,
                    const uint8_t *pkt, size_t length,
                    const uint8_t *payload,
                    int uid,
                    const int epoll_fd) {
    // Get headers
    const uint8_t version = (*pkt) >> 4;
    const struct iphdr *ip4 = (struct iphdr *) pkt;
    const struct ip6_hdr *ip6 = (struct ip6_hdr *) pkt;
    const struct udphdr *udphdr = (struct udphdr *) payload;
    const uint8_t *data = payload + sizeof(struct udphdr);
    const size_t datalen = length - (data - pkt);

    // Search session
    struct ng_session *cur = ng_session;
    while (cur != NULL &&
           !(cur->protocol == IPPROTO_UDP &&
             cur->udp.version == version &&
             cur->udp.source == udphdr->source && cur->udp.dest == udphdr->dest &&
             (version == 4 ? cur->udp.saddr.ip4 == ip4->saddr &&
                             cur->udp.daddr.ip4 == ip4->daddr
                           : memcmp(&cur->udp.saddr.ip6, &ip6->ip6_src, 16) == 0 &&
                             memcmp(&cur->udp.daddr.ip6, &ip6->ip6_dst, 16) == 0)))
        cur = cur->next;

    char source[INET6_ADDRSTRLEN + 1];
    char dest[INET6_ADDRSTRLEN + 1];
    if (version == 4) {
        inet_ntop(AF_INET, &ip4->saddr, source, sizeof(source));
        inet_ntop(AF_INET, &ip4->daddr, dest, sizeof(dest));
    } else {
        inet_ntop(AF_INET6, &ip6->ip6_src, source, sizeof(source));
        inet_ntop(AF_INET6, &ip6->ip6_dst, dest, sizeof(dest));
    }

    if (cur != NULL && cur->udp.state != UDP_ACTIVE) {
        log_android(ANDROID_LOG_INFO, "UDP ignore session from %s/%u to %s/%u state %d",
                    source, ntohs(udphdr->source), dest, ntohs(udphdr->dest), cur->udp.state);
        return 0;
    }

    // Create new session if needed
    if (cur == NULL) {
        log_android(ANDROID_LOG_INFO, "UDP new session from %s/%u to %s/%u",
                    source, ntohs(udphdr->source), dest, ntohs(udphdr->dest));

        // Register session
        struct ng_session *s = malloc(sizeof(struct ng_session));
        s->protocol = IPPROTO_UDP;

        s->udp.time = time(NULL);
        s->udp.uid = uid;
        s->udp.version = version;

        int rversion;
        rversion = s->udp.version;
        s->udp.mss = (uint16_t) (rversion == 4 ? UDP4_MAXMSG : UDP6_MAXMSG);

        s->udp.sent = 0;
        s->udp.received = 0;

        if (version == 4) {
            s->udp.saddr.ip4 = (__be32) ip4->saddr;
            s->udp.daddr.ip4 = (__be32) ip4->daddr;
        } else {
            memcpy(&s->udp.saddr.ip6, &ip6->ip6_src, 16);
            memcpy(&s->udp.daddr.ip6, &ip6->ip6_dst, 16);
        }

        s->udp.source = udphdr->source;
        s->udp.dest = udphdr->dest;
        s->udp.state = UDP_ACTIVE;
        s->next = NULL;

        // Open UDP socket
        s->socket = open_udp_socket(args, &s->udp, 0);
        if (s->socket < 0) {
            free(s);
            return 0;
        }

        log_android(ANDROID_LOG_DEBUG, "UDP socket %d", s->socket);

        // Monitor events
        memset(&s->ev, 0, sizeof(struct epoll_event));
        s->ev.events = EPOLLIN | EPOLLERR;
        s->ev.data.ptr = s;
        if (epoll_ctl(epoll_fd, EPOLL_CTL_ADD, s->socket, &s->ev))
            log_android(ANDROID_LOG_ERROR, "epoll add udp error %d: %s", errno, strerror(errno));

        s->next = ng_session;
        ng_session = s;

        cur = s;
    }

    // Check for DNS
    if (ntohs(udphdr->dest) == 53) {
        char qname[DNS_QNAME_MAX + 1];
        uint16_t qtype;
        uint16_t qclass;
        if (get_dns_query(args, &cur->udp, data, datalen, &qtype, &qclass, qname) >= 0) {
            log_android(ANDROID_LOG_DEBUG,
                        "DNS query qtype %d qclass %d name %s",
                        qtype, qclass, qname);

            if (0)
                if (check_domain(args, &cur->udp, data, datalen, qclass, qtype, qname)) {
                    // Log qname
                    char name[DNS_QNAME_MAX + 40 + 1];
                    sprintf(name, "qtype %d qname %s", qtype, qname);
                    jobject objPacket = create_packet(
                            args, version, IPPROTO_UDP, "",
                            source, ntohs(cur->udp.source), dest, ntohs(cur->udp.dest),
                            name, 0, 0);
                    log_packet(args, objPacket);

                    // Session done
                    cur->udp.state = UDP_FINISHING;
                    return 0;
                }
        }
    }

    // Check for DHCP (tethering)
    if (ntohs(udphdr->source) == 68 || ntohs(udphdr->dest) == 67) {
        if (check_dhcp(args, &cur->udp, data, datalen) >= 0)
            return 1;
    }

    log_android(ANDROID_LOG_INFO, "UDP forward from tun %s/%u to %s/%u data %d",
                source, ntohs(udphdr->source), dest, ntohs(udphdr->dest), datalen);

    cur->udp.time = time(NULL);

    int rversion;
    struct sockaddr_in addr4;
    struct sockaddr_in6 addr6;

    rversion = cur->udp.version;
    if (cur->udp.version == 4) {
        addr4.sin_family = AF_INET;
        addr4.sin_addr.s_addr = (__be32) cur->udp.daddr.ip4;
        addr4.sin_port = cur->udp.dest;
    } else {
        addr6.sin6_family = AF_INET6;
        memcpy(&addr6.sin6_addr, &cur->udp.daddr.ip6, 16);
        addr6.sin6_port = cur->udp.dest;
    }

    if (sendto(cur->socket, data, (socklen_t) datalen, MSG_NOSIGNAL,
               (const struct sockaddr *) (rversion == 4 ? &addr4 : &addr6),
               (socklen_t) (rversion == 4 ? sizeof(addr4) : sizeof(addr6))) != datalen) {
        log_android(ANDROID_LOG_ERROR, "UDP sendto error %d: %s", errno, strerror(errno));
        if (errno != EINTR && errno != EAGAIN) {
            cur->udp.state = UDP_FINISHING;
            return 0;
        }
    } else
        cur->udp.sent += datalen;

    return 1;
}

int open_udp_socket(const struct arguments *args,
                    const struct udp_session *cur, const struct allowed *redirect) {
    int sock;
    int version;
    if (redirect == NULL)
        version = cur->version;
    else
        version = (strstr(redirect->raddr, ":") == NULL ? 4 : 6);

    // Get UDP socket
    sock = socket(version == 4 ? PF_INET : PF_INET6, SOCK_DGRAM, IPPROTO_UDP);
    if (sock < 0) {
        log_android(ANDROID_LOG_ERROR, "UDP socket error %d: %s", errno, strerror(errno));
        return -1;
    }

    // Protect socket
    if (protect_socket(args, sock) < 0)
        return -1;

    // Check for broadcast/multicast
    if (cur->version == 4) {
        uint32_t broadcast4 = INADDR_BROADCAST;
        if (memcmp(&cur->daddr.ip4, &broadcast4, sizeof(broadcast4)) == 0) {
            log_android(ANDROID_LOG_WARN, "UDP4 broadcast");
            int on = 1;
            if (setsockopt(sock, SOL_SOCKET, SO_BROADCAST, &on, sizeof(on)))
                log_android(ANDROID_LOG_ERROR, "UDP setsockopt SO_BROADCAST error %d: %s",
                            errno, strerror(errno));
        }
    } else {
        // http://man7.org/linux/man-pages/man7/ipv6.7.html
        if (*((uint8_t *) &cur->daddr.ip6) == 0xFF) {
            log_android(ANDROID_LOG_WARN, "UDP6 broadcast");

            int loop = 1; // true
            if (setsockopt(sock, IPPROTO_IPV6, IPV6_MULTICAST_LOOP, &loop, sizeof(loop)))
                log_android(ANDROID_LOG_ERROR,
                            "UDP setsockopt IPV6_MULTICAST_LOOP error %d: %s",
                            errno, strerror(errno));

            int ttl = -1; // route default
            if (setsockopt(sock, IPPROTO_IPV6, IPV6_MULTICAST_HOPS, &ttl, sizeof(ttl)))
                log_android(ANDROID_LOG_ERROR,
                            "UDP setsockopt IPV6_MULTICAST_HOPS error %d: %s",
                            errno, strerror(errno));

            struct ipv6_mreq mreq6;
            memcpy(&mreq6.ipv6mr_multiaddr, &cur->daddr.ip6, sizeof(struct in6_addr));
            mreq6.ipv6mr_interface = INADDR_ANY;
            if (setsockopt(sock, IPPROTO_IPV6, IPV6_ADD_MEMBERSHIP, &mreq6, sizeof(mreq6)))
                log_android(ANDROID_LOG_ERROR,
                            "UDP setsockopt IPV6_ADD_MEMBERSHIP error %d: %s",
                            errno, strerror(errno));
        }
    }

    return sock;
}

ssize_t write_udp(const struct arguments *args, const struct udp_session *cur,
                  uint8_t *data, size_t datalen) {
    size_t len;
    u_int8_t *buffer;
    struct udphdr *udp;
    uint16_t csum;
    char source[INET6_ADDRSTRLEN + 1];
    char dest[INET6_ADDRSTRLEN + 1];

    // Build packet
    if (cur->version == 4) {
        len = sizeof(struct iphdr) + sizeof(struct udphdr) + datalen;
        buffer = malloc(len);
        struct iphdr *ip4 = (struct iphdr *) buffer;
        udp = (struct udphdr *) (buffer + sizeof(struct iphdr));
        if (datalen)
            memcpy(buffer + sizeof(struct iphdr) + sizeof(struct udphdr), data, datalen);

        // Build IP4 header
        memset(ip4, 0, sizeof(struct iphdr));
        ip4->version = 4;
        ip4->ihl = sizeof(struct iphdr) >> 2;
        ip4->tot_len = htons(len);
        ip4->ttl = IPDEFTTL;
        ip4->protocol = IPPROTO_UDP;
        ip4->saddr = cur->daddr.ip4;
        ip4->daddr = cur->saddr.ip4;

        // Calculate IP4 checksum
        ip4->check = ~calc_checksum(0, (uint8_t *) ip4, sizeof(struct iphdr));

        // Calculate UDP4 checksum
        struct ippseudo pseudo;
        memset(&pseudo, 0, sizeof(struct ippseudo));
        pseudo.ippseudo_src.s_addr = (__be32) ip4->saddr;
        pseudo.ippseudo_dst.s_addr = (__be32) ip4->daddr;
        pseudo.ippseudo_p = ip4->protocol;
        pseudo.ippseudo_len = htons(sizeof(struct udphdr) + datalen);

        csum = calc_checksum(0, (uint8_t *) &pseudo, sizeof(struct ippseudo));
    } else {
        len = sizeof(struct ip6_hdr) + sizeof(struct udphdr) + datalen;
        buffer = malloc(len);
        struct ip6_hdr *ip6 = (struct ip6_hdr *) buffer;
        udp = (struct udphdr *) (buffer + sizeof(struct ip6_hdr));
        if (datalen)
            memcpy(buffer + sizeof(struct ip6_hdr) + sizeof(struct udphdr), data, datalen);

        // Build IP6 header
        memset(ip6, 0, sizeof(struct ip6_hdr));
        ip6->ip6_ctlun.ip6_un1.ip6_un1_flow = 0;
        ip6->ip6_ctlun.ip6_un1.ip6_un1_plen = htons(len - sizeof(struct ip6_hdr));
        ip6->ip6_ctlun.ip6_un1.ip6_un1_nxt = IPPROTO_UDP;
        ip6->ip6_ctlun.ip6_un1.ip6_un1_hlim = IPDEFTTL;
        ip6->ip6_ctlun.ip6_un2_vfc = IPV6_VERSION;
        memcpy(&(ip6->ip6_src), &cur->daddr.ip6, 16);
        memcpy(&(ip6->ip6_dst), &cur->saddr.ip6, 16);

        // Calculate UDP6 checksum
        struct ip6_hdr_pseudo pseudo;
        memset(&pseudo, 0, sizeof(struct ip6_hdr_pseudo));
        memcpy(&pseudo.ip6ph_src, &ip6->ip6_dst, 16);
        memcpy(&pseudo.ip6ph_dst, &ip6->ip6_src, 16);
        pseudo.ip6ph_len = ip6->ip6_ctlun.ip6_un1.ip6_un1_plen;
        pseudo.ip6ph_nxt = ip6->ip6_ctlun.ip6_un1.ip6_un1_nxt;

        csum = calc_checksum(0, (uint8_t *) &pseudo, sizeof(struct ip6_hdr_pseudo));
    }

    // Build UDP header
    memset(udp, 0, sizeof(struct udphdr));
    udp->source = cur->dest;
    udp->dest = cur->source;
    udp->len = htons(sizeof(struct udphdr) + datalen);

    // Continue checksum
    csum = calc_checksum(csum, (uint8_t *) udp, sizeof(struct udphdr));
    csum = calc_checksum(csum, data, datalen);
    udp->check = ~csum;

    inet_ntop(cur->version == 4 ? AF_INET : AF_INET6,
              cur->version == 4 ? &cur->saddr.ip4 : &cur->saddr.ip6, source, sizeof(source));
    inet_ntop(cur->version == 4 ? AF_INET : AF_INET6,
              cur->version == 4 ? &cur->daddr.ip4 : &cur->daddr.ip6, dest, sizeof(dest));

    // Send packet
    log_android(ANDROID_LOG_DEBUG,
                "UDP sending to tun %d from %s/%u to %s/%u data %u",
                args->tun, dest, ntohs(cur->dest), source, ntohs(cur->source), len);

    ssize_t res = write(args->tun, buffer, len);

    free(buffer);

    if (res != len) {
        log_android(ANDROID_LOG_ERROR, "write %d/%d", res, len);
        return -1;
    }

    return res;
}