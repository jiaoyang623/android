//
// Created by jiaoyang on 16-8-4.
//

#include <iostream>
#include <cstring>
#include "XMLChunk.h"

XMLChunk::XMLChunk(char *buff, const unsigned offset) {
    Tag *rootTag = NULL;
    Tag *currentTag = NULL;
    unsigned int *ptr = (unsigned int *) (buff + offset);
    unsigned int type = -1;
    unsigned int size = -1;
    unsigned int sourceLine = -1;
    unsigned int prefix = -1;
    unsigned int name = -1;
    int count = 0;
    Tag *tag = NULL;
    while (type != Tag::TYPE_NAMESPACE_STOP) {
        type = *ptr++;
        size = *ptr++;
        sourceLine = *ptr++;
        ptr++;// -1
        prefix = *ptr++;
        name = *ptr++;
        switch (type) {
            case Tag::TYPE_NAMESPACE_START:
                tag = new Tag();
                tag->children = new Tag *[1024];
                tag->type = type;
                tag->source = sourceLine;
                tag->prefix = prefix;
                tag->name = name;

                if (rootTag == NULL) {
                    currentTag = rootTag = tag;
                } else {
                    tag->parent = currentTag;
                    currentTag->children[currentTag->childrenCount++] = tag;
                    currentTag = tag;
                }
                break;
            case Tag::TYPE_NAMESPACE_STOP:
                count = currentTag->childrenCount;
                currentTag->endSource = sourceLine;
                if (count > 0) {
                    Tag **children = new Tag *[count];
                    memcpy(children, currentTag->children, sizeof(currentTag) * count);
                    delete (currentTag->children);
                    currentTag->children = children;
                } else {
                    delete (currentTag->children);
                    currentTag->children = NULL;
                }
                currentTag = currentTag->parent;
                // end a tag
                break;
            case Tag::TYPE_TAG_START: {
                // start a tag
                tag = new Tag();
                tag->children = new Tag *[1024];
                tag->type = type;
                tag->source = sourceLine;
                tag->prefix = prefix;
                tag->name = name;
                // attribute
                ptr++; // attr 0x00140014
                count = tag->attributeCount = *ptr++;
                int attrSrc = *ptr++; // sourceline
                tag->attribute = new TagAttribute *[tag->attributeCount];
                for (int i = 0; i < count; i++) {
                    TagAttribute *attribute = new TagAttribute();
                    attribute->nameSpace = *ptr++;
                    attribute->name = *ptr++;
                    attribute->valueString = *ptr++; // index or -1
                    attribute->valueType = *ptr++;
                    attribute->value = *ptr++;
                    tag->attribute[i] = attribute;

//                    printf("%d: %08x %08x %08x %08x %08x\n", i,
//                           attribute->nameSpace,
//                           attribute->name,
//                           attribute->valueString,
//                           attribute->valueType,
//                           attribute->value
//                    );
                }
                if (rootTag == NULL) {
                    currentTag = rootTag = tag;
                } else {
                    tag->parent = currentTag;
                    currentTag->children[currentTag->childrenCount++] = tag;
                    currentTag = tag;
                }
            }
                break;
            case Tag::TYPE_TAG_STOP: // end tag
                count = currentTag->childrenCount;
                currentTag->endSource = sourceLine;
                if (count > 0) {
                    Tag **children = new Tag *[count];
                    memcpy(children, currentTag->children, sizeof(currentTag) * count);
                    delete (currentTag->children);
                    currentTag->children = children;
                } else {
                    delete (currentTag->children);
                    currentTag->children = NULL;
                }
                currentTag = currentTag->parent;
                // end a tag
                break;
            case Tag::TYPE_TEXT: // set name
                currentTag->name = name;
                break;
            default:
                std::cerr << "Invalid tag type (" << type << ")." << std::endl;
                exit(-1);
        }
    }
    this->root = rootTag;
}

XMLChunk::~XMLChunk() {
    if (this->root != NULL) {
        delete (this->root);
        this->root = NULL;
    }
}

Tag *XMLChunk::foreach(bool (*p)(Tag *, void *obj), void *obj) {
    if (root != NULL) {
        return root->foreach(p, obj);
    } else {
        return NULL;
    }
}
