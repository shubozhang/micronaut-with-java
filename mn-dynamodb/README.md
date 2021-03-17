# Micronaut DynamoDB


### Start Local DynamoDB


```bash
AWS_REGION=eu-west-1 AWS_ACCESS_KEY_ID=local AWS_SECRET_ACCESS_KEY=local dynamodb-admin
```

### Create DynamoDB Table
```bash
### Create DynamoDB Table
```bash
aws dynamodb create-table --cli-input-json file://dynamo_table_def.json --endpoint-url http://localhost:8000
```
```

