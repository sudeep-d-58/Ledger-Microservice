package com.command.ledger.model;

import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HistoricalBalance {

    private long walletId;

    private BigDecimal prevBalance;

    private BigDecimal currBalance;

    private Long transactionId;

    private String eventType;

    private BigDecimal amount;

    private AccountState accountState;

    private Date date;
}
