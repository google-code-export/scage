#!/bin/bash
            java -Djava.library.path=native -DLWJGL_DISABLE_XRANDR=true -cp lib/commons-logging-1.1.1.jar:lib/jinput.jar:lib/log4j-1.2.15.jar:lib/lwjgl.jar:lib/lwjgl_util.jar:lib/phys2d-060408.jar:lib/scala-library.jar:lib/slick-util.jar:lib/json.jar:lib/scage-v02.jar:lib/tracertest-v01.jar helloworld.HelloWorld
        