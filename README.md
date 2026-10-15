# Musings
A web app to share what's on your mind.

## Prerequisites
* JDK 25
* Docker and Docker Compose
* Your favourite IDE (Recommended: [IntelliJ IDEA](https://www.jetbrains.com/idea/))

## Using mise
[mise](https://mise.jdx.dev/) can install the required SDKs (Java, Maven) and run tasks defined in `mise.toml`.

```shell
# Trust the project's mise.toml (one-time)
$ mise trust

# Install the SDKs (Java 25 Temurin, Maven)
$ mise install

# List available tasks
$ mise tasks

# Format code and run tests
$ mise run format
$ mise run test

# Run verification checks
$ mise run verify

# Build docker image
$ mise run build_image

# Run application in docker container
$ mise run start
$ mise run stop
$ mise run restart
```
