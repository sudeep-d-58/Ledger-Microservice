package com.command.ledger.model;


import lombok.*;

import java.math.BigDecimal;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ListTransactionRequest {

    private long fromWallet;

    private long toWallet;

    private BigDecimal amount;
}
