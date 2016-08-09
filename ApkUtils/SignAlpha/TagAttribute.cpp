//
// Created by jiaoyang on 16-8-4.
//

#include "TagAttribute.h"

LinkedData *TagAttribute::serialize(LinkedData *preData) {
    unsigned int *data = new unsigned int[5];
    data[0] = nameSpace;
    data[1] = name;
    data[2] = valueString;
    data[3] = valueType;
    data[4] = value;

    LinkedData *ptr = new LinkedData(preData);
    ptr->size = 4 * 5;
    ptr->data = (char *) data;

    return ptr;
}