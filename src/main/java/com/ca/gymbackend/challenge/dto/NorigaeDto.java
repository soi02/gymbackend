package com.ca.gymbackend.challenge.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class NorigaeDto {
    private String norigaeTierName;     
    private String norigaeTierDescription; 
    private String norigaeTierIconPath;  
    private LocalDate awardedDate;
}
