**Usage**

**Account Creation**

Endpoint : /POST/ localhost:8080/api/command/create/account

Example Payload :
    {
    "accountName": "ABC",
    "entityId": "8743"
    }

Assumptions: Account can be created without an Wallet Being Present. An entity can have multiple accounts. No checks


**Wallet Addition to Account**

Endpoint : /POST localhost:8080/api/command/add/wallet/{accountId}

Example Payload :
    {
    "assetType": "FIAT",
    "balance": 100
    }

Assumptions: AssetType is not handled as ENUM. Can Implement more checks based on this. Also, this adds Balance to a wallet amd Also Transmits event to wallet-data-publisher-1 topic

**Close Account**

Endpoint : /PATCH localhost:8080/api/command/close/account/{accountId}

Assumptions: If account is not already closed, then it closes the account and updates DB. Does not emits any event (Can be done if needed).

**Account State Change**

Endpoint : /PATCH localhost:8080/api/command/changeState/account/{accountId}

Example Payload :
    {
    "value": "OPEN",
    }

Assumptions: No Checks present for keeping simple. One can change between any state without any restriction. Can be updated with more checks if needed.


**Transfer Amount**

Endpoint : /POST localhost:8080/api/command/transfer/{fromWallet}/{toWallet}

Example Payload :
    {
    "amount": 21.56
    }

Assumptions: Used for Single Transaction. Returns the Transaction with ID, state and few other details. Updates the Wallet and Transaction DB. Emits Event to transaction-data-publisher-1 for success and failure both.
Maintains ACID Properties w.r.t Wallet Balance. Emits event to wallet-data-publisher-1 which is consumed by Query Service for updating Historical Balance. Uses Spring Retry Module 
for 3 times retry and then generate failed transaction.


**Transfers Balance Between wallets in Async Mode(Simultaneously)**

Endpoint : /POST localhost:8080/api/command/transfer/all

Example Payload :
    [
    {
    "fromWallet": 10003,
    "toWallet": 10004,
    "amount": 10.00
    },
    {
    "fromWallet": 10004,
    "toWallet": 10003,
    "amount": 60.00
    },
    {
    "fromWallet": 10003,
    "toWallet": 10004,
    "amount": 10.00
    }
    ]

Assumptions: Used for Multiple Transaction at same time(async way). Maintains All or Noting in Partial Manner. For any failure in Transaction is the request , It will generate a Failed
Transaction Object and for the rest it will complete the Transaction and return all the transaction result (Both Failed and Success). It will still maintain the ACID property as
Wallet balances are consistent after combination of success and failures as well. Can be implemented as Roll Back of All transaction if failure Occurred if needed. Emits Event to transaction-data-publisher-1 for success and failure both.
Maintains ACID Properties w.r.t Wallet Balance. Emits event to wallet-data-publisher-1 which is consumed by Query Service for updating Historical Balance.Uses Spring Retry Module
for 3 times retry and then generate failed transaction.

**Update Wallet Balance**

Endpoint : /PATCH localhost:8080/api/command/updateBalance/wallet/{walletId}

Example Payload :
    {
    "newBalance": 21.56
    }

Assumptions: To keep simple, no Checks present for Updating Balance except for Wallet Presence and Account being not Closed (Logic can be improved to manage balance). Emits event to wallet-data-publisher-1 topic

**Transaction Status Update**

Endpoint : /PATCH localhost:8080/api/command/transaction/{transactionId}/changeState

Example Payload :
    {
    "transactionState": PENDING
    }

Assumptions: To keep simple, no Checks present for Updating Transaction State for Transaction Presence in DB  (Logic can be improved to manage better). Emits event to transaction-data-publisher-2 topic
