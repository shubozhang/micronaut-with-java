package com.sz.transaction.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.sz.DBMapper;
import com.sz.transaction.model.Order;

import javax.inject.Singleton;

@Singleton
public class OrderDao {
    private final DynamoDBMapper mapper = DBMapper.getMapper();

    public Order getItem(Order order) { return mapper.load(order); }

    public void saveItem(Order order) {
        mapper.save(order);
    }

    public void deleteItem(Order order) {
        mapper.delete(order);
    }
}
