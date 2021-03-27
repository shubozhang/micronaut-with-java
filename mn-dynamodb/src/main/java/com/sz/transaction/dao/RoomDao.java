package com.sz.transaction.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.sz.DBMapper;
import com.sz.transaction.model.Room;

import javax.inject.Singleton;

@Singleton
public class RoomDao {
    private final DynamoDBMapper mapper = DBMapper.getMapper();

    public Room getItem(Room room) { return mapper.load(room); }

    public void saveItem(Room room) { mapper.save(room); }

    public void deleteItem(Room room) {
        mapper.delete(room);
    }
}
