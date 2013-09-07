#!/bin/sh
GIT_LOG_FORMAT="%ai %an: %s"
USER=<username>
API_TOKEN=<api_token>

LAST_SUCCESS_URL_SUFFIX="lastSuccessfulBuild/api/xml"
#JOB_URL gets populated by Jenkins as part of the build environment
URL="$JOB_URL$LAST_SUCCESS_URL_SUFFIX"

LAST_SUCCESS_REV=$(curl --silent --user $USER:$API_TOKEN $URL | grep "<lastBuiltRevision>" | sed 's|.*<lastBuiltRevision>.*<SHA1>\(.*\)</SHA1>.*<branch>.*|\1|')
# Pulls all commit comments since the last successfully built revision
LOG=$(git log --pretty="$GIT_LOG_FORMAT" $LAST_SUCCESS_REV..HEAD)
echo $LOG