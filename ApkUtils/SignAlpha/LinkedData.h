//
// Created by jiaoyang on 16-8-5.
//

#ifndef SIGNALPHA_LINKEDDATA_H
#define SIGNALPHA_LINKEDDATA_H

#include <iostream>

class LinkedData {
public:
    int size = 0;
    char *data = NULL;

    LinkedData *next = NULL;

    ~LinkedData();

    LinkedData(LinkedData *preData);
};


#endif //SIGNALPHA_LINKEDDATA_H
