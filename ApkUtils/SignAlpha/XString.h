//
// Created by jiaoyang on 16-8-3.
//

#ifndef SIGNALPHA_XSTRING_H
#define SIGNALPHA_XSTRING_H


#include "LinkedData.h"

class XString {
private:
    unsigned short length = 0;
    char *content = NULL;
public:
    XString(char *data);

    ~XString();

    LinkedData *serialize(LinkedData *ptr);

    char *getContent();

    unsigned short getLength();

    int compare(char *str);

    bool set(char *str);
};


#endif //SIGNALPHA_XSTRING_H
