/*
 * Copyright (c) 2018-2025 Alexander Yaburov
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

#ifndef KNOCKONPORTS_NETUTIL_H
#define KNOCKONPORTS_NETUTIL_H

#include <jni.h>

extern "C"
JNIEXPORT jint JNICALL Java_me_impa_knockonports_service_Knocker_ping(
        JNIEnv *, jobject __unused, jstring, jint, jint, jint, jbyteArray, jint);

extern "C"
JNIEXPORT jint JNICALL Java_me_impa_knockonports_service_Knocker_ping6(
        JNIEnv *, jobject __unused, jstring, jint, jint, jint, jbyteArray, jint);

extern "C"
JNIEXPORT jint JNICALL Java_me_impa_knockonports_service_Knocker_sendtcp(
        JNIEnv *, jobject __unused, jstring, jint, jint);

extern "C"
JNIEXPORT jint JNICALL Java_me_impa_knockonports_service_Knocker_sendtcp6(
        JNIEnv *, jobject __unused, jstring, jint, jint);

extern "C"
JNIEXPORT jint JNICALL Java_me_impa_knockonports_service_Knocker_sendudp(
        JNIEnv *, jobject __unused, jstring, jint, jint, jint, jbyteArray, jint);

extern "C"
JNIEXPORT jint JNICALL Java_me_impa_knockonports_service_Knocker_sendudp6(
        JNIEnv *, jobject __unused, jstring, jint, jint, jint, jbyteArray, jint);

#endif //KNOCKONPORTS_NETUTIL_H
