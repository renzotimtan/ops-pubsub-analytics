package com.renzotimtan.ops_analytics.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    @Schema(hidden = true)
    private String id;
    
    private String name;
    private String email;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }
    
}
