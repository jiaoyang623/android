//
// Created by jiaoyang on 16-8-4.
//

#ifndef SIGNALPHA_TAG_H
#define SIGNALPHA_TAG_H


#include "TagAttribute.h"
#include "StringChunk.h"

class Tag {
public:
    enum TYPE {
        TYPE_NAMESPACE_START = 0x00100100,
        TYPE_NAMESPACE_STOP = 0x00100101,
        TYPE_TAG_START = 0x00100102,
        TYPE_TAG_STOP = 0x00100103,
        TYPE_TEXT = 0x00100104,
    };

    unsigned int type = 0;
    unsigned int source = 0;
    unsigned int endSource = 0;
    unsigned int name = 0;
    unsigned int prefix = 0;
    unsigned int attributeCount = 0;
    TagAttribute **attribute = NULL;
    unsigned int childrenCount = 0;
    Tag **children = NULL;
    Tag *parent = NULL;

    Tag();

    ~Tag();

    void print();

    LinkedData *serialize(LinkedData *head);

    LinkedData *serializeTag(LinkedData *head);

    LinkedData *serializeNamespace(LinkedData *head);

    LinkedData *serializeText(LinkedData *head);

    Tag *foreach(bool(*p)(Tag *, void *obj), void *obj);

private:
    unsigned int *tagBuffer(unsigned int bufferSize, unsigned int type,
                            unsigned int length, unsigned int source,
                            unsigned int prefix, unsigned int name);


};


#endif //SIGNALPHA_TAG_H
