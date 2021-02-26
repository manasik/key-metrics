Key deployment

Building docker image

If you don't have the directory, create it using 

```$ mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/*.jar)```

To build image

```docker build --build-arg JAR_FILE=build/libs/*.jar -t key-metrics .```

or

```$ ./gradlew bootBuildImage --imageName=key-metrics```

To run application

```docker run -p 8102:8102 -t key-metrics```

To setup locally

```./gradlew clean build```

```docker build --build-arg JAR_FILE=build/libs/*.jar -t key-metrics .```

```docker-compose up```


Debugging mongo

docker exec -it my-mongodb-container bash

// -it is a flag for 'interactive terminal', bash is the shell we'll use

root@example:/# mongo 
// enter mongodb shell

> use admin 
// switched to db admin

> db.auth("username", "password");
// 1

> show dbs
