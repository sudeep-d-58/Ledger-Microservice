**Usage**

**Get All Details for a wallet**

Endpoint : /GET localhost:8081/api/query/getAll/{walletId}

Example Response Payload:

    [
    {
    "id": 51,
    "walletId": 10001,
    "prevBalance": 0.00,
    "currBalance": 300.00,
    "transactionId": null,
    "eventType": "Wallet Creation",
    "amount": null,
    "accountState": "OPEN",
    "date": "2024-03-18T06:04:24.937+00:00"
    }
    ]

Assumption : Returns Empty List in case of Data not present


**Get All Details for a wallet for a Date**

Endpoint : /GET localhost:8081/api/query/getAll/{walletId}/2024-03-15

Example Response Payload:

    [
    {
    "id": 51,
    "walletId": 10001,
    "prevBalance": 0.00,
    "currBalance": 300.00,
    "transactionId": null,
    "eventType": "Wallet Creation",
    "amount": null,
    "accountState": "OPEN",
    "date": "2024-03-18T06:04:24.937+00:00"
    }
    ]

Assumption : Returns Empty List in case of Data not present


**Get All Details for a wallet for a Date Range**

Example Endpoint : /GET localhost:8081/api/query/getAll/{walletId}/2024-03-15/2024-03-18

Example Response Payload:

    [
    {
    "id": 51,
    "walletId": 10001,
    "prevBalance": 0.00,
    "currBalance": 300.00,
    "transactionId": null,
    "eventType": "Wallet Creation",
    "amount": null,
    "accountState": "OPEN",
    "date": "2024-03-18T06:04:24.937+00:00"
    }
    ]

Assumption : Returns Empty List in case of Data not present



**Get All Details for a wallet for a TimeStamp Range**

Example Endpoint : /GET localhost:8081/api/query/getAllAtSpecificTimeStamp/10001

Example Request Payload :
    {
    "startTimeStamp": "2024-03-18 06:00:00",
    "endTimeStamp": "2024-03-19 12:00:00"
    }

Example Response Payload:
    [
    {
    "id": 51,
    "walletId": 10001,
    "prevBalance": 0.00,
    "currBalance": 300.00,
    "transactionId": null,
    "eventType": "Wallet Creation",
    "amount": null,
    "accountState": "OPEN",
    "date": "2024-03-18T06:04:24.937+00:00"
    }
    ]

Assumption : Returns Empty List in case of Data not present