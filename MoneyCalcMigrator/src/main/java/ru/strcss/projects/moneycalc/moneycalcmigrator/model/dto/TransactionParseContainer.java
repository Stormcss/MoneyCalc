package ru.strcss.projects.moneycalc.moneycalcmigrator.model.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class TransactionParseContainer {
    private LocalDate date;
    private int id;
    private int sum;
    private String description;
}
