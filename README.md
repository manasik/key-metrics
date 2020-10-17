Key metrics

Building docker image

If you don't have the directory, create it using 

```$ mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/*.jar)```

To build image

```docker build --build-arg DEPENDENCY=build/dependency -t key-metrics .```

To run application

```docker run -p 8080:8080 -t key-metrics```
