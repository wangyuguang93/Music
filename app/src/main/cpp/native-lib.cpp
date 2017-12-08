#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_com_music_guang_music_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_music_guang_musicG_MainActivity_BannerId(
        JNIEnv *env,
jobject /* this */) {
std::string BannerId = "ca-app-pub-2160621189322911/7600690099";
return env->NewStringUTF(BannerId.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_music_guang_musicG_MainActivity_InsertId(
        JNIEnv *env,
        jobject /* this */) {
    std::string InsertId = "ca-app-pub-2160621189322911/5210625628";
    return env->NewStringUTF(InsertId.c_str());
}