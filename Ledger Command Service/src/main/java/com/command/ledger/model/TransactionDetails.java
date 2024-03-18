package com.command.ledger.model;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDetails {

    private TransactionState transactionState;
}
