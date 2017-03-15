# Build a sample system to play with the intergration between ARTIK Cloud and Kinesis

**Do not peek!!! Work in progress.**

TODO

After completing this sample, you will learn the following objectives:

- TODO
- TODO.

## Requirements
- Java version >= 1.8
- Apache Maven 3.0.5 or above
- ARTIK Cloud SDK for Java
- [AWS SDK for Java](https://aws.amazon.com/developers/getting-started/java/)

## Setup / Installation

### Setup at ARTIK Cloud???

### Setup the AWS Kinesis Java project

 1. Clone this sample application if you haven't already done so.
 2. Cd to `kinesis-streams-consumer` directory and run the following build command at in this directory:

  ~~~shell
  mvn clean package
  ~~~

  The executable `target/read-stream-x.x.jar` is generated under the target directory.

## Play with the system
Are you ready to have fun? 

 1. Start Kinesis stream read app. Run the following command at the directory where the JAR file is located:
  ~~~shell
  java -jar read-stream-x.x.jar k AWS_KEY -s AWS_SECRETE -r KINESIS_STREAM_REGION -n KINESIS_STREAM_NAME
  ~~~
  From the terminal print out, you should see that the stream shard iterator has been initialized and the app is now waiting to receive messages from the stream.
