package com.github.jonashonecker.backend.exceptions;

import java.util.NoSuchElementException;

public class IdNotFoundException extends NoSuchElementException {
    public IdNotFoundException(String message) {
        super(message);
    }
}
