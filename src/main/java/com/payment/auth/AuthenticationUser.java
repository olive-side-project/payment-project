package com.payment.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationUser {
    private long userSeq;
    private String email;
    private boolean loginComplete;
}
