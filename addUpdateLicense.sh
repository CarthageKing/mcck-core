#!/bin/bash
mvn org.codehaus.mojo:license-maven-plugin:2.3.0:update-file-header -Dlicense.trimHeaderLine=true -Dlicense.encoding=utf-8 -Dlicense.inceptionYear=2023 -Dlicense.organizationName="Michael I. Calderero" -Dlicense.licenseName=apache_v2
