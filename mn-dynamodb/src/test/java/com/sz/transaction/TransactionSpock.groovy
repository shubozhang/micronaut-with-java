package com.sz.transaction

import com.amazonaws.services.dynamodbv2.datamodeling.*
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.InternalServerErrorException
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException
import com.amazonaws.services.dynamodbv2.model.TransactionCanceledException
import com.sz.DBMapper
import com.sz.transaction.dao.AccountDao
import com.sz.transaction.dao.OrderDao
import com.sz.transaction.dao.RoomDao
import com.sz.transaction.model.Account
import com.sz.transaction.model.Order
import com.sz.transaction.model.Room
import spock.lang.Specification

import javax.inject.Inject

class TransactionSpock extends Specification{
    @Inject
    AccountDao accountDao;

    @Inject
    RoomDao roomDao;

    @Inject
    OrderDao orderDao;

    private static final DynamoDBMapper mapper = DBMapper.getMapper();


    def "should fail by invalid account"() {
        when:
        def order = new Order("100", "None", "R5")
        createOrder(order)

        then:
        thrown DynamoDBMappingException

    }

    private List<Object> readOrder(Order order) {
        TransactionLoadRequest transactionLoadRequest = new TransactionLoadRequest();
        transactionLoadRequest.addLoad(order);
        transactionLoadRequest.addLoad(new Room(order.getRoomId()));
        transactionLoadRequest.addLoad(new Account(order.getAccountId()));

        return executeTransactionLoad(transactionLoadRequest);
    }

    private void createOrder(Order order) {
        DynamoDBTransactionWriteExpression checkAccount = new DynamoDBTransactionWriteExpression()
                .withConditionExpression("attribute_exists(AccountId)");

        Map<String, AttributeValue> roomUpdateValues = new HashMap<>();
        roomUpdateValues.put(":pre_status", new AttributeValue("YES"));
        DynamoDBTransactionWriteExpression checkRoom = new DynamoDBTransactionWriteExpression()
                .withExpressionAttributeValues(roomUpdateValues)
                .withConditionExpression("Available = :pre_status");
        TransactionWriteRequest transactionWriteRequest = new TransactionWriteRequest();
        transactionWriteRequest.addConditionCheck(new Account(order.getAccountId()), checkAccount);
        transactionWriteRequest.addUpdate(new Room(order.getRoomId(), "NO"), checkRoom);
        transactionWriteRequest.addPut(order);

        executeTransactionWrite(transactionWriteRequest);
    }

    private static List<Object> executeTransactionLoad(TransactionLoadRequest transactionLoadRequest) {
        List<Object> loadedObjects = new ArrayList<Object>();
        try {
            loadedObjects = mapper.transactionLoad(transactionLoadRequest);
        } catch (DynamoDBMappingException ddbme) {
            System.err.println("Client side error in Mapper, fix before retrying. Error: " + ddbme.getMessage());
        } catch (ResourceNotFoundException rnfe) {
            System.err.println("One of the tables was not found, verify table exists before retrying. Error: " + rnfe.getMessage());
        } catch (InternalServerErrorException ise) {
            System.err.println("Internal Server Error, generally safe to retry with back-off. Error: " + ise.getMessage());
        } catch (TransactionCanceledException tce) {
            System.err.println("Transaction Canceled, implies a client issue, fix before retrying. Error: " + tce.getMessage());
        } catch (Exception ex) {
            System.err.println("An exception occurred, investigate and configure retry strategy. Error: " + ex.getMessage());
        }
        return loadedObjects;
    }
    private static void executeTransactionWrite(TransactionWriteRequest transactionWriteRequest) {
        try {
            mapper.transactionWrite(transactionWriteRequest);
        } catch (DynamoDBMappingException ddbme) {
            System.err.println("Client side error in Mapper, fix before retrying. Error: " + ddbme.getMessage());
        } catch (ResourceNotFoundException rnfe) {
            System.err.println("One of the tables was not found, verify table exists before retrying. Error: " + rnfe.getMessage());
        } catch (InternalServerErrorException ise) {
            System.err.println("Internal Server Error, generally safe to retry with back-off. Error: " + ise.getMessage());
        } catch (TransactionCanceledException tce) {
            System.err.println("Transaction Canceled, implies a client issue, fix before retrying. Error: " + tce.getMessage());
        } catch (Exception ex) {
            System.err.println("An exception occurred, investigate and configure retry strategy. Error: " + ex.getMessage());
        }
    }
}
