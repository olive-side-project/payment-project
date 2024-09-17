package com.module.payment.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EncryptedValue {
    private int keyIndex;
    private int ivIndex;
    private String encryptedValue;
}
