package ru.strcss.projects.moneycalcserver.enitities.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Builder
@Getter
public class Transaction {
    private Date date;
    private int sum;
    private String description;
    private String sectionID;
}
