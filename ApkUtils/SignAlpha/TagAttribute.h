//
// Created by jiaoyang on 16-8-4.
//

#ifndef SIGNALPHA_TAGATTRIBUTE_H
#define SIGNALPHA_TAGATTRIBUTE_H


#include "LinkedData.h"

class TagAttribute {
public:
    int nameSpace;
    int name;
    int valueString;
    int valueType;
    int value;

    LinkedData *serialize(LinkedData *preData);
};


#endif //SIGNALPHA_TAGATTRIBUTE_H
