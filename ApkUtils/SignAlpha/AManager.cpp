//
// Created by daniel on 16-8-9.
//

#include <fstream>
#include "AManager.h"

AManager::AManager() {

}

AManager::~AManager() {

}

bool AManager::read(char *path) {
    ifstream ifs(path, ios::binary);

    if (!ifs) {
        cerr << "File read error!" << endl;
        return false;
    }

    ifs.seekg(0, ios::end);

    long len = ifs.tellg();
    char *buff = new char[len];
    ifs.seekg(0);
    ifs.read(buff, len);
    ifs.close();

    info = new XMLInfo(buff);
    delete (buff);

    return true;
}

bool AManager::write(char *path) {
    if (info == NULL) {
        cerr << "AndroidManifest.xml is not loaded!" << endl;
        return false;
    }
    LinkedData *data = info->serialize();

    ofstream ofs(path, ios::binary);
    if (!ofs) {
        cerr << "File write error!" << endl;
        return false;
    }

    ofs.write(data->data, data->size);
    ofs.close();
    delete (data);

    return true;
}

bool AManager::replace(char *src, char *dst) {
    info->stringChunk->replace(src, dst);
}


struct SearchBean {
    int tagId;
    int name;
};

bool findptr(Tag *tag, void *obj) {
    SearchBean *bean = (SearchBean *) obj;
    if (tag->name != bean->tagId) {
        return false;
    }
//    cout << tag->name << endl;
    for (int i = 0; i < tag->attributeCount; i++) {
//        cout << bean->name << " -> " << tag->attribute[i]->nameSpace << ":" << tag->attribute[i]->value << endl;
        if (tag->attribute[i]->value == bean->name) {
            return true;
        }
    }
    return false;
}


bool AManager::changeValue(char *tag, char *name, char *value) {
    if (info == NULL) {
        return false;
    }
    SearchBean *bean = new SearchBean;
    bean->tagId = info->stringChunk->index(tag);
    bean->name = info->stringChunk->index(name);
//    cout << bean->tagId << ", " << bean->name << endl;
    Tag *result = info->xmlChunk->foreach(findptr, bean);
    if (result != NULL) {
        int vIndex = -1;
        int vnIndex = info->stringChunk->index("value");
        for (int i = 0; i < result->attributeCount; i++) {
            if (result->attribute[i]->name == vnIndex) {
                vIndex = result->attribute[i]->value;
                break;
            }
        }
        if (vIndex != -1) {
            info->stringChunk->replace(vIndex, value);
        }
    }

    delete (bean);
}

void AManager::print() {
    info->print();
}

