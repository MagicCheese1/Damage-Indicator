#!/bin/bash

mkdir $1
cp BuildTools.jar $1/BuildTools.jar
cd $1
java -jar BuildTools.jar -rev $1 --remapped
cd ..
