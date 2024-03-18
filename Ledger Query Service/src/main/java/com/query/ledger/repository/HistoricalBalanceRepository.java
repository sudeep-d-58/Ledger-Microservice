package com.query.ledger.repository;

import com.query.ledger.model.HistoricalBalanceData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoricalBalanceRepository extends JpaRepository<HistoricalBalanceData, Long> {

    @Query(value = "Select * from historical_balance_data where DATE(date)= :date and wallet_id= :walletId order by date", nativeQuery = true)
    List<HistoricalBalanceData> findBySpecificDateAndWalletID(@Param("date") String date, Long walletId);

    @Query(value = "Select * from historical_balance_data where DATE(date) between :startDate and :endDate and wallet_id= :walletId  order by date", nativeQuery = true)
    List<HistoricalBalanceData> findByDateRangeAndWalletID(@Param("startDate") String startDate, @Param("endDate") String endDate, Long walletId);


    @Query(value = "Select * from historical_balance_data where wallet_id= :walletId", nativeQuery = true)
    List<HistoricalBalanceData> findAllTransactionForWallet(Long walletId);

    @Query(value = "Select * from historical_balance_data where TIMESTAMP(date) between timestamp(:startTimeStamp) and timestamp(:endTimeStamp) and wallet_id= :walletId  order by date", nativeQuery = true)
    List<HistoricalBalanceData> findBetweenSpecificTimeStampAndWalletID(@Param("startTimeStamp") String startTimeStamp, @Param("endTimeStamp") String endTimeStamp, Long walletId);
}
