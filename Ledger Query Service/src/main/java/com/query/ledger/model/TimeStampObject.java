package com.query.ledger.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimeStampObject {

    @NotNull
    private String startTimeStamp;

    @NotNull
    private String endTimeStamp;
}
