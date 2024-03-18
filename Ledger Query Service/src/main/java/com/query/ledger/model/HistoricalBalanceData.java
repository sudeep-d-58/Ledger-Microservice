package com.query.ledger.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@TableGenerator(name = "tab3", initialValue = 50, allocationSize = 90)
public class HistoricalBalanceData {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "tab3")
    private long id;

    private long walletId;

    private BigDecimal prevBalance;

    private BigDecimal currBalance;

    private Long transactionId;

    private String eventType;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column
    private AccountState accountState;

    private Date date;
}
