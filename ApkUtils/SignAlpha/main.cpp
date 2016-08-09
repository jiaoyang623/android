#include <iostream>
#include "AManager.h"

using namespace std;

int main(int argc, char **argv) {
    AManager *manager = new AManager();
    manager->read("/opt/workspace/SignAlpha/data/a1.xml");
//    manager->replace("360so", "jiaoy");
    manager->print();
    manager->changeValue("meta-data", "ALPHA", "jiaoyang1234");
    manager->write("/opt/workspace/SignAlpha/data/a2.xml");

    delete (manager);

    return 0;
}