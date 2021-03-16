package com.sz;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Table;

public class DBMapper {

    private static DynamoDBMapper mapper;
    private static final String TABLE_NAME = "Music";
    private static final String LSI_ALBUM =  "lsi_album";
    private static final String GSI_UPC = "gsi_upc";

    private static AmazonDynamoDB client;

    private static void setClient() {
        AWSCredentialsProvider creds = new AWSCredentialsProvider() {
            @Override
            public AWSCredentials getCredentials() {
                return new BasicAWSCredentials("local", "local");
            }

            @Override
            public void refresh() {
            }
        };

        client = AmazonDynamoDBClientBuilder.standard()
                .withCredentials(creds)
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-east-1"))
                .build();

    }


    public static DynamoDBMapper getMapper(){
        DynamoDBMapper mapper;
        if (client == null) {
            setClient();
        }
        mapper = new DynamoDBMapper(client);
        return mapper;
    }

    public static Index getLSIAlbumTitle() {
        if (client == null) {
            setClient();
        }
        DynamoDB dynamoDb = new DynamoDB(client);
        Table table = dynamoDb.getTable(TABLE_NAME);
        return table.getIndex(LSI_ALBUM);
    }

    public static Index getGSIUpc() {
        if (client == null) {
            setClient();
        }
        DynamoDB dynamoDb = new DynamoDB(client);
        Table table = dynamoDb.getTable(TABLE_NAME);
        return table.getIndex(GSI_UPC);
    }
}
