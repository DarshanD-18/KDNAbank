package com.bank.banking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountHolderDTO {

    private Long accountId;
    private String customerName;
    private String accountNumber;
    private double balance;
}
