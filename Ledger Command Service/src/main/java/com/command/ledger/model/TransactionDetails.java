package com.command.ledger.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDetails {

    @NotNull
    private TransactionState transactionState;
}
