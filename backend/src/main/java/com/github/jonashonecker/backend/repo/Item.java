package com.github.jonashonecker.backend.repo;

import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@With
@Document("items")
public record Item (
        @Id
        String id,
        String description,
        Status status
) {
}
