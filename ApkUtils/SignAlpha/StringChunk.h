//
// Created by jiaoyang on 16-8-3.
//

#ifndef SIGNALPHA_STRINGCHUNK_H
#define SIGNALPHA_STRINGCHUNK_H

#include <iostream>
#include "XString.h"
#include "LinkedData.h"

class StringChunk {
private:
    char *read(char *buff);

public:
    unsigned int type = 0;
    unsigned int size = 0;
    unsigned int stringCount = 0;
    unsigned int styleCount = 0;
    unsigned int unknown = 0;
    unsigned int stringPoolOffset = 0;
    unsigned int stylePoolOffset = 0;
    unsigned int resourceSize = 0;
    unsigned int resourceCount = 0;
    unsigned int *resourceIds = NULL;
    XString **content = NULL;

    StringChunk(char *buff, const unsigned int offset);

    ~StringChunk();

    void print();

    LinkedData *serialize(LinkedData *ptr);

    int index(char *str);

    bool replace(char *src, char *dst);

    bool replace(int index, char *dst);
};


#endif //SIGNALPHA_STRINGCHUNK_H
