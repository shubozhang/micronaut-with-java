# DynamoDB Transactions

DynamoDB is a horizontally scaled NoSQL database. Previously, it did not support transaction that is a unit of work 
and should be either all succeed or fail. That means you can't concurrently update multiple tables, 
multiple rows in transaction way, all or nothing. Developers had challenges to manually implement transaction in DynamoDB. 

Now DynamoDB supports transactions. It simplifies the developer experience of making coordinated, all-or-nothing 
changes to multiple items both within and across tables. DynamoDB's transaction provides atomicity, consistency, 
isolation, and durability (ACID) in DynamoDB, helping you to maintain data correctness in your applications.
So transaction is not a deal breaker for Dynamodb anymore.


### Use cases of Transactions
* Financial transactions
* Order processing
* Coordinate actions in multi-players game
* Updated distributed data

### Transactional API

* TransactWriteItems
    * Include up to 25 distinct items per transaction. The aggregate size of the items in the transaction cannot 
      exceed 4 MB (single item can't exceed 400 KB). The actions are completed atomically so that either all of them succeed or none of them succeeds.
    * You can't target the same item with multiple operations within the same transaction.
    * Evaluate conditions
    * If all conditions are simultaneously true, perform write operations
    * Consume 2 WCUs for every 1 KB of data
    * Use client token to keep idempotency (10 mins)
    * Error Handling for Writing: Write transactions don't succeed under the following circumstances:
        * When a condition in one of the condition expressions is not met.
        * When a transaction validation error occurs because more than one action in the same  TransactWriteItems operation targets the same item.
        * When a TransactWriteItems request conflicts with an ongoing TransactWriteItems operation on one or more items in the 
          TransactWriteItems request. In this case, the request fails with a TransactionCanceledException.
        * When there is insufficient provisioned capacity for the transaction to be completed.
        * When an item size becomes too large(larger than 400 KB), or a local secondary index(LSI) becomes too large, or a similar 
          validation error occurs because of changes made by the transaction.
        * When there is a user error, such as an invalid data format.


* TransactReadItems
    * Include up to 25 distinct items per transaction. The aggregate size of the items in the transaction can't exceed 4 MB.
    * Return a consistent, isolated snapshot of all times.
    * Consumes 2 RCUs for every 4 KB of data.
    * Error Handling for Reading
        * When a TransactGetItems request conflicts with an ongoing TransactWriteItems operation on one or more items in the TransactGetItems request. 
          In this case, the request fails with a TransactionCanceledException. 
        * When there is insufficient provisioned capacity for the transaction to be completed. 
        * When there is a user error, such as an invalid data format.



| | Non-Transactional (Singleton) | Transactional|
|---|---|---|
|Reads|GetItem </br> Query </br> Scan </br> BatchGetItem | TransactionGetItems|
|Writes|PutItem </br> UpdateItem </br> DeleteItem </br> BatchWriteItem: it is possible that only some of the actions in the batch succeed while the others do not. | TransactWriteItems (put/update/delete/conditionCheck)|



* Pitfalls:
    * write collision: need to handle retry
    * 25 items max limit in one transaction
    * You can't target the same item more than once in one transaction.
    * Double the cost since it uses consistent write / read. You still need to pay for failed transaction.


### DynamoDB flexibility
* The same transaction can include items:
    * with the same or different partition key
    * within the same table of across different DynamoDB tables.
    
* All items within a single transaction must be:
    * Within the same AWS Region
    * Within the same AWS Account
    * Within DynamoDB
    
### DynamoDB Scale
Transactions scale like the rest of DynamoDB:
* Virtually limitless concurrent transactions
* Low latency, high availability
* Never deadlocks
* Request/response-based implementation
* Scales horizontally



### Capacity Management for Transactions
There is no additional cost to enable transactions for your DynamoDB tables. You pay only for the reads or writes that are part of your transaction. 
DynamoDB performs two underlying reads or writes of every item in the transaction: one to prepare the transaction and one to commit the transaction. 
The two underlying read/write operations are visible in your Amazon CloudWatch metrics.
Plan for the additional reads and writes that are required by transactional APIs when you are provisioning capacity to your tables. 
For example, suppose that your application runs one transaction per second, and each transaction writes three 500-byte items in your table. 
Each item requires two write capacity units (WCUs): one to prepare the transaction and one to commit the transaction. Therefore, you would need to provision six WCUs to the table.
If you were using DynamoDB Accelerator (DAX) in the previous example, you would also use two read capacity units (RCUs) for each item 
in the TransactWriteItems call. So you would need to provision six additional RCUs to the table.
Similarly, if your application runs one read transaction per second, and each transaction reads three 500- byte items in your table, 
you would need to provision six read capacity units (RCUs) to the table. Reading each item require two RCUs: one to prepare the transaction and one to commit the transaction.
Also, default SDK behavior is to retry transactions in case of a TransactionInProgressException exception. Plan for the additional 
read-capacity units (RCUs) that these retries consume. The same is true if you are retrying transactions in your own code using a ClientRequestToken.

### Best Practices for Transactions

* Enable automatic scaling on your tables, or ensure that you have provisioned enough throughput capacity to perform the 
  two read or write operations for every item in your transaction.
* If you are not using an AWS provided SDK, include a ClientRequestToken attribute when you make a TransactWriteItems call to ensure that the request is idempotent.
* Don't group operations together in a transaction if it's not necessary. For example,if a single transaction with 10 operations 
  can be broken up into multiple transactions without compromising the application correctness, we recommend splitting up the transaction. 
  Simpler transactions improve throughput and are more likely to succeed.
* Multiple transactions updating the same items simultaneously can cause conflicts that cancel the transactions. We recommend following 
  DynamoDB best practices for data modeling to minimize such conflicts.
* If a set of attributes is of ten updated across multiple items as part of a single transaction,consider grouping the attributes 
  into a single item to reduce the scope of the transaction.
* Avoid using transactions for ingesting data in bulk. For bulk writes, it is better to use BatchWriteItem.


### Using Transactional APIs with Global Tables
Transactional operations provide atomicity, consistency, isolation, and durability (ACID) guarantees only within 
the region where the write is made originally. Transactions are not supported across regions in global tables. 
For example, if you have a global table with replicas in the US East (Ohio) and US West (Oregon) regions and 
perform a TransactWriteItems operation in the US East (N. Virginia) Region, you may observe partially completed 
transactions in US West (Oregon) Region as changes are replicated. Changes will only be replicated to other regions 
once they have been committed in the source region.


