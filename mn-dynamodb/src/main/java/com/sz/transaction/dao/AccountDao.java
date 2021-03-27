package com.sz.transaction.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.sz.DBMapper;
import com.sz.transaction.model.Account;

import javax.inject.Singleton;

@Singleton
public class AccountDao {
    private final DynamoDBMapper mapper = DBMapper.getMapper();

    public Account getItem(Account account) { return mapper.load(account); }

    public void saveItem(Account account) {
        mapper.save(account);
    }

    public void deleteItem(Account account) {
        mapper.delete(account);
    }
}
