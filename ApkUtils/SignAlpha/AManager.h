//
// Created by daniel on 16-8-9.
//

#ifndef SIGNALPHA_AMANAGER_H
#define SIGNALPHA_AMANAGER_H


#include "XMLInfo.h"

class AManager {
public:
    AManager();

    ~AManager();

    bool read(char *path);

    bool write(char *path);

    bool replace(char *src, char *dst);

    bool changeValue(char *tag, char *name, char *value);

    void print();

    XMLInfo *info = NULL;
};


#endif //SIGNALPHA_AMANAGER_H
