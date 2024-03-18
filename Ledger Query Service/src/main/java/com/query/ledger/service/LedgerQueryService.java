package com.query.ledger.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.query.ledger.model.HistoricalBalanceData;
import com.query.ledger.model.TimeStampObject;
import com.query.ledger.repository.HistoricalBalanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;

import static com.query.ledger.util.QueryUtility.Transaction_Group;
import static com.query.ledger.util.QueryUtility.Wallet_Data_Publisher_1;

@Service
@Slf4j
public class LedgerQueryService {

    @Autowired
    private HistoricalBalanceRepository historicalBalanceRepository;

    @Autowired
    private ObjectMapper objectMapper;


    @KafkaListener(topics = Wallet_Data_Publisher_1, groupId = Transaction_Group, containerFactory = "myConsumerFactory")
    public void consumeTransactionMessage(LinkedHashMap map) {
        HistoricalBalanceData historicalBalance = objectMapper.convertValue(map, HistoricalBalanceData.class);
        log.info("Consumed historicalBalance {} ", historicalBalance.toString());
        historicalBalanceRepository.save(historicalBalance);
    }

    public List<HistoricalBalanceData> findBySpecificDateAndWalletID(String date, Long walletId) {
        List<HistoricalBalanceData> list = historicalBalanceRepository.findBySpecificDateAndWalletID(date, walletId);
        return list;
    }

    public List<HistoricalBalanceData> findByDateRangeAndWalletID(String startDate, String endDate, Long walletId) {
        List<HistoricalBalanceData> list = historicalBalanceRepository.findByDateRangeAndWalletID(startDate, endDate, walletId);
        return list;
    }

    public List<HistoricalBalanceData> findAllTransactionForWallet(Long walletId) {
        List<HistoricalBalanceData> list = historicalBalanceRepository.findAllTransactionForWallet(walletId);
        return list;
    }

    public List<HistoricalBalanceData> findBetweenSpecificTimeStampAndWalletID(Long walletId, TimeStampObject timeStampObject) {
        List<HistoricalBalanceData> list = historicalBalanceRepository.findBetweenSpecificTimeStampAndWalletID(timeStampObject.getStartTimeStamp(), timeStampObject.getEndTimeStamp(), walletId);
        return list;
    }
}
