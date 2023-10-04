package com.acgustafson;

import java.io.Serializable;

public class HelloMessage implements Serializable {
    private final String messageContent = "abc123";

    public String getTestString() {
        return this.messageContent;
    }
}
