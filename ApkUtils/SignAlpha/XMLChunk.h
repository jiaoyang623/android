//
// Created by jiaoyang on 16-8-4.
//

#ifndef SIGNALPHA_XMLCHUNK_H
#define SIGNALPHA_XMLCHUNK_H


#include "Tag.h"

class XMLChunk {
public:
    Tag *root;

    XMLChunk(char *buff, const unsigned int offset);

    ~XMLChunk();

    Tag *foreach(bool (*p)(Tag *, void *obj), void *obj);
};


#endif //SIGNALPHA_XMLCHUNK_H
