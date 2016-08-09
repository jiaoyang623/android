//
// Created by jiaoyang on 16-8-3.
//

#include "StringChunk.h"
#include <cstring>

using namespace std;

StringChunk::StringChunk(char *buff, const unsigned int offset) {
    char *buffer = buff + offset;
    unsigned int *ptr = (unsigned int *) (buffer);
    this->type = *ptr++;
    this->size = *ptr++;
    this->stringCount = *ptr++;
    this->styleCount = *ptr++;
    this->unknown = *ptr++;
    this->stringPoolOffset = *ptr++;
    this->stylePoolOffset = *ptr++;
    //创建多维数组
    int count = this->stringCount;
    this->content = new XString *[count];

    for (int i = 0; i < count; i++) {
        XString *content = new XString(buffer + this->stringPoolOffset + *(ptr++));
        this->content[i] = content;
    }

    ptr = (unsigned int *) (buffer + this->size);
    ptr++;
    this->resourceSize = *ptr++;
    int idLength = this->resourceSize - 8;
    if (idLength < 0) {
        cerr << "Invalid resource id length (" << idLength << ")." << endl;
        exit(-1);
    } else {
        int count = idLength / 4;
        this->resourceCount = count;
        this->resourceIds = new unsigned int[count];
        for (int i = 0; i < count; i++) {
            this->resourceIds[i] = *ptr++;
        }
    }

}

StringChunk::~StringChunk() {

    for (int i = 0; i < this->stringCount; i++) {
        if (this->content[i] != NULL) {
            delete (this->content[i]);
            this->content[i] = NULL;
        }
    }
    if (this->content != NULL) {
        delete (this->content);
        this->content = NULL;
    }

    if (this->resourceIds != NULL) {
        delete (this->resourceIds);
        this->resourceIds = NULL;
    }
}

void StringChunk::print() {
    cout << "type: " << this->type << endl;
    cout << "size: " << this->size << endl;
    cout << "stringCount: " << this->stringCount << endl;
    cout << "styleCount: " << this->styleCount << endl;
    cout << "unknown: " << this->unknown << endl;
    cout << "stringPoolOffset: " << this->stringPoolOffset << endl;
    cout << "stylePoolOffset: " << this->stylePoolOffset << endl;
    cout << "StringChunk:" << endl;
    for (int i = 0; i < this->stringCount; i++) {
        cout << '\t' << i << ": " << this->content[i]->getContent() << endl;
    }

}

LinkedData *StringChunk::serialize(LinkedData *ptr) {
    int strTotal = 0;

    // description
    unsigned int *data = new unsigned int[7];
    data[0] = this->type;
    data[1] = this->size;
    data[2] = this->stringCount;
    data[3] = this->styleCount;
    data[4] = this->unknown;
    data[5] = this->stringPoolOffset;
    data[6] = this->stylePoolOffset;
    ptr = new LinkedData(ptr);
    ptr->size = 4 * 7;
    ptr->data = (char *) data;
    strTotal += ptr->size;
    // offsets
    unsigned int *offsetData = new unsigned int[stringCount];
    offsetData[0] = 0;
    for (int i = 1; i < stringCount; i++) {
        offsetData[i] = offsetData[i - 1] + (content[i - 1]->getLength() + 2) * 2;
    }
    ptr = new LinkedData(ptr);
    ptr->size = 4 * stringCount;
    ptr->data = (char *) offsetData;
    strTotal += ptr->size;

    // strings
    for (int i = 0; i < stringCount; i++) {
        ptr = content[i]->serialize(ptr);
        strTotal += ptr->size;
    }
    // string postfix
    ptr = new LinkedData(ptr);
    ptr->size = 4 - (strTotal % 4);
    ptr->size = ptr->size == 4 ? 0 : ptr->size;
    ptr->data = new char[ptr->size];
    memset(ptr->data, 0, ptr->size);
    strTotal += ptr->size;
    data[1] = strTotal;

    // resource
    unsigned int *resData = new unsigned int[resourceCount + 2];
    resData[0] = 0x00080180; //id 8001 0800
    resData[1] = (resourceCount + 2) * 4;
    for (int i = 0; i < resourceCount; i++) {
        resData[i + 2] = resourceIds[i];
    }
    ptr = new LinkedData(ptr);
    ptr->size = resData[1];
    ptr->data = (char *) resData;

    return ptr;
}

int StringChunk::index(char *str) {
    for (int i = 0; i < stringCount; i++) {
        if (content[i]->compare(str) == 0) {
            return i;
        }
    }
    return -1;
}


bool StringChunk::replace(char *src, char *dst) {
    for (int i = 0; i < stringCount; i++) {
        if (content[i]->compare(src) == 0) {
            content[i]->set(dst);
        }
    }

    return true;
}

bool StringChunk::replace(int index, char *dst) {
    if (index >= 0 && index < stringCount) {
        content[index]->set(dst);
        return true;
    } else {
        return false;
    }
}
