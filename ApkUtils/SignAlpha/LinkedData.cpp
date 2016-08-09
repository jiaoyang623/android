//
// Created by jiaoyang on 16-8-5.
//

#include "LinkedData.h"

LinkedData::LinkedData(LinkedData *preData) {
    if (preData != NULL) {
        preData->next = this;
    }
}

LinkedData::~LinkedData() {
    if (data != NULL) {
        delete (data);
        data = NULL;
    }
}
