/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

#pragma clang diagnostic push
#pragma ide diagnostic ignored "hicpp-member-init"
#pragma ide diagnostic ignored "cppcoreguidelines-pro-type-member-init"
#pragma ide diagnostic ignored "modernize-use-nullptr"

#include "netutil.h"

#include <cstdlib>
#include <cstdio>
#include <cerrno>
#include <linux/icmp.h>
#include <linux/icmpv6.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <android/log.h>
#include <jni.h>
#include <cstring>
#include <algorithm>
#include <unistd.h>

const int DUMMY_PORT = 1337;

int init_addr_v4(void **addr_buf, int *addr_len, const char *host, const int port) {
    struct sockaddr_in *addr;
    addr = (struct sockaddr_in*)calloc(1, sizeof(struct sockaddr_in));
    *addr_buf = addr;
    *addr_len = sizeof(struct sockaddr_in);

    addr->sin_family = AF_INET;
    addr->sin_port = htons(port);
    if (inet_pton(AF_INET, host, &addr->sin_addr)<0) {
        __android_log_print(ANDROID_LOG_ERROR, "ADDR", "inet_pton errono %d %s", errno,
                            strerror(errno));
        free(addr);
        return EXIT_FAILURE;
    }
    return EXIT_SUCCESS;
}

int init_addr_v6(void **addr_buf, int *addr_len, const char *host, const int port) {
    struct sockaddr_in6 *addr;
    addr = (struct sockaddr_in6*)calloc(1, sizeof(struct sockaddr_in6));
    *addr_buf = addr;
    *addr_len = sizeof(struct sockaddr_in6);

    addr->sin6_family = AF_INET6;
    addr->sin6_port = htons(port);
    if (inet_pton(AF_INET6, host, &(addr->sin6_addr))<0) {
        __android_log_print(ANDROID_LOG_ERROR, "ADDR", "inet_pton errono %d %s", errno,
                            strerror(errno));
        free(addr);
        return EXIT_FAILURE;
    }
    return EXIT_SUCCESS;
}

void init_icmp4_header(void *buf) {
    auto *hdr = (icmphdr *)buf;
    hdr->type = ICMP_ECHO;
    hdr->code = 0;
    hdr->checksum = 0;
    hdr->un.echo.id = (unsigned short)getpid();
    hdr->un.echo.sequence = 0;
}

void init_icmp6_header(void *buf) {
    auto *hdr = (icmp6hdr *)buf;
    hdr->icmp6_type = ICMPV6_ECHO_REQUEST;
    hdr->icmp6_code = 0;
    hdr->icmp6_cksum = 0;
    hdr->icmp6_dataun.u_echo.identifier = (unsigned short)getpid();
    hdr->icmp6_dataun.u_echo.sequence = 0;
}

void update_sequence(const char *buf, int family) {
    if (family == AF_INET) {
        ((icmphdr *)buf)->un.echo.sequence++;
    } else if (family == AF_INET6) {
        ((icmp6hdr *)buf)->icmp6_dataun.u_echo.sequence++;
    }
}

int ping(int family, const char *host, const int ttl, const int size, const int count, jbyte* pattern, jsize pattern_len, const int sleep) {

    void *addr;
    int hdr_size, addr_size;

    if (family == AF_INET) {
        hdr_size = sizeof(icmphdr);
    } else if (family == AF_INET6) {
        hdr_size = sizeof(icmp6hdr);
    } else {
        __android_log_print(ANDROID_LOG_ERROR, "ICMP", "invalid family %d", family);
        return EXIT_FAILURE;
    }

    int packet_size = size;
    if (packet_size < hdr_size)
        packet_size = hdr_size;

    __android_log_print(ANDROID_LOG_INFO, "ICMP", "hitting %s", host);

    char packet_data[packet_size];

    memset(packet_data, 0, sizeof(packet_data));
    if (pattern_len > 0) {
        for (int i = hdr_size; i<packet_size; i = i + pattern_len) {
            // fill by pattern
            memcpy(packet_data+i, pattern, (size_t) std::min(pattern_len, packet_size - i));
        }
    }

    int sock;
    if (family == AF_INET) {
        if (init_addr_v4(&addr, &addr_size, host, DUMMY_PORT) == EXIT_FAILURE)
            return EXIT_FAILURE;
        sock = socket(family, SOCK_DGRAM, IPPROTO_ICMP);
        if (ttl > 0 && ttl <= 255) {
            if (setsockopt(sock, IPPROTO_IP, IP_TTL, &ttl, sizeof(ttl)) < 0) {
                __android_log_print(ANDROID_LOG_ERROR, "ICMP", "setsockopt errono %d %s", errno,
                                    strerror(errno));
            }
        }
        init_icmp4_header(&packet_data);
    } else {
        if (init_addr_v6(&addr, &addr_size, host, DUMMY_PORT) == EXIT_FAILURE)
            return EXIT_FAILURE;
        sock = socket(family, SOCK_DGRAM, IPPROTO_ICMPV6);
        if (ttl> 0 && ttl <= 255) {
            if (setsockopt(sock, IPPROTO_IPV6, IPV6_UNICAST_HOPS, &ttl, sizeof(ttl)) < 0) {
                __android_log_print(ANDROID_LOG_ERROR, "ICMP", "setsockopt errono %d %s", errno,
                                    strerror(errno));
            }
        }
        init_icmp6_header(&packet_data);
    }

    if (sock < 0) {
        __android_log_print(ANDROID_LOG_ERROR, "ICMP", "socket errorno %d %s", errno,
                            strerror(errno));
        free(addr);
        return EXIT_FAILURE;
    }

    for (int i = 1; i<=count; i++) {
        __android_log_print(ANDROID_LOG_INFO, "ICMP", "icmp ping");
        update_sequence(packet_data, family);
        // NOTE no need to calc checksum

        if (sendto(sock, packet_data, sizeof(packet_data), 0, (struct sockaddr*)addr, addr_size)<0) {
            __android_log_print(ANDROID_LOG_ERROR, "ICMP", "sendto errno %d %s", errno, strerror(errno));
            free(addr);
            return EXIT_FAILURE;
        }

        usleep(static_cast<useconds_t>(sleep*1000));
    }
    close(sock);

    free(addr);
    return EXIT_SUCCESS;
}

int send_tcp_packet(int family, const char *host, const int ttl, const int port) {
    void *addr;
    int addr_size;

    __android_log_print(ANDROID_LOG_INFO, "TCP", "hitting %s:%d", host, port);

    memset(&addr, 0, sizeof(addr));

    if (family == AF_INET) {
        if (init_addr_v4(&addr, &addr_size, host, port) == EXIT_FAILURE)
            return EXIT_FAILURE;
    } else if (family == AF_INET6) {
        if (init_addr_v6(&addr, &addr_size, host, port) == EXIT_FAILURE)
            return EXIT_FAILURE;
    } else {
        __android_log_print(ANDROID_LOG_ERROR, "TCP", "invalid family %d", family);
        return EXIT_FAILURE;
    }

    int sock = socket(family, SOCK_STREAM | SOCK_NONBLOCK, IPPROTO_TCP); // NOLINT(android-cloexec-socket,hicpp-signed-bitwise)
    if (sock < 0) {
        __android_log_print(ANDROID_LOG_ERROR, "TCP", "socket errorno %d %s\n", errno,
                            strerror(errno));
        free(addr);
        return EXIT_FAILURE;
    }
    if (ttl > 0 && ttl <= 255) {
        if (family == AF_INET) {
            if (setsockopt(sock, IPPROTO_IP, IP_TTL, &ttl, sizeof(ttl)) < 0) {
                __android_log_print(ANDROID_LOG_ERROR, "TCP", "setsockopt errono %d %s", errno,
                                    strerror(errno));
            }
        } else {
            if (setsockopt(sock, IPPROTO_IPV6, IPV6_UNICAST_HOPS, &ttl, sizeof(ttl)) < 0) {
                __android_log_print(ANDROID_LOG_ERROR, "TCP", "setsockopt errono %d %s", errno,
                                    strerror(errno));
            }
        }
    }
    connect(sock, (struct sockaddr *)addr, addr_size); // NOLINT(bugprone-unused-return-value)
    shutdown(sock, SHUT_RDWR);
    close(sock);

    free(addr);
    return EXIT_SUCCESS;

}

int send_udp_packet(int family, const char *host, const int ttl, const int port, const int local_port, const char *data, int data_len) {
    void *addr;
    int addr_size;

    __android_log_print(ANDROID_LOG_INFO, "UDP", "Sending UDP to %s:%d", host, port);

    if (family == AF_INET) {
        if (init_addr_v4(&addr, &addr_size, host, port) == EXIT_FAILURE)
            return EXIT_FAILURE;
    } else if (family == AF_INET6) {
        if (init_addr_v6(&addr, &addr_size, host, port) == EXIT_FAILURE)
            return EXIT_FAILURE;
    } else {
        __android_log_print(ANDROID_LOG_ERROR, "UDP", "invalid family %d", family);
        return EXIT_FAILURE;
    }

    int sock = socket(family, SOCK_DGRAM, IPPROTO_UDP);
    if (sock < 0) {
        __android_log_print(ANDROID_LOG_ERROR, "UDP", "socket errorno %d %s\n", errno,
                            strerror(errno));
        free(addr);
        return EXIT_FAILURE;
    }

    int optval = 1;
    setsockopt(sock, SOL_SOCKET, SO_REUSEPORT, &optval, sizeof(optval));

    if (ttl > 0 && ttl <= 255) {
        if (family == AF_INET) {
            if (setsockopt(sock, IPPROTO_IP, IP_TTL, &ttl, sizeof(ttl)) < 0) {
                __android_log_print(ANDROID_LOG_ERROR, "UDP", "setsockopt errono %d %s", errno,
                                    strerror(errno));
            }
        } else {
            if (setsockopt(sock, IPPROTO_IPV6, IPV6_UNICAST_HOPS, &ttl, sizeof(ttl)) < 0) {
                __android_log_print(ANDROID_LOG_ERROR, "UDP", "setsockopt errono %d %s", errno,
                                    strerror(errno));
            }
        }
    }

    // Bind to the local port if it's valid
    if (local_port > 0 && local_port <= 65535) {
        struct sockaddr_in local_addr;
        struct sockaddr_in6 local_addr6;
        memset(&local_addr, 0, sizeof(local_addr));  // Important: Initialize to zero
        memset(&local_addr6, 0, sizeof(local_addr6));

        if (family == AF_INET) {
            local_addr.sin_family = AF_INET;
            local_addr.sin_port = htons(local_port);
            local_addr.sin_addr.s_addr = INADDR_ANY; // Allow binding to any local address

            if (bind(sock, (struct sockaddr *)&local_addr, sizeof(local_addr)) < 0) {
                __android_log_print(ANDROID_LOG_WARN, "UDP", "Failed to bind to local port %d: errno %d %s. Using system-assigned port.", local_port, errno, strerror(errno));
                // Continue without binding, letting the system assign a port.
            } else {
                __android_log_print(ANDROID_LOG_INFO, "UDP", "Bound to local port %d", local_port);
            }

        } else {
            local_addr6.sin6_family = AF_INET6;
            local_addr6.sin6_port = htons(local_port);
            local_addr6.sin6_addr = in6addr_any; // Allow binding to any local address

            if (bind(sock, (struct sockaddr *)&local_addr6, sizeof(local_addr6)) < 0) {
                __android_log_print(ANDROID_LOG_WARN, "UDP", "Failed to bind to local port %d: errno %d %s. Using system-assigned port.", local_port, errno, strerror(errno));
                // Continue without binding, letting the system assign a port.
            } else {
                __android_log_print(ANDROID_LOG_INFO, "UDP", "Bound to local port %d", local_port);
            }
        }
    } else {
        __android_log_print(ANDROID_LOG_WARN, "UDP", "Invalid local port %d. Using system-assigned port.", local_port);
    }

    ssize_t sent_bytes = sendto(sock, data, data_len, 0, (struct sockaddr *)addr, addr_size);
    if (sent_bytes < 0) {
        __android_log_print(ANDROID_LOG_ERROR, "UDP", "sendto errorno %d %s\n", errno,
                            strerror(errno));
        close(sock);
        free(addr);
        return EXIT_FAILURE;
    } else if (sent_bytes != data_len) {
        __android_log_print(ANDROID_LOG_WARN, "UDP", "sent %zd bytes, expected %d\n", sent_bytes, data_len);
    }

    close(sock);
    free(addr);
    return EXIT_SUCCESS;
}

extern "C" jint Java_me_impa_knockonports_service_sequence_Knocker_ping(JNIEnv *env, jobject  __unused thiz, jstring address, jint ttl, jint size, jint count, jbyteArray pattern, jint sleep) {
    const char *n_address = env->GetStringUTFChars(address, NULL);

    jbyte *const n_pattern = env->GetByteArrayElements(pattern, NULL);
    const jsize patLen = env->GetArrayLength(pattern);

    int result = ping(AF_INET, n_address, ttl, size, count, n_pattern, patLen, sleep);

    (*env).ReleaseStringUTFChars(address, n_address);
    (*env).ReleaseByteArrayElements(pattern, n_pattern, JNI_ABORT);

    return result;
}

extern "C" jint Java_me_impa_knockonports_service_sequence_Knocker_ping6(JNIEnv *env, jobject  __unused thiz, jstring host, jint ttl, jint size, jint count, jbyteArray pattern, jint sleep) {
    const char *n_host = env->GetStringUTFChars(host, NULL);

    jbyte *const n_pattern = env->GetByteArrayElements(pattern, NULL);
    const jsize patLen = env->GetArrayLength(pattern);

    int result = ping(AF_INET6, n_host, ttl, size, count, n_pattern, patLen, sleep);

    (*env).ReleaseStringUTFChars(host, n_host);
    (*env).ReleaseByteArrayElements(pattern, n_pattern, JNI_ABORT);

    return result;
}

extern "C" jint Java_me_impa_knockonports_service_sequence_Knocker_sendtcp(JNIEnv *env, jobject  __unused thiz, jstring host, jint ttl, jint port) {
    const char *n_host = env->GetStringUTFChars(host, NULL);

    int result = send_tcp_packet(AF_INET, n_host, ttl, port);

    (*env).ReleaseStringUTFChars(host, n_host);

    return result;
}

extern "C" jint Java_me_impa_knockonports_service_sequence_Knocker_sendtcp6(JNIEnv *env, jobject  __unused thiz, jstring host, jint ttl, jint port) {
    const char *n_host = env->GetStringUTFChars(host, NULL);

    int result = send_tcp_packet(AF_INET6, n_host, ttl, port);

    (*env).ReleaseStringUTFChars(host, n_host);

    return result;
}

extern "C" jint Java_me_impa_knockonports_service_sequence_Knocker_sendudp(JNIEnv *env, jobject  __unused thiz, jstring host, jint ttl, jint port, jint local_port, jbyteArray data) {
    const char *n_host = env->GetStringUTFChars(host, NULL);

    jbyte *const n_data = env->GetByteArrayElements(data, NULL);
    const jsize data_len = env->GetArrayLength(data);

    int result = send_udp_packet(AF_INET, n_host, ttl, port, local_port, (const char*)n_data, data_len);

    (*env).ReleaseStringUTFChars(host, n_host);
    (*env).ReleaseByteArrayElements(data, n_data, JNI_ABORT);

    return result;
}

extern "C" jint Java_me_impa_knockonports_service_sequence_Knocker_sendudp6(JNIEnv *env, jobject  __unused thiz, jstring host, jint ttl, jint port, jint local_port, jbyteArray data) {
    const char *n_host = env->GetStringUTFChars(host, NULL);

    jbyte *const n_data = env->GetByteArrayElements(data, NULL);
    const jsize data_len = env->GetArrayLength(data);

    int result = send_udp_packet(AF_INET6, n_host, ttl, port, local_port, (const char*)n_data, data_len);

    (*env).ReleaseStringUTFChars(host, n_host);
    (*env).ReleaseByteArrayElements(data, n_data, JNI_ABORT);

    return result;
}