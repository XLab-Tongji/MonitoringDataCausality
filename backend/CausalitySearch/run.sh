#! /bin/bash
#mvn install:install-file -Dfile=tetrad-gui-6.6.0-SNAPSHOT.jar -DgroupId=edu.cmu -DartifactId=tetrad-gui -Dversion=6.6.0-SNAPSHOT -Dpackaging=jar -DgeneratePom=true
mvn package
docker build -f src/main/docker/Dockerfile -t causalitysearch .
docker run -d -p 8080:8080 causalitysearch