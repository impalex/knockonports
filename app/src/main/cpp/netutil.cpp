//
// Created by impa on 07.11.2018.
//

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

int ping(int family, const char *host, const int size, const int count, jbyte* pattern, jsize pattern_len, const int sleep) {

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
        init_icmp4_header(&packet_data);
    } else {
        if (init_addr_v6(&addr, &addr_size, host, DUMMY_PORT) == EXIT_FAILURE)
            return EXIT_FAILURE;
        sock = socket(family, SOCK_DGRAM, IPPROTO_ICMPV6);
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

int send_tcp_packet(int family, const char *host, const int port) {
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
    connect(sock, (struct sockaddr *)addr, addr_size); // NOLINT(bugprone-unused-return-value)
    shutdown(sock, SHUT_RDWR);
    close(sock);

    free(addr);
    return EXIT_SUCCESS;

}

extern "C" jint Java_me_impa_knockonports_service_Knocker_ping(JNIEnv *env, jobject  __unused thiz, jstring address, jint size, jint count, jbyteArray pattern, jint sleep) {
    const char *n_address = env->GetStringUTFChars(address, NULL);

    jbyte *const n_pattern = env->GetByteArrayElements(pattern, NULL);
    const jsize patLen = env->GetArrayLength(pattern);

    int result = ping(AF_INET, n_address, size, count, n_pattern, patLen, sleep);

    (*env).ReleaseStringUTFChars(address, n_address);
    (*env).ReleaseByteArrayElements(pattern, n_pattern, JNI_ABORT);

    return result;
}

extern "C" jint Java_me_impa_knockonports_service_Knocker_ping6(JNIEnv *env, jobject  __unused thiz, jstring address, jint size, jint count, jbyteArray pattern, jint sleep) {
    const char *n_address = env->GetStringUTFChars(address, NULL);

    jbyte *const n_pattern = env->GetByteArrayElements(pattern, NULL);
    const jsize patLen = env->GetArrayLength(pattern);

    int result = ping(AF_INET6, n_address, size, count, n_pattern, patLen, sleep);

    (*env).ReleaseStringUTFChars(address, n_address);
    (*env).ReleaseByteArrayElements(pattern, n_pattern, JNI_ABORT);

    return result;
}

extern "C" jint Java_me_impa_knockonports_service_Knocker_sendtcp(JNIEnv *env, jobject  __unused thiz, jstring host, jint port) {
    const char *n_host = env->GetStringUTFChars(host, NULL);

    int result = send_tcp_packet(AF_INET, n_host, port);

    (*env).ReleaseStringUTFChars(host, n_host);

    return result;
}

extern "C" jint Java_me_impa_knockonports_service_Knocker_sendtcp6(JNIEnv *env, jobject  __unused thiz, jstring host, jint port) {
    const char *n_host = env->GetStringUTFChars(host, NULL);

    int result = send_tcp_packet(AF_INET6, n_host, port);

    (*env).ReleaseStringUTFChars(host, n_host);

    return result;
}
