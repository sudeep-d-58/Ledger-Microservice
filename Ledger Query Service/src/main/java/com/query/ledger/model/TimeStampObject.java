package com.query.ledger.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimeStampObject {

    @NotNull
    @Schema(example = "2024-03-18 06:00:00")
    private String startTimeStamp;

    @NotNull
    @Schema(example = "2024-03-18 12:00:00")
    private String endTimeStamp;
}
