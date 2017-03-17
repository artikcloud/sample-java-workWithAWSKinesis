package cloud.artik.example;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Subscribe Amazon Kinesis stream to ARTIK Cloud
 * https://developer.artik.cloud/documentation/connect-the-data/push-to-amazon-kinesis.html
 *
 */
public class Subscriber 
{
    private String amazonAccessKey = null;
    private String amazonSecretKey = null;
    private String amazonStreamName = null;
    private String amazonRegionName = null;
    private String artikcloudUid = null;
    private String artikcloudAccessToken = null;

    private static final int EXPECTED_ARGUMENT_NUMBER = 12;
    private final String SUBSCRIPTION_URL = "https://api.artik.cloud/v1.1/subscriptions";

    public static void main( String[] args )
    {
        Subscriber subscriber = new Subscriber();
        
        if (!subscriber.succeedParseCommand(args))
            return;
            
        try {
            subscriber.subToARTIKCloud();
        } catch (Exception e) {
            System.out.println("Receving exeption. Exiting ...");
            e.printStackTrace(System.out);
            return;
        }
    }
    
    // HTTP Post request
    private void subToARTIKCloud() throws Exception {
		 URL obj = new URL(SUBSCRIPTION_URL);
		 HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		
		 // Setting post request
		 con.setRequestMethod("POST");
		 con.setRequestProperty("Authorization", "Bearer " + artikcloudAccessToken);
		 con.setRequestProperty("Content-Type", "application/json");
		 
		 // example of post data
		 /*     {
		         "messageType": "message",
		         "uid": "240bc34cf61348e6a3255fe5d8539484",
		         "subscriptionType": "awsKinesis",
		         "awsKey": "AKYICRWQ",
		         "awsSecret": "nwwx",
		         "awsRegion": "us-west-1",
		         "awsKinesisStreamName": "akcstream",
		         "description": "This is a subscription to user devices"
		         }
		 */
		 String postJsonData = "{\"messageType\": \"message\"," 
		     + "\"uid\": \"" + artikcloudUid + "\","
		     + "\"subscriptionType\": \"awsKinesis\","
		     + "\"awsKey\": \"" + amazonAccessKey + "\","
		     + "\"awsSecret\": \"" + amazonSecretKey + "\","
		     + "\"awsRegion\": \"" + amazonRegionName + "\","
		     + "\"awsKinesisStreamName\": \"" + amazonStreamName + "\","
		     + "\"description\": \"This is a subscription to user devices\"}";
		 System.out.println("\nSending 'POST' request to URL : " + SUBSCRIPTION_URL);
		 System.out.println("Request body : " + postJsonData);
		
		 // Send post request
		 con.setDoOutput(true);
		 DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		 wr.writeBytes(postJsonData);
		 wr.flush();
		 wr.close();
		
		 BufferedReader in = new BufferedReader(
		         new InputStreamReader(con.getInputStream()));
		 String output;
		 StringBuffer response = new StringBuffer();
		 while ((output = in.readLine()) != null) {
		  response.append(output);
		 }
		 in.close();
		 
		 //printing result from response
		 int responseCode = con.getResponseCode();
		 System.out.println("\nresponse code : " + responseCode);
		 System.out.println("response : " + response.toString());
    }

    ////////////////////////////////////////////
    // Helper functions
    private boolean succeedParseCommand(String args[]) {
        // java -jar target/sub-kinesis-to-akc.jar -u ARITKCLOUD_UID -t ARTIKCLOUD_TOKEN -k AWS_KEY -s AWS_SECRETE -r KINESIS_STREAM_REGION -n KINESIS_STREAM_NAME
        if (args.length != EXPECTED_ARGUMENT_NUMBER) {
            printUsage();
            return false; 
        }
        int index = 0;
        while (index < args.length) {
            String arg = args[index];
            if ("-u".equals(arg)) {
                ++index; 
                artikcloudUid = args[index];
            } else if ("-t".equals(arg)) {
                ++index; 
                artikcloudAccessToken = args[index];
            } else if ("-k".equals(arg)) {
                ++index; 
                amazonAccessKey = args[index];
            } else if ("-s".equals(arg)) {
                ++index;
                amazonSecretKey = args[index];
            } else if("-r".equals(arg)) {
                ++index;
                amazonRegionName = args[index];
            } else if ("-n".equals(arg)) {
                ++index;
                amazonStreamName = args[index];
            }
            ++index;
        }
        if (artikcloudUid == null
                || artikcloudAccessToken == null
                || amazonAccessKey == null 
                || amazonSecretKey == null 
                || amazonRegionName == null 
                || amazonStreamName == null
            ) {
            printUsage();
            return false;
        }
        System.out.println("key:" + amazonAccessKey + " secrete:" + amazonSecretKey + " region:" + amazonRegionName + " stream:" + amazonStreamName);
        return true;
    }
    
    private static void printUsage() {
        System.out.println("Usage: " + "sub-kinesis-to-akc -u ARITKCLOUD_UID -t ARTIKCLOUD_TOKEN -k AWS_KEY -s AWS_SECRETE -r KINESIS_STREAM_REGION -n KINESIS_STREAM_NAME");
    }
}
