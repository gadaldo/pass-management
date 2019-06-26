package com.gadaldo.leisure.pass.rest.controller;

public class ResourceNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -2749823569349376330L;

    public ResourceNotFoundException(String message) {
        super(message);
    }

}
