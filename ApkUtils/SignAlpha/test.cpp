//
// Created by jiaoyang on 16-8-3.
//

#include <iostream>
#include <cstring>
#include <string>

using namespace std;

void print(string msg) {
    cout << msg << endl;
}

int tmain(int argc, char **argv) {
    int x = 1, i = 2;
    char data[32];
    sprintf(data, "%d<%d&&%d>%d", 3, 5, 3, 6);
    cout << data << endl;
    return 0;
}