package ru.strcss.projects.moneycalcmigrator.dto;

import lombok.Data;

@Data
public class PairFilesContainer {
    private String pathDataFile;
    private String pathInfoFile;

    public PairFilesContainer(String pathDataFile, String pathInfoFile) {

        this.pathDataFile = pathDataFile;
        this.pathInfoFile = pathInfoFile;
    }
}
