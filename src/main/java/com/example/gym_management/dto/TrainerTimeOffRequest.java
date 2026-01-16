package com.example.gym_management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Trainer time-off request")
public class TrainerTimeOffRequest {

    @NotNull(message = "Date is required")
    private LocalDate date;

    @Size(max = 255, message = "Reason cannot exceed 255 characters")
    private String reason;
}
