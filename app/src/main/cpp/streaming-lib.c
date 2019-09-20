//
// Created by Comet on 8/30/2019.
//

#include "streaming-lib.h"

JNIEXPORT jstring JNICALL
Java_com_comet_androidstreaming_media_extractors_mp4_CustomMp4Extractor_readSampleNative( JNIEnv* env,
                                                  jobject thiz )
{
    return (*env)->NewStringUTF(env, "Hello from JNI !  Compiled with ABI");
}

JNIEXPORT void JNICALL
Java_com_comet_androidstreaming_http_CustomHttpDataSource_skipInternalNative(JNIEnv *env,
                                                                             jobject instance) {


    jclass this_class = (*env)->GetObjectClass(env, instance);

    jfieldID bytesSkipped_fID = (*env)->GetFieldID(env, this_class, "bytesSkipped", "J");
    jlong bytesSkipped = (*env)->GetLongField(env, instance, bytesSkipped_fID);

    jfieldID bytesToSkip_fID = (*env)->GetFieldID(env, this_class, "bytesToSkip", "J");
    jlong bytesToSkip = (*env)->GetLongField(env, instance, bytesToSkip_fID);

    if (bytesSkipped == bytesToSkip)
        return;

    jfieldID skipBufferReference_fID = (*env)->GetFieldID(env, this_class, "skipBufferReference", "Ljava/util/concurrent/atomic/AtomicReference;");
    jobject skipBufferReference = (*env)->GetObjectField(env, instance, skipBufferReference_fID);

    jclass skipBufferReference_class = (*env)->GetObjectClass(env, skipBufferReference);
    jmethodID getAndSet = (*env)->GetMethodID(env, skipBufferReference_class, "getAndSet", "([B)[B");
    jbyteArray skipBuffer = (*env)->CallObjectMethod(env, skipBufferReference, getAndSet, NULL);

    if (skipBuffer == NULL) {
        skipBuffer = (*env)->NewByteArray(env, 4096);
    }

    while (bytesSkipped != bytesToSkip) {
        int skipBuffer_length = (*env)->GetArrayLength(env, skipBuffer);
        int bytes_diff = bytesToSkip - bytesSkipped;
        int readLength = bytes_diff > skipBuffer_length ? skipBuffer_length : bytes_diff;

        jfieldID input_stream_fID = (*env)->GetFieldID(env, this_class, "inputStream", "Ljava/io/InputStream;");
        jobject input_stream = (*env)->GetObjectField(env, instance, input_stream_fID);

        jclass inputStream_class = (*env)->GetObjectClass(env, input_stream);
        jmethodID input_stream_read_fID = (*env)->GetMethodID(env, inputStream_class, "read", "([BII)I");

        jint read = (*env)->CallObjectMethod(env, input_stream, input_stream_read_fID, skipBuffer, 0, readLength);

        jclass thread = (*env)->FindClass(env, "java/lang/Thread");
        jmethodID thread_interrupt = (*env)->GetStaticMethodID(env, thread, "interrupted", "()V");
        jboolean thread_interrupted = (*env)->CallStaticBooleanMethod(env, thread, thread_interrupt);

        if (thread_interrupted) {
            throwException(env, "java/io/InterruptedIOException", "skip thread interrupted");
        }
        if (read == -1) {
            throwException(env, "java/io/EOFException", "skip thread EOFException");
        }
        bytesSkipped += read;
        (*env)->SetLongField(env, instance, bytesSkipped_fID, bytesSkipped);

        jfieldID listener_fID = (*env)->GetFieldID(env, this_class, "listener", "Lcom/google/android/exoplayer2/upstream/TransferListener;");
        jobject listener = (*env)->GetLongField(env, instance, listener_fID);

        if (listener != NULL) {
            jclass listener_class = (*env)->GetObjectClass(env, listener);
            jmethodID onBytesTransferred_fID = (*env)->GetMethodID(env, listener_class, "onBytesTransferred", "(Lcom/comet/androidstreaming/http/CustomHttpDataSource;I)V");

            (*env)->CallVoidMethod(env, listener, onBytesTransferred_fID, instance, read);

        }
    }

    // Release the shared skip buffer.
    jmethodID set_ID = (*env)->GetMethodID(env, skipBufferReference_class, "set", "([B)V");
    (*env)->CallVoidMethod(env, skipBufferReference_class, set_ID, skipBuffer);
}



jint Java_com_comet_androidstreaming_http_CustomHttpDataSource_readInternalNative(JNIEnv *env,
                                                                                  jobject instance,
                                                                                  jbyteArray buffer,
                                                                                  jint offset,
                                                                                  jint readLength) {



    jclass this_class = (*env)->GetObjectClass(env, instance);

    jfieldID bytesToRead_fID = (*env)->GetFieldID(env, this_class, "bytesToRead", "J");
    jlong bytesToRead = (*env)->GetLongField(env, instance, bytesToRead_fID);

    jfieldID bytesRead_fID = (*env)->GetFieldID(env, this_class, "bytesRead", "J");
    jlong bytesRead = (*env)->GetLongField(env, instance, bytesRead_fID);

    jfieldID input_stream_fID = (*env)->GetFieldID(env, this_class, "inputStream", "Ljava/io/InputStream;");
    jobject input_stream = (*env)->GetObjectField(env, instance, input_stream_fID);

    if (readLength == 0) {
        return 0;
    }

    jclass C_class = (*env)->FindClass(env, "com/google/android/exoplayer2/C");

    jfieldID length_fID = (*env)->GetStaticFieldID(env, C_class, "LENGTH_UNSET", "I");
    jint LENGTH_UNSET = (*env)->GetStaticIntField(env, C_class, length_fID);
    jfieldID result_fID = (*env)->GetStaticFieldID(env, C_class, "RESULT_END_OF_INPUT", "I");
    jint RESULT_END_OF_INPUT = (*env)->GetStaticIntField(env, C_class, result_fID);

    if (bytesToRead != LENGTH_UNSET) {
        long bytesRemaining = bytesToRead - bytesRead;
        if (bytesRemaining == 0) {
            return RESULT_END_OF_INPUT;
        }
        readLength = readLength > (jint) bytesRemaining ? (jint) bytesRemaining : readLength;
    }

    jclass inputStream_class = (*env)->GetObjectClass(env, input_stream);
    jmethodID input_stream_read_fID = (*env)->GetMethodID(env, inputStream_class, "read", "([BII)I");

    jint read = (*env)->CallIntMethod(env, input_stream, input_stream_read_fID, buffer, offset, readLength);

    if (read == -1) {
        if (bytesToRead != LENGTH_UNSET) {
            // End of stream reached having not read sufficient data.
            throwException(env, "java/io/EOFException", "skip thread EOFException");
        }
        return RESULT_END_OF_INPUT;
    }

    bytesRead += read;
    (*env)->SetLongField(env, instance, bytesRead_fID, bytesRead);

    jfieldID listener_fID = (*env)->GetFieldID(env, this_class, "listener", "Lcom/google/android/exoplayer2/upstream/TransferListener;");
    jobject listener = (*env)->GetObjectField(env, instance, listener_fID);

    if (listener != NULL) {
        jclass listener_class = (*env)->GetObjectClass(env, listener);
        jmethodID onBytesTransferred_fID = (*env)->GetMethodID(env, listener_class, "onBytesTransferred", "(Lcom/comet/androidstreaming/http/CustomHttpDataSource;I)V");

        (*env)->CallVoidMethod(env, listener, onBytesTransferred_fID, instance, read);

    }
    return read;
}

jint throwException( JNIEnv *env, char *className, char *message )
{
    jclass exClass;
    //char *className = "java/lang/NoClassDefFoundError";

    exClass = (*env)->FindClass( env, className);

    return (*env)->ThrowNew( env, exClass, message );
}
