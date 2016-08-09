//
// Created by jiaoyang on 16-8-3.
//

#include <cstring>
#include "XString.h"

XString::XString(char *data) {
    length = *((unsigned short *) (data));
    content = new char[length + 1];
    for (int i = 0; i < length; i++) {
        content[i] = (char) (*((unsigned short *) (data + 2 + i * 2)));
    }
    content[length] = '\0';
}

XString::~XString() {
    if (content != NULL) {
        delete (content);
        content = NULL;
    }
}

LinkedData *XString::serialize(LinkedData *ptr) {
    ptr = new LinkedData(ptr);

    int len = this->length;
    int size = (len + 2) * 2;
    unsigned short *data = new unsigned short[len + 2];
    data[0] = this->length;
    for (int i = 0; i < len; i++) {
        data[i + 1] = content[i];
    }
    data[len + 1] = 0;
    ptr->data = (char *) data;
    ptr->size = size;

    return ptr;
}

char *XString::getContent() {
    return content;
}

unsigned short XString::getLength() {
    return length;
}

int XString::compare(char *str) {
    return strcmp(content, str);
}

bool XString::set(char *str) {
    int len = strlen(str);
    if (len > 0 && len < 0xffff) {
        content = new char[len + 1];
        length = len;
        memcpy(content, str, len + 1);
        return true;
    } else {
        return false;
    }
}
