package com.command.ledger.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBalance {

    @NotNull
    private BigDecimal newBalance;
}
