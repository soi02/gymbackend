package com.ca.gymbackend.challenge.dto.payment;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ChallengePayment {
    private Integer challengePaymentId;
    private Integer userId;
    private Integer challengeId;
    private Integer challengePaymentAmount;
    private String challengePaymentStatus;
    private String challengePaymentTid;
    private String challengePaymentPgToken;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}
