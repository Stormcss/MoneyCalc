package ru.strcss.projects.moneycalc.moneycalcmigrator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.Status;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.SpendingSection;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Transaction;
import ru.strcss.projects.moneycalc.moneycalcmigrator.api.FileReader;
import ru.strcss.projects.moneycalc.moneycalcmigrator.dto.PairFilesContainer;
import ru.strcss.projects.moneycalc.moneycalcmigrator.properties.MigrationProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ru.strcss.projects.moneycalc.moneycalcmigrator.utils.GenerationUtils.generateAccess;
import static ru.strcss.projects.moneycalc.moneycalcmigrator.utils.GenerationUtils.generateSpendingSection;

@Slf4j
@Component
class FileParser {

    private final MigrationProperties properties;
    private final ServerConnector serverConnector;
    private final FileReader fileReader;

    private int transactionsAdded;
    private int spendingSectionsAdded;

    @Autowired
    public FileParser(MigrationProperties properties, ServerConnector serverConnector, FileReader fileReader) {
        this.properties = properties;
        this.serverConnector = serverConnector;
        this.fileReader = fileReader;
    }

    /**
     * Parse data files for outdated version of MoneyCalc
     */
    void parseOldFiles(boolean printStatistics) {
        log.debug("Pairing Data and Info files...");
        Map<String, PairFilesContainer> filesEntries = fileReader.groupFiles(properties.getDataPath());

        log.info("filesEntries: {}", filesEntries);

        log.debug("Logging in ...");
        String token = serverConnector.login(generateAccess(properties));

        log.info("token = " + token);

        log.debug("Getting section names ...");
        List<SpendingSection> personSectionsList = serverConnector.getSectionsList(token);

        for (Map.Entry<String, PairFilesContainer> pair : filesEntries.entrySet()) {
            Set<String> sectionsInFile = fileReader.parseDataFile(properties.getDataPath(), pair.getValue().getPathDataFile());

            List<SpendingSection> spendingSectionsTemp = new ArrayList<>(personSectionsList);
            for (String sectionInFile : sectionsInFile) {
                if (spendingSectionsTemp.stream().noneMatch(spendingSection -> spendingSection.getName().equals(sectionInFile))) {
                    personSectionsList = serverConnector.saveSpendingSection(token, generateSpendingSection(sectionInFile));
                }
            }

            List<Transaction> transactionsInFile = fileReader.parseInfoFile(properties.getDataPath(), pair.getValue().getPathInfoFile());

            Status savingStatus = serverConnector.saveTransactions(token, transactionsInFile, properties.getLogin());

            if (Status.SUCCESS.equals(savingStatus))
                transactionsAdded += transactionsInFile.size();

            if (Status.SUCCESS.equals(savingStatus))
                log.debug("Saving Transactions status is {}. Saved {} transactions from file {}",
                        savingStatus, transactionsInFile.size(), pair.getValue().getPathInfoFile());
            else
                log.debug("Saving Transactions status is {}", savingStatus);
        }
        spendingSectionsAdded += personSectionsList.size();

        if (printStatistics) {
            log.info("Added " + transactionsAdded + " transactions.");
            log.info("Added " + spendingSectionsAdded + " spendingSections.");
        }

    }


//    private List<Transaction> getTransactionList(Map<String, PairFilesContainer> filesEntries, List<SpendingSection> spendingSections) {
//        List<Transaction> transactions = new ArrayList<>();
//        for (PairFilesContainer pair : filesEntries.values()) {
//            transactions.addAll(fileReader.parseInfoFile(properties.getDataPath(), pair.getPathInfoFile()));
//        }
//        return transactions;
//    }
//
//    private Set<String> getSectionNames(Map<String, PairFilesContainer> filesEntries) {
//        Set<String> sectionNames = new HashSet<>();
//
//        for (PairFilesContainer pair : filesEntries.values()) {
////            sectionNames.addAll(parseDataFile(pair.getPathDataFile()));
//            sectionNames.addAll(fileReader.parseDataFile(properties.getDataPath(), pair.getPathDataFile()));
//        }
//        return sectionNames;
//    }


}
