#! /bin/bash
mvn install:install-file -Dfile=src/lib/tetrad-gui-6.6.0-SNAPSHOT.jar -DgroupId=edu.cmu -DartifactId=tetrad-gui -Dversion=6.6.0-SNAPSHOT -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -Dfile=src/lib/data-reader-0.2.4.jar -DgroupId=edu.pitt.dbmi -DartifactId=data-reader -Dversion=0.2.4 -Dpackaging=jar -DgeneratePom=true
mvn package
docker build -f src/main/docker/Dockerfile -t causalitysearch-v1.0 .
docker run -d -p 17777:17777 causalitysearch-v1.0