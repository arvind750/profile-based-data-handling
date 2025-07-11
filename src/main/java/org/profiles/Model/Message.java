package org.profiles.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Min(value = 0, message = "Age must be non-negative")
    private int age;

    @NotBlank(message = "Profile name cannot be blank")
    private String profileName;

    @NotBlank(message = "Message cannot be blank")
    @Size(max = 255, message = "Message cannot exceed 255 characters")
    private String message;
}