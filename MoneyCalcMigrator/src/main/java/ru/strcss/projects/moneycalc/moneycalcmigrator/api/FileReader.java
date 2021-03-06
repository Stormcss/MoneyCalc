package ru.strcss.projects.moneycalc.moneycalcmigrator.api;

import ru.strcss.projects.moneycalc.moneycalcdto.entities.Transaction;
import ru.strcss.projects.moneycalc.moneycalcmigrator.model.dto.PairFilesContainer;

import java.util.List;
import java.util.Map;

public interface FileReader {

    /**
     * Get Map of date and grouped Files
     */
    Map<String, PairFilesContainer> groupFiles(String filesPath);

    /**
     * Get list of sections per Data file
     */
    List<String> parseDataFile(String folderPath, String fileName);

    /**
     * Get list of Transactions per Info file
     */
    List<Transaction> parseInfoFile(String folderPath, String fileName, Map<Integer, Integer> idMapper);
}
