package com.command.ledger.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EnumWrapper {

    @NotNull
    private String value;
}
