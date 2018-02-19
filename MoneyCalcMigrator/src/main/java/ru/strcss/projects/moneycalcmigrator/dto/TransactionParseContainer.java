package ru.strcss.projects.moneycalcmigrator.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionParseContainer {
    private String date;
    private int id;
    private int sum;
    private String description;
}
