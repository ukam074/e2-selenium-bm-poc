#!/usr/bin/env bash

if [ ! -d $HOME/Documents/GitHub/e2-selenium/src/test/resources/testAttachments ]; then
  echo "This direcotry is needed for the attachment tests.  Place test files there."
  echo $HOME/Documents/GitHub/e2-selenium/src/test/resources/testAttachmentsTest
  exit 1
fi

if [[ ! -d target ]]; then
  mkdir target &>/dev/null
fi
if [[ -f target/dockerInfo.txt ]]; then
  echo "remove dockerInfo"
  rm target/dockerInfo.txt
fi

$(brew install grep wget coreutils) &>/dev/null

docker info >>target/dockerInfo.txt
ERR=$(grep ERROR target/dockerInfo.txt | cut -d ':' -f1)

echo "CMD: $CMD"
if [[ $ERR == "ERROR" ]]; then
  echo "<=== ERROR ===>"
  echo "Docker is not running"
  echo "Please install or start before using local grid"
  exit 1
else
  echo "<===== Info =====>"
  echo "You can open Zalenium Dashboard at: http://localhost:4444/dashboard"
  echo "You can see live preview at: http://localhost:4444/grid/admin/live"
  echo "Enter ctrl-C to stop"
  docker run --rm -ti --name zalenium -p 4444:4444 \
    -e PULL_SELENIUM_IMAGE=true \
    -v /var/run/docker.sock:/var/run/docker.sock \
    -v /tmp/videos:/home/seluser/videos \
    -v $HOME/Documents/GitHub/e2-selenium/src/test/resources/testAttachments:/tmp/node/src/test/resources/testAttachments \
    --privileged dosel/zalenium start
fi

