# Building and Publishing

LimLib has three Gradle modules:

- `common`: shared API, implementation, resources, and generated data
- `fabric`: Fabric loader jar
- `neoforge`: NeoForge loader jar

## Building

Build every module:

```sh
./gradlew build
```

On Windows:

```text
gradlew.bat build
```

Build one module:

```sh
./gradlew :common:build
./gradlew :fabric:build
./gradlew :neoforge:build
```

Run a development client or server:

```sh
./gradlew :fabric:runClient
./gradlew :fabric:runServer
./gradlew :neoforge:runClient
./gradlew :neoforge:runServer
```

Run NeoForge data generation:

```sh
./gradlew :neoforge:runData
```

`common` is not an installable mod. The loader jars are written to:

- `fabric/build/libs`
- `neoforge/build/libs`

## Publishing to Maven Local

Publish every module:

```sh
./gradlew publishToMavenLocal
```

Publish one module:

```sh
./gradlew :common:publishToMavenLocal
./gradlew :fabric:publishToMavenLocal
./gradlew :neoforge:publishToMavenLocal
```

Artifact IDs use this format:

```text
${mod_id}-${project.name}-${minecraft_version}
```

With the current Gradle properties, LimLib publishes as:

- `org.dimdev:limlib-common-1.21.1:13.0.11`
- `org.dimdev:limlib-fabric-1.21.1:13.0.11`
- `org.dimdev:limlib-neoforge-1.21.1:13.0.11`
