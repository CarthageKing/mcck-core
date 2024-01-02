#!/bin/bash
set -eux
# Add -B to get rid of colors in the Maven output
mvn clean install -ntp -e -pl 'mcck-core-checkstyle' -DskipITs
mvn clean install -ntp -e -pl '!mcck-core-checkstyle' -DskipITs
mvn site site:stage -ntp -Dmaven.test.skip.exec
