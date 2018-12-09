package ru.strcss.projects.moneycalcmigrator.api;

import ru.strcss.projects.moneycalc.entities.Transaction;
import ru.strcss.projects.moneycalcmigrator.dto.PairFilesContainer;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface FileReader {

    /**
     * Get Map of date and grouped Files
     */
    Map<String, PairFilesContainer> groupFiles(String filesPath);

    /**
     * Get list of sections per Data file
     */
    Set<String> parseDataFile(String folderPath, String fileName);

    /**
     * Get list of Transactions per Info file
     */
    List<Transaction> parseInfoFile(String folderPath, String fileName);
}
