//
// Created by Louis Wu on 2023/10/28.
//

#ifndef CHAFENQI_HTTP_H
#define CHAFENQI_HTTP_H

#include <stdint.h>

int get_header(const char *header, const char *data, size_t data_len, char *value);
int next_header(const char **data, size_t *len);
uint8_t *find_data(uint8_t *data, size_t data_len, char *value);

uint8_t *patch_http_url(uint8_t *data, size_t *data_len);

#endif //CHAFENQI_HTTP_H
