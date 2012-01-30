#!/bin/bash
java -Djava.library.path=native -Dfile.encoding=UTF-8 -DLWJGL_DISABLE_XRANDR=true -jar lib/${artifactId}-${version}.jar