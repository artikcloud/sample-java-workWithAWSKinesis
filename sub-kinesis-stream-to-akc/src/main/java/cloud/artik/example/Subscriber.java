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
 * Or delete the subscription
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
    private String artikcloudSubscriptionID = null;
    private boolean createSubscription = true;

    private static final int EXPECTED_ARGUMENT_NUMBER_SUB = 12;
    private static final int EXPECTED_ARGUMENT_NUMBER_DELETE_SUB = 4;
    private final String SUBSCRIPTION_URL = "https://api.artik.cloud/v1.1/subscriptions";

    public static void main( String[] args )
    {
        Subscriber subscriber = new Subscriber();
        
        if (!subscriber.succeedParseCommand(args))
            return;
            
        try {
            if (subscriber.createSubscription) {
                subscriber.subToARTIKCloud();
            } else {
                subscriber.deleteSubscription();
            }
            
        } catch (Exception e) {
            System.out.println("Receving exeption. Exiting ...");
            e.printStackTrace(System.out);
            return;
        }
    }
    
    // HTTP Post request
    private void subToARTIKCloud() throws Exception {
        System.out.println("\nSubscribing Kinesis to ARTIK Cloud ... ");
        String postJsonData = "{\"messageType\": \"message\"," 
                + "\"uid\": \"" + artikcloudUid + "\","
                + "\"subscriptionType\": \"awsKinesis\","
                + "\"awsKey\": \"" + amazonAccessKey + "\","
                + "\"awsSecret\": \"" + amazonSecretKey + "\","
                + "\"awsRegion\": \"" + amazonRegionName + "\","
                + "\"awsKinesisStreamName\": \"" + amazonStreamName + "\","
                + "\"description\": \"This is a subscription to user devices\"}";
        sendToARTIKCloud(SUBSCRIPTION_URL, "POST", postJsonData, artikcloudAccessToken);
    }

    // HTTP DELETE request
    // DELETE .../subscriptions/<subscriptionID>
    private void deleteSubscription() throws Exception {
        System.out.println("\nDeleting subscription with id " + artikcloudSubscriptionID);
        String deleteURL = SUBSCRIPTION_URL + "/" + artikcloudSubscriptionID;
        String postJsonData ="\"{}\""; //empty body
        
        sendToARTIKCloud(deleteURL, "DELETE", postJsonData, artikcloudAccessToken);
    }
    
    private void sendToARTIKCloud(String url, String method, String jsonBody, String accessToken) throws Exception {
         URL obj = new URL(url);
         HttpURLConnection con = (HttpURLConnection) obj.openConnection();
         
         // Setting delete request
         con.setRequestMethod(method);
         con.setRequestProperty("Authorization", "Bearer " + accessToken);
         con.setRequestProperty("Content-Type", "application/json");
         
         System.out.println("\nSending '" + method +"' request to URL : " + url);
         System.out.println("Request body: " + jsonBody);
        
         // Send post request
         con.setDoOutput(true);
         DataOutputStream wr = new DataOutputStream(con.getOutputStream());
         wr.writeBytes(jsonBody);
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
    
    // 1. usage help:
	// java -jar target/sub-kinesis-to-akc.jar -h
    // 2. subscribe
    // java -jar target/sub-kinesis-to-akc.jar -u ARITKCLOUD_UID -t ARTIKCLOUD_TOKEN -k AWS_KEY -s AWS_SECRETE -r KINESIS_STREAM_REGION -n KINESIS_STREAM_NAME
	// 3. delete subscription
    // java -jar target/sub-kinesis-to-akc.jar -del ARTIKCLOUD_SUBSCRIPTION_ID -t ARTIKCLOUD_TOKEN
    private boolean succeedParseCommand(String args[]) {
    	if ("-h".equals(args[0])) {
    		printUsage();
    		return false;
    	}
    	if (args.length != EXPECTED_ARGUMENT_NUMBER_SUB 
            && args.length != EXPECTED_ARGUMENT_NUMBER_DELETE_SUB) {
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
            } else if ("-del".equals(arg)) { // delete subscription
                ++index;
                artikcloudSubscriptionID = args[index];
                createSubscription = false;
            }
            ++index;
        }
        
        // create subscription
        if (createSubscription) {
             if (artikcloudUid == null
                || artikcloudAccessToken == null
                || amazonAccessKey == null 
                || amazonSecretKey == null 
                || amazonRegionName == null 
                || amazonStreamName == null
                ) {  // invalid input arguments
                    printUsage();
                    return false;
             } else { // valid input arguments
                 System.out.println("key:" + amazonAccessKey + " secrete:" + amazonSecretKey + " region:" + amazonRegionName + " stream:" + amazonStreamName);
             }
             return true;
        }
        
        // delete subscription
        if (artikcloudSubscriptionID == null || artikcloudAccessToken == null) { // invalid input arguments
            printUsage();
            return false;
        }
        System.out.println("ARTIKCLOUD_SUBSCRIPTION_ID:" + artikcloudSubscriptionID + " ARTIKCLOUD_TOKEN:" + artikcloudAccessToken);
        return true;
    }
    
    private static void printUsage() {
        System.out.println("Usages \nSubscribe Kinesis stream to ARTIK Cloud:\n" + "sub-kinesis-to-akc -u ARITKCLOUD_UID -t ARTIKCLOUD_TOKEN -k AWS_KEY -s AWS_SECRETE -r KINESIS_STREAM_REGION -n KINESIS_STREAM_NAME");
        System.out.println("\nDelete Kinesis stream subscription:\n" + "sub-kinesis-to-akc -del ARTIKCLOUD_SUBSCRIPTION_ID -t ARTIKCLOUD_TOKEN");
    }
}
