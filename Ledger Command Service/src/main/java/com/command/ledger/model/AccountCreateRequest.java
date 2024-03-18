package com.command.ledger.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountCreateRequest {

    @NotNull
    private String accountName;

    @NotNull
    private long entityId;

}
