#!/bin/bash
set -eux
# Add -B to get rid of colors in the Maven output
mvn clean install -ntp -e -DskipITs
mvn site site:stage -ntp -Dmaven.test.skip.exec
