package ru.strcss.projects.moneycalc.moneycalcmigrator.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PairFilesContainer {
    private String pathDataFile;
    private String pathInfoFile;
}
