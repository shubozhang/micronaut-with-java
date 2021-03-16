# Dockerized Postgres

https://hub.docker.com/_/postgres

## Ephemeral Postgres instances (Data will be gone after restart)
This is the quickest way to get started:
```
docker run --name mn-pg -e POSTGRES_PASSWORD=mn-pg -e POSTGRES_DB=mn-pg -p 5432:5432 -d postgres:12.4
```

* User: postgres
* Password: mn-pg
* Database: mn-pg

## Docker Compose
Execute from root directory:
```
docker-compose up -d
```

* Contains a volume for permanent storage of data. On system restart the data is available again.
* It contains PgAdmin that is available at localhost:7777
* Also, Postgres can be hooked up with IDEA Database tool
