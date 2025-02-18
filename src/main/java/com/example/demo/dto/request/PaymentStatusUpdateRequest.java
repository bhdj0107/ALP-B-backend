package com.example.demo.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class PaymentStatusUpdateRequest {
    private String merchantUid;
    private String status;
    private String approvalNumber;
    private LocalDateTime approvedAt;
}
