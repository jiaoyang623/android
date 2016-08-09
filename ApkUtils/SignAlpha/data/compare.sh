#!/bin/bash

xxd a1.xml > a1x.xml
xxd a2.xml > a2x.xml
meld a1x.xml a2x.xml
