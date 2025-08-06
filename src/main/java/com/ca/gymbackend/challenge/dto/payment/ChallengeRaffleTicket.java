package com.ca.gymbackend.challenge.dto.payment;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ChallengeRaffleTicket {
    private Integer raffleTicketId;
    private Integer userId;
    private Integer challengeId;
    private Integer raffleTicketCount;
    private String raffleTicketSourceType;
    private LocalDateTime createdAt;
}
