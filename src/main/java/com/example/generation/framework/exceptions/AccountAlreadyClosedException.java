package com.example.generation.framework.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountAlreadyClosedException extends RuntimeException{
    private String message;
    public AccountAlreadyClosedException(String message){
        super(message);
    }
}
