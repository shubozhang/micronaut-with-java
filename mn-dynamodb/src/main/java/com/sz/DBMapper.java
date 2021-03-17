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
    private static final String TABLE_NAME = "Music";
    private static final String LSI_ALBUM_INDEX_NAME =  "lsi_album";
    private static final String GSI_UPC_INDEX_NAME = "gsi_upc";
    private static AmazonDynamoDB client;
    private static DynamoDBMapper mapper;
    private static Index lsi_album;
    private static Index gsi_upc;

    static {
        AWSCredentialsProvider creds = new AWSCredentialsProvider() {
            @Override
            public AWSCredentials getCredentials() {
                return new BasicAWSCredentials("local", "local");
            }
            @Override
            public void refresh() { }
        };

        client = AmazonDynamoDBClientBuilder.standard()
                .withCredentials(creds)
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-east-1"))
                .build();

        mapper = new DynamoDBMapper(client);
        DynamoDB dynamoDb = new DynamoDB(client);
        Table table = dynamoDb.getTable(TABLE_NAME);
        lsi_album = table.getIndex(LSI_ALBUM_INDEX_NAME);
        gsi_upc = table.getIndex(GSI_UPC_INDEX_NAME);

    }

    public static DynamoDBMapper getMapper(){ return mapper; }

    public static Index getLSIAlbumTitle() { return lsi_album; }

    public static Index getGSIUpc() { return gsi_upc; }
}
