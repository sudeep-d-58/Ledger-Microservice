**Project Description** :

Ledger Microservice for source of truth for balances of assets and liabilities of an entity.

**Technology**:

Java 21
SpringBoot 3.2 
MySQL
Docker
Kafka
zookeeper

**Prerequisite** :

Docker Installed. 
Docker Compose Installed. 
Postman to test REST Endpoints.


**Run :**

1.Navigate to project directory (Ledger-Microservice) and run docker-compose up --build -d 
2. It will automatically pull kafka, zookeeper and mysql, java images and configure them and start them and eventually start the spring boot applications 
3. All the hosts and ports are configured in docker compose file and since this is running the containerized env, we don't need to install anything except docker and docker compose.

**Architecture** :

Keeping in mind the requirement, The Ledger Microservice internally runs two services.

1. To serve to write requests
2. to Serve to read requests.

This is done using the CQRS Pattern. The Databases are synced by kafka broker. All times configured to be in UTC.


    **Ledger-Command-Service**
* This is service is running in port 8080 and serving to all write requests. It is using its own mysql database with 3 main tables Account(To Store the accounts owner by an entity), 
Wallet(to store the data of wallets) and transaction(to store all transaction). 
* JPA Versioning is used for Database Locking and checks(Inbuilt offering by JPA)
* As per requirement , this service emits events for any changes in Wallet Balance add/change Event, New Transaction event and Change in existing Event.
* We are using Kafka as message broker, but the code has been written as loosely coupled. In case of any future changes in Message Broker we can easily switch to Solace, ActiveMQ etc.
* Any interested parties can listen to the above transmitted events.
* This service emits Wallet Change event in a specific format , which in turn is consumed by the Other Ledger-Query-Service and stored in DB , which is used to serve the user queries.
* Since We have Separated the read and write , this service can work efficiently without the read overhead
* All Object relations are met as per requirements.
* We can easily update the event objects as per our requirement.
* interaction with Client is via Rest Endpoints , but can be converted to Async(Listening to events and consuming and processing them) if needed.
* Specific Usage Manual is available in Ledger-Command-Service/LedgerCommands.md file.

    **Ledger Query Service**
* This service is running in 8081 port and serving to all read requests. It is using its own mysql database with 1 main table Historical_Balance_Data. This is used to store all wallet
activities.
* This service consumes Wallet Balance Change Activity event and uses them to store into DB. The Database is configurable and can be updated as needed with necessary fields.
* Since this is dockerized , this can be scaled in an container env as needed.
* Specific Usage Manual is available in Ledger Query Service/UserInstructions.md file.

