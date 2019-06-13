#! /bin/bash
PORT=8080
#set container port if have "-p" option
while getopts 'p:' OPT; do
    case $OPT in
        p)
            PORT=$OPTARG;
    esac
done

#add data processing libraries and build package
cd CausalitySearch
mvn install:install-file -Dfile=src/lib/tetrad-lib-6.7.0-SNAPSHOT.jar -DgroupId=edu.cmu -DartifactId=tetrad-lib -Dversion=6.7.0-SNAPSHOT -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -Dfile=src/lib/data-reader-1.1.0.jar -DgroupId=edu.pitt.dbmi -DartifactId=data-reader -Dversion=1.1.0 -Dpackaging=jar -DgeneratePom=true
mvn package
cd ..
#build docker and run

CURRENT_CONTAINER=$(docker ps -aq -f "name=causality_search")
if [ -n "$CURRENT_CONTAINER" ]; then
    docker stop $CURRENT_CONTAINER
    docker rm $CURRENT_CONTAINER
fi

docker build -f Docker/Dockerfile -t causalitysearch:2.0 .
docker run --name causality_search -d -p $PORT:8080 causalitysearch:2.0
