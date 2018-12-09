#! /bin/bash
mvn package
docker build -f src/main/docker/Dockerfile -t causalitysearch .
docker run -d -p 8080:8080 causalitysearch