//
// Created by impa on 07.11.2018.
//

#include "icmputil.h"

#include <stdlib.h>
#include <stdio.h>
#include <errno.h>
#include <linux/icmp.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <android/log.h>
#include <jni.h>
#include <cstring>
#include <algorithm>
#include <unistd.h>
#include <fcntl.h>

int ping(const char *host, const int size, const int count, jbyte* pattern, jsize pattern_len, const int sleep) {

    struct sockaddr_in addr;
    struct icmphdr icmp_header;
    int packet_size = size;
    if (packet_size < sizeof(icmp_header))
        packet_size = sizeof(icmp_header);

    __android_log_print(ANDROID_LOG_INFO, "ICMP", "hitting %s", host);

    char packet_data[packet_size];

    memset(&addr, 0, sizeof(addr));

    if (pattern_len > 0) {
        // fill
        for (int i = sizeof(icmp_header); i < packet_size; i = i + pattern_len) {
            memcpy(packet_data+i, pattern, (size_t) std::min(pattern_len, packet_size - i));
        }
    } else {
        memset(packet_data, 0, sizeof(packet_data));
    }

    addr.sin_family = AF_INET;

    if (inet_pton(AF_INET, host, &(addr.sin_addr)) < 0) {
        __android_log_print(ANDROID_LOG_ERROR, "ICMP", "inet_pton errono %d %s\n", errno,
                            strerror(errno));
        return EXIT_FAILURE;
    }

    int sock = socket(AF_INET, SOCK_DGRAM, IPPROTO_ICMP);

    if (sock < 0) {
        __android_log_print(ANDROID_LOG_ERROR, "ICMP", "socket errorno %d %s\n", errno,
                            strerror(errno));
        return EXIT_FAILURE;
    }

    memset(&icmp_header, 0, sizeof(icmp_header));
    icmp_header.type = ICMP_ECHO;
    icmp_header.un.echo.id = 1337;

    for(int i = 1; i<=count; i++) {
        __android_log_print(ANDROID_LOG_INFO, "ICMP", "icmp ping\n");
        icmp_header.un.echo.sequence = (__be16) i;
        memcpy(packet_data, &icmp_header, sizeof(icmp_header));
        if (sendto(sock, packet_data, sizeof(packet_data), 0, (struct sockaddr*) &addr, sizeof(addr)) < 0) {
            __android_log_print(ANDROID_LOG_ERROR, "ICMP", "sendto errno %d %s\n", errno, strerror(errno));
            return EXIT_FAILURE;

        }
        fd_set read_set;
        int rc;
        struct timeval tout = { 2, 0 };
        memset(&read_set, 0, sizeof(read_set));
        FD_SET(sock, &read_set);
        rc = select(sock+1, &read_set, NULL, NULL, &tout);
        if (rc == 0) {
            __android_log_print(ANDROID_LOG_WARN, "ICMP", "no reply in 2 secs\n");
        } else if (rc<0) {
            __android_log_print(ANDROID_LOG_ERROR, "ICMP", "select errno %d %s\n", errno, strerror(errno));
        }
        usleep(static_cast<useconds_t>(sleep * 1000));
    }
    close(sock);

    return EXIT_SUCCESS;
}

int send_tcp_packet(const char *host, const int port) {
    struct sockaddr_in addr;
    int flags;

    __android_log_print(ANDROID_LOG_INFO, "TCP", "hitting %s:%d", host, port);

    memset(&addr, 0, sizeof(addr));

    addr.sin_family = AF_INET;
    addr.sin_port = htons(port);

    if (inet_pton(AF_INET, host, &(addr.sin_addr)) < 0) {
        __android_log_print(ANDROID_LOG_ERROR, "TCP", "inet_pton errono %d %s\n", errno,
                            strerror(errno));
        return EXIT_FAILURE;
    }

    int sock = socket(PF_INET, SOCK_STREAM, 0);
    if (sock < 0) {
        __android_log_print(ANDROID_LOG_ERROR, "TCP", "socket errorno %d %s\n", errno,
                            strerror(errno));
        return EXIT_FAILURE;
    }
    flags = fcntl(sock, F_GETFL, 0);
    fcntl(sock, F_SETFL, flags | O_NONBLOCK);

    connect(sock, (struct sockaddr*)&addr, sizeof(struct sockaddr));
    close(sock);

    return EXIT_SUCCESS;
}

jint Java_me_impa_knockonports_service_Knocker_ping(JNIEnv *env, jobject thiz, jstring address, jint size, jint count, jbyteArray pattern, jint sleep) {
    const char *n_address = env->GetStringUTFChars(address, 0);

    jbyte *const n_pattern = env->GetByteArrayElements(pattern, 0);
    const jsize patLen = env->GetArrayLength(pattern);

    int result = ping(n_address, size, count, n_pattern, patLen, sleep);

    (*env).ReleaseStringUTFChars(address, n_address);
    (*env).ReleaseByteArrayElements(pattern, n_pattern, JNI_ABORT);

    return result;
}

jint Java_me_impa_knockonports_service_Knocker_sendtcp(JNIEnv *env, jobject thiz, jstring host, jint port) {
    const char *n_host = env->GetStringUTFChars(host, 0);

    int result = send_tcp_packet(n_host, port);

    (*env).ReleaseStringUTFChars(host, n_host);

    return result;
}