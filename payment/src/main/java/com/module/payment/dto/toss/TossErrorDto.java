package com.module.payment.dto.toss;

import com.module.payment.dto.ApiError;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TossErrorDto implements ApiError {
    private String code;
    private String message;
}
