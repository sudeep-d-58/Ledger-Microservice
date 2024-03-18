package com.command.ledger.publisher;

import com.command.ledger.model.MessageEvent;


public interface MessagePublisher {

    void publish(MessageEvent event);
}
