package ru.strcss.projects.moneycalcserver.enitities.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class Transaction {
    private String date;
    private int sum;
    private String currency;
    private String description;
    private String sectionID;
}


