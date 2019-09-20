//
// Created by Comet on 8/30/2019.
//

#ifndef ANDROIDSTREAMING_STREAMING_LIB_H
#define ANDROIDSTREAMING_STREAMING_LIB_H

#include <stdlib.h>
#include <string.h>
#include <jni.h>
#include <math.h>

#endif //ANDROIDSTREAMING_STREAMING_LIB_H

JNIEXPORT jint JNICALL
Java_com_comet_androidstreaming_http_CustomHttpDataSource_readInternalNative(JNIEnv *env,
                                                                             jobject instance,
                                                                             jbyteArray buffer,
                                                                             jint offset,
                                                                             jint readLength);
JNIEXPORT void JNICALL
Java_com_comet_androidstreaming_http_CustomHttpDataSource_skipInternalNative(JNIEnv *env,
                                                                             jobject instance);
jint throwException( JNIEnv *env, char *className, char *message );
