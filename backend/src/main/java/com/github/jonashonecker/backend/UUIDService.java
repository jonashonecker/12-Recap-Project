package com.github.jonashonecker.backend;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UUIDService {
    public String generate () {
        return UUID.randomUUID().toString();
    }
}
