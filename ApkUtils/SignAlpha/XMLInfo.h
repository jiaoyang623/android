//
// Created by jiaoyang on 16-8-3.
//

#ifndef SIGNALPHA_XMLINFO_H
#define SIGNALPHA_XMLINFO_H

#include "StringChunk.h"
#include "XMLChunk.h"
#include <string>

/**
 * Magic Number
 * SFile Size
 * String Chunk
 * ResourceId Chunk
 * XmlContent Chunk
 * */

using namespace std;

class XMLInfo {
public:
    unsigned int magicNumber;
    unsigned int fileSize;
    StringChunk *stringChunk;
    XMLChunk *xmlChunk;

    XMLInfo(char *buff);

    ~XMLInfo();

    void print();

    LinkedData *serialize();

    void printTag(Tag *tag, string indent);

    void printTag(Tag *tag);

    string getString(int index);

    string makeNamespacePrefix(string nameSpace);

    string makeAttributeValue(unsigned int type, string valueString, unsigned int value);
};


#endif //SIGNALPHA_XMLINFO_H
