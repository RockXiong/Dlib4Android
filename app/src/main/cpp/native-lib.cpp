//
// Created by xiongda on 2017/7/22.
//

#include <jni.h>
#include <string>
#include <vector>
#include <iostream>

#include "dlib/image_io.h"
#include "dlib/image_processing.h"
#include "dlib/image_processing/frontal_face_detector.h"

#include <ctime>

dlib::frontal_face_detector m_facedetector = dlib::get_frontal_face_detector();

//获取人脸框
std::vector<int> getfacerect(const std::vector<int> img, int height, int width) {
    dlib::array2d<unsigned char> image;
    image.set_size(height, width);

    for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
            int clr = img[i * width + j];
            int red = (clr & 0x00ff0000) >> 16;//取高两位
            int green = (clr & 0x0000ff00) >> 8;//取中两位
            int blue = clr & 0x000000ff;//取低两位

            unsigned char gray = red * 0.299 + green * 0.587 + blue * 0.114;

            //dlib::rgb_pixel pt(red,green.blue);
            image[i][j] = gray;

        }
    }

    clock_t begin = clock();
    std::vector<dlib::rectangle> dets = m_facedetector(image);

    std::vector<int> rect;

    if (!dets.empty()) {
        rect.push_back(dets[0].left());
        rect.push_back(dets[0].right());
        rect.push_back(dets[0].width());
        rect.push_back(dets[0].height());
    }
    return rect;
}

extern "C"
JNIEXPORT jintArray JNICALL
Java_com_xiongda_dlib4android_MainActivity_stringFromJNI(JNIEnv *env, jobject /* this */, jintArray image_data,
                                                         jint image_height, jint image_width) {

//    std::string hello = "Hello from C++";
//    return env->NewStringUTF(hello.c_str());
    std::vector<int> image_datacpp(image_height * image_width, image_width);
    jsize len = env->GetArrayLength(image_data);

    jint *body = env->GetIntArrayElements(image_data, 0);

    for (jsize i = 0; i < len; i++) {
        image_datacpp[i] = (int) body[i];
    }
    std::vector<int > rect=getfacerect(image_datacpp,image_height,image_width);
    jintArray result=env->NewIntArray(4);
    env->SetIntArrayRegion(result,0,4,&rect[0]);

    return result;
}
