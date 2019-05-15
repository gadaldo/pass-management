package com.gadaldo.leisure.pass.rest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseBody
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -2749823569349376330L;

    public ResourceNotFoundException(String message) {
        super(message);
    }

    ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
