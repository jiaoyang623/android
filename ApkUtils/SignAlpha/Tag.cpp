//
// Created by jiaoyang on 16-8-4.
//

#include "Tag.h"

Tag::Tag() {

}

Tag::~Tag() {
    if (this->attribute != NULL) {
        delete (this->attribute);
    }

    if (this->children != NULL) {
        for (int i = 0; i < this->childrenCount; i++) {
            if (this->children[i] != NULL) {
                delete (this->children[i]);
                this->children[i] = NULL;
            }
        }
        delete (this->children);
        this->children = NULL;
        this->childrenCount = -1;
    }
}

void Tag::print() {

}

LinkedData *Tag::serialize(LinkedData *head) {
    switch (type) {
        case TYPE_TAG_START:
            return serializeTag(head);
        case TYPE_NAMESPACE_START:
            return serializeNamespace(head);
        case TYPE_TEXT:
            return serializeText(head);
        default:
            return NULL;
    }
}

LinkedData *Tag::serializeTag(LinkedData *head) {
//    tag
//    [size, source, -1, prefix, name]
//    Attribute: 1400 1400
//    [count, src, [items]*count]
//    item
//    [prefix, uri, valueString, type, value]
    //[type, size, source, -1, prefix, name]
    //
    LinkedData *ptr = new LinkedData(head);

    int size = 6 + 3;
    int length = 6 + 3 + 5 * attributeCount;

    unsigned int *data = tagBuffer(size, TYPE_TAG_START, length * 4, source, prefix, name);
    data[6] = 0x00140014;
    data[7] = attributeCount;
    data[8] = 0;
    ptr->size = 4 * size;
    ptr->data = (char *) data;

    // append attributes
    for (int i = 0; i < attributeCount; i++) {
        ptr = attribute[i]->serialize(ptr);
    }

    // append children tags
    for (int i = 0; i < childrenCount; i++) {
        ptr = children[i]->serialize(ptr);
    }

    // append tag close
    //EndTag: 0301 1000
    //[size, source, -1, prefix, name]
    ptr = new LinkedData(ptr);
    ptr->size = 4 * 6;
    ptr->data = (char *) tagBuffer(6, TYPE_TAG_STOP, 4 * 6, endSource, prefix, name);

    return ptr;
}

LinkedData *Tag::serializeNamespace(LinkedData *head) {
    LinkedData *ptr;
    // start
    ptr = new LinkedData(head);
    ptr->size = 4 * 6;
    ptr->data = (char *) tagBuffer(6, TYPE_NAMESPACE_START, 4 * 6, source, prefix, name);

    // append children tags
    for (int i = 0; i < childrenCount; i++) {
        ptr = children[i]->serialize(ptr);
    }

    ptr = new LinkedData(ptr);
    ptr->size = 4 * 6;
    ptr->data = (char *) tagBuffer(6, TYPE_NAMESPACE_STOP, 4 * 6, endSource, prefix, name);

    return ptr;
}


LinkedData *Tag::serializeText(LinkedData *head) {
    return head;
}

unsigned int *Tag::tagBuffer(unsigned int bufferSize, unsigned int type,
                             unsigned int length, unsigned int source,
                             unsigned int prefix, unsigned int name) {
    unsigned int *data = new unsigned int[bufferSize];
    data[0] = type;
    data[1] = length;
    data[2] = source;
    data[3] = -1;
    data[4] = prefix;
    data[5] = name;
    return data;
}

Tag *Tag::foreach(bool (*p)(Tag *, void *obj), void *obj) {
    if (p != NULL) {
        if ((*p)(this, obj)) {
            return this;
        }
        for (int i = 0; i < childrenCount; i++) {
            Tag *result = children[i]->foreach(p, obj);
            if (result != NULL) {
                return result;
            }
        }
    }

    return NULL;
}
