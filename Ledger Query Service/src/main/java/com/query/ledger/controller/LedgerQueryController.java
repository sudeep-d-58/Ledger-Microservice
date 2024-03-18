package com.query.ledger.controller;


import com.query.ledger.model.HistoricalBalanceData;
import com.query.ledger.model.TimeStampObject;
import com.query.ledger.service.LedgerQueryService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/query")
public class LedgerQueryController {

    @Autowired
    private LedgerQueryService ledgerQueryService;

    @GetMapping("/test")
    public String test() {
        return "Hello";
    }

    @GetMapping("/getAll/{walletId}")
    public ResponseEntity<List<HistoricalBalanceData>> getAllTransactionsForWallet(@PathVariable Long walletId) {
        List<HistoricalBalanceData> historicalBalances = ledgerQueryService.findAllTransactionForWallet(walletId);
        return new ResponseEntity<>(historicalBalances, HttpStatus.OK);
    }


    @GetMapping("/getAll/{walletId}/{date}")
    public ResponseEntity<List<HistoricalBalanceData>> findBySpecificDateAndWalletID(@PathVariable Long walletId, @PathVariable String date) {
        List<HistoricalBalanceData> historicalBalances = ledgerQueryService.findBySpecificDateAndWalletID(date, walletId);
        return new ResponseEntity<>(historicalBalances, HttpStatus.OK);
    }

    @GetMapping("/getAll/{walletId}/{startDate}/{endDate}")
    public ResponseEntity<List<HistoricalBalanceData>> findBySpecificDateAndWalletID(@PathVariable Long walletId, @PathVariable String startDate, @PathVariable String endDate) {
        List<HistoricalBalanceData> historicalBalances = ledgerQueryService.findByDateRangeAndWalletID(startDate, endDate, walletId);
        return new ResponseEntity<>(historicalBalances, HttpStatus.OK);
    }

    @GetMapping("/getAllAtSpecificTimeStamp/{walletId}")
    public ResponseEntity<List<HistoricalBalanceData>> findBetweenSpecificTimeStampAndWalletID(@PathVariable @NotNull Long walletId, @RequestBody TimeStampObject timeStampObject) {
        List<HistoricalBalanceData> historicalBalances = ledgerQueryService.findBetweenSpecificTimeStampAndWalletID(walletId,timeStampObject);
        return new ResponseEntity<>(historicalBalances, HttpStatus.OK);
    }
}
