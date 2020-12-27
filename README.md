# Receiver

## Building a native image
1. Make sure to run GraalVM. (Check [SDKMAN](https://sdkman.io/) to easily switch between Java versions.)
1. Make sure `native-image` is installed: `gu install native-image`
1. Run `mvn -Pnative clean package`
1. The native image will be saved as `target/receiver`

## Using a native image
Simply run the program via the terminal.

Properties (specified in `src/main/resources/application.properties`) can be changed at run time.
E.g. to change the uploads root, run the native image like this: `./receiver --uploads-root=<path>`.
