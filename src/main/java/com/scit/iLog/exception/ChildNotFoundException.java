package com.scit.iLog.exception;

public class ChildNotFoundException extends RuntimeException{
    public ChildNotFoundException(Long childId) {
        super(childId.toString());
    }
}
