//
// Created by impa on 07.11.2018.
//

#ifndef KNOCKONPORTS_ICMPUTIL_H
#define KNOCKONPORTS_ICMPUTIL_H

#include <jni.h>

extern "C"
JNIEXPORT jint JNICALL Java_me_impa_knockonports_service_Knocker_ping(
        JNIEnv *, jobject, jstring, jint, jint, jbyteArray, jint);

#endif //KNOCKONPORTS_ICMPUTIL_H
