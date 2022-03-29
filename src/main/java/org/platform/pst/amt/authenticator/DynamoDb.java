package org.platform.pst.amt.authenticator;

import org.jboss.logging.Logger;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class  DynamoDb {
    private static final Logger logger = Logger.getLogger(DynamoDb.class);

    public static boolean checkEmail (String email) {
        Region region = Region.US_GOV_WEST_1;
        DynamoDbAsyncClient client = DynamoDbAsyncClient.builder()
                .region(region)
                .credentialsProvider(()-> AwsBasicCredentials.create(System.getenv("AWS_ACCESS_KEY"), System.getenv("AWS_SECRET_KEY")))
                .build();
        HashMap<String, AttributeValue> keyToGet = new HashMap<>();
        keyToGet.put("email", AttributeValue.builder()
                .s(email).build());

        try {
            GetItemRequest request = GetItemRequest.builder()
                    .key(keyToGet)
                    .tableName(System.getenv("DYNAMODB_TABLE"))
                    .build();

            java.util.Collection<AttributeValue> returnedItem = client.getItem(request).join().item().values();
            Map<String, AttributeValue> map = returnedItem.stream().collect(Collectors.toMap(AttributeValue::s, s->s));
            return null != map.get(email);

        } catch (DynamoDbException e) {
           logger.info(e.getMessage());
        } finally {
           client.close();
        }
        return false;
    }
}
