package com.query.ledger.controller;


import com.query.ledger.model.HistoricalBalanceData;
import com.query.ledger.model.TimeStampObject;
import com.query.ledger.service.LedgerQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    @Operation(summary = "test", description = "Test method to check if api is up and running")
    public String test() {
        return "Hello";
    }

    @Operation(summary = "Get All Details for a wallet")
    @GetMapping("/getAll/{walletId}")
    public ResponseEntity<List<HistoricalBalanceData>> getAllTransactionsForWallet(@PathVariable @Parameter(example = "10002") Long walletId) {
        List<HistoricalBalanceData> historicalBalances = ledgerQueryService.findAllTransactionForWallet(walletId);
        return new ResponseEntity<>(historicalBalances, HttpStatus.OK);
    }


    @Operation(summary = "Get All Details for a wallet for a Date")
    @GetMapping("/getAll/{walletId}/{date}")
    public ResponseEntity<List<HistoricalBalanceData>> findBySpecificDateAndWalletID(@PathVariable @Parameter(example = "10002") Long walletId, @PathVariable @Parameter(example = "2024-03-15") String date) {
        List<HistoricalBalanceData> historicalBalances = ledgerQueryService.findBySpecificDateAndWalletID(date, walletId);
        return new ResponseEntity<>(historicalBalances, HttpStatus.OK);
    }

    @Operation(summary = "Get All Details for a wallet for a Date Range")
    @GetMapping("/getAll/{walletId}/{startDate}/{endDate}")
    public ResponseEntity<List<HistoricalBalanceData>> findBySpecificDateAndWalletID(@PathVariable @Parameter(example = "10002") Long walletId, @PathVariable @Parameter(example = "2024-03-15") String startDate,
                                                                                     @PathVariable @Parameter(example = "2024-03-16") String endDate) {
        List<HistoricalBalanceData> historicalBalances = ledgerQueryService.findByDateRangeAndWalletID(startDate, endDate, walletId);
        return new ResponseEntity<>(historicalBalances, HttpStatus.OK);
    }

    @Operation(summary = "Get All Details for a wallet for a TimeStamp Range")
    @GetMapping("/getAllAtSpecificTimeStamp/{walletId}")
    public ResponseEntity<List<HistoricalBalanceData>> findBetweenSpecificTimeStampAndWalletID(@PathVariable @Parameter(example = "10002") @NotNull Long walletId,
                                                                                               @RequestBody TimeStampObject timeStampObject) {
        List<HistoricalBalanceData> historicalBalances = ledgerQueryService.findBetweenSpecificTimeStampAndWalletID(walletId,timeStampObject);
        return new ResponseEntity<>(historicalBalances, HttpStatus.OK);
    }
}
