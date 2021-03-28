package com.sz.transaction;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.InternalServerErrorException;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.model.TransactionCanceledException;
import com.sz.DBMapper;
import com.sz.transaction.dao.AccountDao;
import com.sz.transaction.dao.OrderDao;
import com.sz.transaction.dao.RoomDao;
import com.sz.transaction.model.Account;
import com.sz.transaction.model.Order;
import com.sz.transaction.model.Room;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
public class TransactionTest {

    @Inject
    AccountDao accountDao;

    @Inject
    RoomDao roomDao;

    @Inject
    OrderDao orderDao;

    private static final DynamoDBMapper mapper = DBMapper.getMapper();

    @BeforeAll
    public static void setUp(){
        insertAccounts(5);
        insertRooms(5);
    }

    @AfterAll
    public static void tearDown() {
        deleteAccounts(5);
        deleteRooms(5);
    }

    @Test
    public void testCreateOrder() {
        Order order = new Order();
        order.setOrderId("1");
        order.setAccountId("A1");
        order.setRoomId("R1");

        createOrder(order);

        List<Object> objects = readOrder(order);
        Order resOrder = (Order) objects.get(0);
        Room resRoom = (Room) objects.get(1);
        Account resAccount = (Account) objects.get(2);

        assertThat(resOrder).isEqualTo(order);
        assertThat(resAccount).isEqualTo(new Account(order.getAccountId()));
        assertTrue(resRoom.getRoomId().equals(order.getRoomId())
                && resRoom.getAvailable().equals("NO"));

        // clean up
        deleteOrder(order);
    }

    @Test
    public void testInvalidAccountException() {
        Order order = new Order();
        order.setOrderId("2");
        order.setAccountId("A");
        order.setRoomId("R3");

        String res = createOrder(order);
        assertTrue(res.contains("Transaction Canceled, implies a client issue, fix before retrying. Error: "));
        assertTrue(res.contains("[ConditionalCheckFailed, None, None]"));
    }

    @Test
    public void testInvalidRoomException() {
        Order order = new Order();
        order.setOrderId("2");
        order.setAccountId("A2");
        order.setRoomId("R");

        String res = createOrder(order);
        assertTrue(res.contains("Transaction Canceled, implies a client issue, fix before retrying. Error: "));
        assertTrue(res.contains("[None, ConditionalCheckFailed, None]"));
    }

    @Test
    public void testAllInvalidException() {
        Order order = new Order();
        order.setOrderId("2");
        order.setAccountId("A2");
        order.setRoomId("R2");

        String res = createOrder(order);
        assertTrue(res.equals("SUCCESS"));

        Order order2 = new Order();
        order2.setOrderId("2");
        order2.setAccountId("NONE");
        order2.setRoomId("NONE");

        String res2 = createOrder(order2);
        assertTrue(res2.contains("Transaction Canceled, implies a client issue, fix before retrying. Error: "));
        assertTrue(res2.contains("[ConditionalCheckFailed, ConditionalCheckFailed, ConditionalCheckFailed]"));

        // clean up
        deleteOrder(order);
    }

    @Test
    public void testDupRoomException() {
        Order order = new Order();
        order.setOrderId("2");
        order.setAccountId("A2");
        order.setRoomId("R2");

        String res = createOrder(order);
        assertTrue(res.equals("SUCCESS"));

        Order order2 = new Order();
        order2.setOrderId("3");
        order2.setAccountId("A3");
        order2.setRoomId("R2");

        String res2 = createOrder(order2);
        assertTrue(res2.contains("Transaction Canceled, implies a client issue, fix before retrying. Error: "));
        assertTrue(res2.contains("[None, ConditionalCheckFailed, None]"));

        // clean up
        deleteOrder(order);
    }
    @Test
    public void testDupOrderException() {
        Order order = new Order();
        order.setOrderId("2");
        order.setAccountId("A2");
        order.setRoomId("R2");

        String res = createOrder(order);
        assertTrue(res.equals("SUCCESS"));

        Order order2 = new Order();
        order2.setOrderId("2");
        order2.setAccountId("A3");
        order2.setRoomId("R3");

        String res2 = createOrder(order2);
        assertTrue(res2.contains("Transaction Canceled, implies a client issue, fix before retrying. Error: "));
        assertTrue(res2.contains("[None, None, ConditionalCheckFailed]"));

        // clean up
        deleteOrder(order);
    }

    private List<Object> readOrder(Order order) {
        TransactionLoadRequest transactionLoadRequest = new TransactionLoadRequest();
        transactionLoadRequest.addLoad(order);
        transactionLoadRequest.addLoad(new Room(order.getRoomId()));
        transactionLoadRequest.addLoad(new Account(order.getAccountId()));

        return executeTransactionLoad(transactionLoadRequest);
    }

    private String createOrder(Order order) {
        DynamoDBTransactionWriteExpression checkAccount = new DynamoDBTransactionWriteExpression()
                .withConditionExpression("attribute_exists(AccountId)");

        Map<String, AttributeValue> roomUpdateValues = new HashMap<>();
        roomUpdateValues.put(":pre_status", new AttributeValue("YES"));
        DynamoDBTransactionWriteExpression checkRoom = new DynamoDBTransactionWriteExpression()
                .withExpressionAttributeValues(roomUpdateValues)
                .withConditionExpression("Available = :pre_status");

        DynamoDBTransactionWriteExpression checkOrder = new DynamoDBTransactionWriteExpression()
                .withConditionExpression("attribute_not_exists(OrderId)");

        TransactionWriteRequest transactionWriteRequest = new TransactionWriteRequest();
        transactionWriteRequest.addConditionCheck(new Account(order.getAccountId()), checkAccount);
        transactionWriteRequest.addUpdate(new Room(order.getRoomId(), "NO"), checkRoom);
        transactionWriteRequest.addPut(order, checkOrder);

        return executeTransactionWrite(transactionWriteRequest);
    }

    private void deleteOrder(Order order) {
        Map<String, AttributeValue> roomUpdateValues = new HashMap<>();
        roomUpdateValues.put(":pre_status", new AttributeValue("NO"));
        DynamoDBTransactionWriteExpression checkRoom = new DynamoDBTransactionWriteExpression()
                .withExpressionAttributeValues(roomUpdateValues)
                .withConditionExpression("Available = :pre_status");
        TransactionWriteRequest transactionWriteRequest = new TransactionWriteRequest();
        transactionWriteRequest.addUpdate(new Room(order.getRoomId(), "YES"), checkRoom);
        transactionWriteRequest.addDelete(order);

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

    private static String executeTransactionWrite(TransactionWriteRequest transactionWriteRequest) {
        try {
            mapper.transactionWrite(transactionWriteRequest);
        } catch (DynamoDBMappingException ddbme) {
            return ("Client side error in Mapper, fix before retrying. Error: " + ddbme.getMessage());
        } catch (ResourceNotFoundException rnfe) {
            return ("One of the tables was not found, verify table exists before retrying. Error: " + rnfe.getMessage());
        } catch (InternalServerErrorException ise) {
            return ("Internal Server Error, generally safe to retry with back-off. Error: " + ise.getMessage());
        } catch (TransactionCanceledException tce) {
            return ("Transaction Canceled, implies a client issue, fix before retrying. Error: " + tce.getMessage());
        } catch (Exception ex) {
            return ("An exception occurred, investigate and configure retry strategy. Error: " + ex.getMessage());
        }
        return "SUCCESS";
    }

    private static void insertRooms(Integer count) {
        RoomDao roomDao = new RoomDao();
        for (int i = 0; i < count; i++) {
            roomDao.saveItem(new Room("R"+i, "YES"));
        }
    }

    private static void insertAccounts(Integer count) {
        AccountDao accountDao = new AccountDao();
        for (int i = 0; i < count; i++) {
            accountDao.saveItem(new Account("A"+i));
        }
    }

    private static void deleteRooms(Integer count) {
        RoomDao roomDao = new RoomDao();
        for (int i = 0; i < count; i++) {
            roomDao.deleteItem(new Room("R"+i, "YES"));
        }
    }

    private static void deleteAccounts(Integer count) {
        AccountDao accountDao = new AccountDao();
        for (int i = 0; i < count; i++) {
            accountDao.deleteItem(new Account("A"+i));
        }
    }
}
