cmake_minimum_required(VERSION 3.5)
project(SignAlpha)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

set(SOURCE_FILES main.cpp test.cpp XMLInfo.cpp XMLInfo.h StringChunk.cpp StringChunk.h XString.cpp XString.h XMLChunk.cpp XMLChunk.h TagAttribute.cpp TagAttribute.h Tag.cpp Tag.h LinkedData.cpp LinkedData.h AManager.cpp AManager.h)
add_executable(SignAlpha ${SOURCE_FILES})