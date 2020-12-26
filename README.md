# Receiver

## Building a native image
1. Make sure to run GraalVM. (Check [SDKMAN](https://sdkman.io/) to easily switch between Java versions.)
1. Make sure `native-image` is installed: `gu install native-image`
1. Run `mvn -Pnative clean package`
1. The native image will be saved as `target/receiver`
