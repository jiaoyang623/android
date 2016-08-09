//
// Created by jiaoyang on 16-8-3.
//

#include <cstring>
#include "XMLInfo.h"

XMLInfo::XMLInfo(char *buff) {
    this->magicNumber = *((unsigned int *) (buff));
    this->fileSize = *((unsigned int *) buff + 1);
    StringChunk *stringChunk = new StringChunk(buff, 8);
    this->stringChunk = stringChunk;
    XMLChunk *xmlChunk = new XMLChunk(buff, 8 + stringChunk->size + stringChunk->resourceSize);
    this->xmlChunk = xmlChunk;
}

XMLInfo::~XMLInfo() {
    if (this->stringChunk != NULL) {
        delete (this->stringChunk);
        this->stringChunk = NULL;
    }
    if (this->xmlChunk != NULL) {
        delete (this->xmlChunk);
        this->xmlChunk = NULL;
    }
}

void XMLInfo::print() {
    this->stringChunk->print();
    this->printTag(xmlChunk->root, "");
}

void XMLInfo::printTag(Tag *tag, string indent) {
    cout << indent << "<" << getString(tag->name) << endl;
    for (int i = 0; i < tag->attributeCount; i++) {
        TagAttribute *attr = tag->attribute[i];
        cout << indent << "\t"
             << makeNamespacePrefix(getString(attr->nameSpace))
             << getString(attr->name)
             << "="
             << makeAttributeValue(attr->valueType, getString(attr->valueString), attr->value)
             << endl;
    }
    cout << indent << "\t>" << endl;

    for (int i = 0; i < tag->childrenCount; i++) {
        printTag(tag->children[i], indent + "\t");
    }

    cout << indent << "</" << getString(tag->name) << ">" << endl;
}

void XMLInfo::printTag(Tag *tag) {
    cout << "<" << getString(tag->name) << endl;
    for (int i = 0; i < tag->attributeCount; i++) {
        TagAttribute *attr = tag->attribute[i];
        cout << "\t"
             << makeNamespacePrefix(getString(attr->nameSpace))
             << getString(attr->name)
             << "="
             << makeAttributeValue(attr->valueType, getString(attr->valueString), attr->value)
             << endl;
    }
    cout << "\t>" << endl;
}

string XMLInfo::getString(int index) {
    if (index >= 0 && index < this->stringChunk->stringCount) {
        return this->stringChunk->content[index]->getContent();
    } else {
        return "";
    }
}

string XMLInfo::makeNamespacePrefix(string nameSpace) {
    if (nameSpace.length() != 0) {
        if (nameSpace.compare("http://schemas.android.com/apk/res/android") == 0) {
            return "android:";
        } else {
            return nameSpace.append(":");
        }
    } else {
        return "";
    }
}

string XMLInfo::makeAttributeValue(unsigned int type, string valueString, unsigned int value) {
    if (type == 0x3000008) {
        return "\"" + valueString + "\"";
    } else {
        char data[64];
        sprintf(data, "<0x%X, type 0x%02X>", value, type);
        return data;
    }
}

LinkedData *XMLInfo::serialize() {
    LinkedData *rootPtr = new LinkedData(NULL);
    int *fileData = new int[2];
    fileData[0] = magicNumber;
    // fileData[1] = fileSize;
    rootPtr->size = 4 * 2;
    rootPtr->data = (char *) fileData;

    xmlChunk->root->serialize(stringChunk->serialize(rootPtr));

    // get total length
    int length = 0;
    for (LinkedData *ptr = rootPtr; ptr != NULL; length += ptr->size, ptr = ptr->next);
    // set file size;
    fileData[1] = length;
    // copy data
    char *data = new char[length];
    long pos = 0;
    for (LinkedData *ptr = rootPtr; ptr != NULL; ptr = ptr->next) {
        memcpy(data + pos, ptr->data, ptr->size);
        pos += ptr->size;
    }

    LinkedData *result = new LinkedData(NULL);
    result->size = length;
    result->data = data;

    // free memory
    for (LinkedData *ptr = rootPtr, *pre = ptr; ptr->next != NULL; ptr = ptr->next, delete (pre), pre = ptr);

    return result;
}


