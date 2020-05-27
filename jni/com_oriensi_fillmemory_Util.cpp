/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#define LOG_TAG "FillMemory"

#include <utils/Log.h>

#include "jni.h"
#include <nativehelper/JNIHelp.h>

#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h>


namespace android {

/*
 * JNI glue
 */

static jlong memory_size = 0;
static char* memory_ptrs[2048];

static jlong com_oriensi_fillmemory_MainActivity_nativeFillMemory(JNIEnv* env) {
  jlong m_block = 1024 * 1024;
  char* ptr;
  while (((ptr = (char*) malloc(m_block * sizeof(char))) != NULL)
         && memory_size < 2048) {
    memset(ptr, 0xFF, m_block * sizeof(char));
    memory_ptrs[memory_size++] = ptr;
    
    ALOGW("FillMem, size:%ld, value:%2x", memory_size, ptr[0]);
  }
  return memory_size;
}

static void com_oriensi_fillmemory_MainActivity_nativeFreeMemory(JNIEnv* env) {
  while (memory_size > 0) {
    free(memory_ptrs[--memory_size]);
  }
}

static JNINativeMethod gMethods[] = {
    { "nativeFillMemory", "()J", (void*)com_oriensi_fillmemory_MainActivity_nativeFillMemory },
    { "nativeFreeMemory", "()V", (void*)com_oriensi_fillmemory_MainActivity_nativeFreeMemory },
};

int register_com_oriensi_fillmemory_MainActivity(JNIEnv* env) {
    return jniRegisterNativeMethods(env, "com/oriensi/fillmemory/MainActivity",
            gMethods, NELEM(gMethods));
}

} /* namespace android */
