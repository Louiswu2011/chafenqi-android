
# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html.
# For more examples on how to use CMake, see https://github.com/android/ndk-samples.

cmake_minimum_required(VERSION 3.10.2)

project(tun2http)

include_directories(${CMAKE_CURRENT_SOURCE_DIR}/src/main/cpp)
set(EXECUTABLE_OUTPUT_PATH "${CMAKE_CURRENT_SOURCE_DIR}/src/main/assets/${ANDROID_ABI}")

add_library( # Sets the name of the library.
        tun2http

        # Sets the library as a shared library.
        SHARED

        src/main/cpp/dhcp.c
        src/main/cpp/dns.c
        src/main/cpp/icmp.c
        src/main/cpp/ip.c
        src/main/cpp/http.c
        src/main/cpp/tun2http.c
        src/main/cpp/session.c
        src/main/cpp/tcp.c
        src/main/cpp/tls.c
        src/main/cpp/udp.c
        src/main/cpp/util.c
        )

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

target_link_libraries( # Specifies the target library.
        tun2http
        ${log-lib}
        )