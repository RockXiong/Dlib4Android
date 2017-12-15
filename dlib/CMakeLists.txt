cmake_minimum_required(VERSION 2.8.12)
# 指定生成库的输出路径
#SET(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${PROJECT_SOURCE_DIR}/demo/Dlib18_10/${CMAKE_BUILD_TYPE}/${ANDROID_ABI})
SET(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${PROJECT_SOURCE_DIR}/demo/Dlib19_04/${CMAKE_BUILD_TYPE}/${ANDROID_ABI})
 #add_subdirectory(src/main/cpp/dlib-19.4) #编译 dlib
#add_subdirectory(src/main/cpp/dlib-18.10) #编译 dlib18.10
# ADD_SUBDIRECTORY(src/main/cpp/tbb) #编译tbb

# ADD_SUBDIRECTORY(src/main/cpp/landmarkdetector) # 编译landmarkdetector

include(src/main/cpp/dlib-19.4/dlib/cmake)

add_library( native-lib SHARED src/main/cpp/native-lib.cpp)

target_link_libraries(
        native-lib
        dlib
        android
        log
)