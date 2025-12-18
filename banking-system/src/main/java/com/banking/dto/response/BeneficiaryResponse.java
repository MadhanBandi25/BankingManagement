package com.banking.dto.response;

import com.banking.entity.BeneficiaryStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BeneficiaryResponse {

    private Long beneficiaryId;

    private Long customerId;
    private String  customerName;

    private String beneficiaryName;
    private String beneficiaryAccountNumber;
    private String bankName;
    private String ifscCode;

    private BeneficiaryStatus status;
    private LocalDateTime createdAt;
}
