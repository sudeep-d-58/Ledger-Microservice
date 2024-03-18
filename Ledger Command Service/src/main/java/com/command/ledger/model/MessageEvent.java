package com.command.ledger.model;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageEvent {

    private String destination;
    private Object payload;
}
