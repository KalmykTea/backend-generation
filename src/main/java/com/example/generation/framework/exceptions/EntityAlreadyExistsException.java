package com.example.generation.framework.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntityAlreadyExistsException extends RuntimeException{
    private String field;
    private String message;
    public EntityAlreadyExistsException(String field, String message){
        super(message);
        this.field = field;
        this.message = message;
    }
}
