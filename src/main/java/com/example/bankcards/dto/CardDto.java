package com.example.bankcards.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CardDto {

    private Long id;

    @NotNull
    @Size(min = 19, max = 19)
    @Pattern(regexp = "^\\d{4} \\d{4} \\d{4} \\d{4}$")
    private String cardNumber;

    private String status;

    private LocalDate expireDate;

    private BigDecimal balance;

}
