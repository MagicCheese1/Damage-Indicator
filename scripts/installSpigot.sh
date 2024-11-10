#!/bin/bash

mkdir $1
cp buildtools.jar $1/buildtools.jar
cd $1
java -jar BuildTools.jar -rev $1
cd ..
