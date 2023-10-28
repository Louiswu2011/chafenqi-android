//
// Created by Louis Wu on 2023/10/28.
//

#ifndef CHAFENQI_TLS_H
#define CHAFENQI_TLS_H

#include <stdint.h>

void parse_tls_header(const char *data, size_t data_len, char *hostname);

#endif //CHAFENQI_TLS_H
