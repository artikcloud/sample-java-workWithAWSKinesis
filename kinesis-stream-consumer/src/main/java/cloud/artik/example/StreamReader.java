package cloud.artik.example;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONObject;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.kinesis.*;
import com.amazonaws.services.kinesis.model.*;
import com.amazonaws.regions.*;

/**
 * Kinesis stream consumer App that shows received ARTIK Cloud messages
 *
 */
public class StreamReader 
{
    private static final int EXPECTED_ARGUMENT_NUMBER = 8;

    private String amazonAccessKey = null;
    private String amazonSecretKey = null;
    private String amazonStreamName = null;
    private String amazonRegionName = null;


    public static void main( String[] args )
    {
    	StreamReader streamReader = new StreamReader();
        if (!streamReader.succeedParseCommand(args)) {
            return;
        }
        
        streamReader.readFromStream();
    }//end of Main
    
    private void readFromStream() {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(amazonAccessKey, amazonSecretKey);
		@SuppressWarnings("deprecation")
		AmazonKinesisClient client = new AmazonKinesisClient(awsCredentials);
		client.setRegion(RegionUtils.getRegion(amazonRegionName));
         
		long recordNum = 0;
		final int INTERVAL = 2000;

        // Getting initial stream description from aws
		System.out.println(client.describeStream(amazonStreamName).toString());
        List<Shard> initialShardData = client.describeStream(amazonStreamName).getStreamDescription().getShards();
        System.out.println("\nlist of shards:");
        initialShardData.forEach(d->System.out.println(d.toString()));
         
        // Getting shardIterators (at beginning sequence number) for reach shard
        List<String> initialShardIterators = initialShardData.stream().map(s -> 
             client.getShardIterator(new GetShardIteratorRequest()
                .withStreamName(amazonStreamName)
                .withShardId(s.getShardId())
                .withStartingSequenceNumber(s.getSequenceNumberRange().getStartingSequenceNumber())
                .withShardIteratorType(ShardIteratorType.AT_SEQUENCE_NUMBER)
                ).getShardIterator()
        ).collect(Collectors.toList());

        System.out.println("\nlist of ShardIterators:");
        initialShardIterators.forEach(i -> System.out.println(i));
        System.out.println("\nwaiting for messages....");

        // WARNING!!! Assume that only have one shard. So only use that shard
        String shardIterator = initialShardIterators.get(0);
        
        // Continuously read data records from a shard
        while (true) {
           // Create a new getRecordsRequest with an existing shardIterator 
            // Set the maximum records to return to 25
            GetRecordsRequest getRecordsRequest = new GetRecordsRequest();
            getRecordsRequest.setShardIterator(shardIterator);
            getRecordsRequest.setLimit(25); 

            GetRecordsResult recordResult = client.getRecords(getRecordsRequest);
           
	        recordResult.getRecords().forEach(record -> {
	      	try {
	      		String rec = new String(record.getData().array(), "UTF-8");
	              JSONObject fromKinesis = new JSONObject(rec);
	              System.out.println("\nKinesis record: " + record.toString());
	              System.out.println("ARTIK Cloud message: " + fromKinesis.toString());
	          } catch (UnsupportedEncodingException e) {
     	          System.out.println("Could not decode message from Kinesis stream result");
	              e.printStackTrace();
	          }
	        });

	        recordNum += recordResult.getRecords().size();
	        System.out.println("\nReceived " + recordNum +" records. sleep for " + INTERVAL/1000 +"s ...");
            try {
                Thread.sleep(INTERVAL);
            } catch (InterruptedException exception) {
        	    System.out.println("Receving InterruptedException. Exiting ...");
        	    return;
            }
            shardIterator = recordResult.getNextShardIterator();
        }
    	
    }
    
    ////////////////////////////////////////////
    // Helper functions
    private boolean succeedParseCommand(String args[]) {
       // java -jar target/read_stream.jar -k AWS_KEY -s AWS_SECRETE -r KINESIS_STREAM_REGION -n KINESIS_STREAM_NAME
       if (args.length != EXPECTED_ARGUMENT_NUMBER) {
           printUsage();
           return false; 
       }
       int index = 0;
       while (index < args.length) {
           String arg = args[index];
           if ("-k".equals(arg)) {
               ++index; // Move to the next argument the value of device id
               amazonAccessKey = args[index];
           } else if ("-s".equals(arg)) {
               ++index; // Move to the next argument the value of device token
               amazonSecretKey = args[index];
           } else if("-r".equals(arg)) {
               ++index; // Move to the next argument the value of device token
               amazonRegionName = args[index];
           } else if ("-n".equals(arg)) {
               ++index; // Move to the next argument the value of firmware version after update
               amazonStreamName = args[index];
           }
           ++index;
       }
       if (amazonAccessKey == null || amazonSecretKey == null || amazonRegionName == null || amazonStreamName == null) {
           printUsage();
           return false;
       }
       System.out.println("key:" + amazonAccessKey + " secrete:" + amazonSecretKey + " region:" + amazonRegionName + " stream:" + amazonStreamName);
       return true;
   }
   
   private static void printUsage() {
       System.out.println("Usage: " + "read-stream -k AWS_KEY -s AWS_SECRETE -r KINESIS_STREAM_REGION -n KINESIS_STREAM_NAME");
   }

}
