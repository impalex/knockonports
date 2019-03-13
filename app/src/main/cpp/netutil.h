//
// Created by impa on 07.11.2018.
//

#ifndef KNOCKONPORTS_NETUTIL_H
#define KNOCKONPORTS_NETUTIL_H

#include <jni.h>

extern "C"
JNIEXPORT jint JNICALL Java_me_impa_knockonports_service_Knocker_ping(
        JNIEnv *, jobject __unused, jstring, jint, jint, jbyteArray, jint);

extern "C"
JNIEXPORT jint JNICALL Java_me_impa_knockonports_service_Knocker_sendtcp(
        JNIEnv *, jobject __unused, jstring, jint);

#endif //KNOCKONPORTS_NETUTIL_H
