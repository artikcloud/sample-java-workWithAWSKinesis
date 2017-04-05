# Build a sample system using ARTIK Cloud and Kinesis Stream integration 

The sample system has the following functionalities:
 1. Subscribe [AWS Kinesis Stream](http://docs.aws.amazon.com/streams/latest/dev/introduction.html) to ARTIK Cloud
 2. Devices send data to ARTIK Cloud
 3. Read device data from Kinesis Stream

We build two applications to achieve Item 1 and 3, and use the Device Simulator instead of real devices to achieve Item 2. 

After completing this sample, you will learn the following objectives:

- How to [subscribe Kinesis Stream to ARTIK Cloud](https://developer.artik.cloud/documentation/connect-the-data/push-to-amazon-kinesis.html) via API calls
- How to read data from Kinesis Stream using AWS SDK for Java
- How to [delete a subscription](https://developer.artik.cloud/documentation/api-reference/rest-api.html#delete-a-subscription) in ARTIK Cloud via API calls

## Requirements
- Java version >= 1.8
- Apache Maven >= 3.3.9
- [AWS SDK for Java](https://aws.amazon.com/developers/getting-started/java/)
- [Amazon Kinesis Streams setup](http://docs.aws.amazon.com/streams/latest/dev/before-you-begin.html)

## Setup / Installation

### Setup at ARTIK Cloud

 1. Open [My ARTIK Cloud](https://my.artik.cloud/) and [connect a device](/documentation/tools/web-tools.html#connecting-a-device) (or use one that you already own) of the device type (for example, Demo Fire Sensor with unique name `cloud.artik.sample.demofiresensor`). You will use the device simulator to send data to ARTIK Cloud on behalf of this device later. 
 2. Open the [API Console](https://developer.artik.cloud/documentation/tools/api-console.html) and follow the instruction to [get the access token](https://developer.artik.cloud/documentation/introduction/hello-world.html#step-2-get-an-access-token). This access token is your [user token](https://developer.artik.cloud/documentation/introduction/authentication.html#user-token). Then [get your user ID](https://developer.artik.cloud/documentation/tools/api-console.html#find-your-user-id). Note the user ID and access token. You will need them later.
 
### Setup at Amazon AWS

 1. [Sign up for an AWS account](http://docs.aws.amazon.com/streams/latest/dev/before-you-begin.html#setting-up-sign-up-for-aws) if you have not done so.
 2. Get the AWS access key and secret of your AWS user/role. You will need them later. 
 3. Create a Kinesis stream with *one* shard on aws site. For example, you can do it using the [AWS Kinesis Console](http://docs.aws.amazon.com/streams/latest/dev/managing-streams-console.html). Noted the stream name (e.g. akcstream) and service region (e.g. us-west-1). You will need them later.

### Setup two Java projects

 1. Clone this repository if you haven't already done so.
 2. Build the subscription app. Go to `sub-kinesis-stream-to-akc` directory and run the following command:
  ~~~shell
  mvn clean package
  ~~~
  The executable `sub-kinesis-to-akc-x.x.jar` is generated under the target directory. Run the following command to learn the usage:
  ~~~shell
  java -jar sub-kinesis-to-akc-x.x.jar -h
  ~~~

  3. Build the Stream reader app. Go to `kinesis-stream-consumer` directory from the root and run the following command:
  ~~~shell
  mvn clean package
  ~~~
  The executable `read-stream-x.x.jar` is generated under the target directory. Run the following command to learn the usage:
  ~~~shell
  java -jar read-stream-x.x.jar -h
  ~~~

## Send data to ARTIK Cloud and read them from Kinesis stream

Read the blog post [How to send ARTIK Cloud data to an Amazon Kinesis Stream](https://www.artik.io/blog/2017/04/how-to-send-artik-cloud-data-to-an-amazon-kinesis-stream/#section-demo) for the full details!
