package ru.strcss.projects.moneycalcmigrator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.strcss.projects.moneycalc.dto.Status;
import ru.strcss.projects.moneycalc.enitities.SpendingSection;
import ru.strcss.projects.moneycalc.enitities.Transaction;
import ru.strcss.projects.moneycalcmigrator.api.FileReaderI;
import ru.strcss.projects.moneycalcmigrator.dto.ConfigContainer;
import ru.strcss.projects.moneycalcmigrator.dto.PairFilesContainer;

import java.util.*;

import static ru.strcss.projects.moneycalcmigrator.utils.GenerationUtils.generateAccess;
import static ru.strcss.projects.moneycalcmigrator.utils.GenerationUtils.generateSpendingSection;

@Slf4j
@Component
class Parser {

    private final ConfigContainer config;
    private final ServerConnector serverConnector;
    private final FileReaderI fileReader;

    @Autowired
    public Parser(ConfigContainer config, ServerConnector serverConnector, FileReaderI fileReader) {
        this.config = config;
        this.serverConnector = serverConnector;
        this.fileReader = fileReader;
    }

    void parse() {
        log.debug("Pairing Data and Info files...");
        Map<String, PairFilesContainer> filesEntries = fileReader.groupFiles(config.getDataPath());

        log.info("filesEntries: {}", filesEntries);

        log.debug("Logging in ...");
        String token = serverConnector.login(generateAccess(config));

        System.out.println("token = " + token);

        log.debug("Getting section names ...");
        List<SpendingSection> personSectionsList = serverConnector.getSectionsList(token);

        for (Map.Entry<String, PairFilesContainer> pair : filesEntries.entrySet()){
            Set<String> sectionsInFile = fileReader.parseDataFile(config.getDataPath(), pair.getValue().getPathDataFile());

            List<SpendingSection> spendingSectionsTemp = new ArrayList<>(personSectionsList);
            for (String sectionInFile : sectionsInFile){
                if (spendingSectionsTemp.stream().noneMatch(spendingSection -> spendingSection.getName().equals(sectionInFile)))
                    personSectionsList = serverConnector.saveSpendingSection(token, generateSpendingSection(sectionInFile));
            }

            List<Transaction> transactionsInFile = fileReader.parseInfoFile(config.getDataPath(), pair.getValue().getPathInfoFile());

            Status savingStatus = serverConnector.saveTransactions(token, transactionsInFile, config.getLogin());
            log.debug("Saving Transactions status is {}", savingStatus);

        }


//        Set<String> sectionNames = getSectionNames(filesEntries);
//
//          saving them
//        List<SpendingSection> spendingSections = serverConnector.saveSections(sectionNames);
//
//        log.debug("Getting list of Transactions from files ...");
//         getting list of Transactions
//        List<Transaction> transactionsToAdd = getTransactionList(filesEntries, spendingSections);
//
//        saving them
//        log.debug("Saving Transactions ...");
//
//        Status savingStatus = serverConnector.saveTransactions(transactionsToAdd, config.getLogin());
//        log.debug("Saving Transactions status is {}", savingStatus);
    }



    private List<Transaction> getTransactionList(Map<String, PairFilesContainer> filesEntries, List<SpendingSection> spendingSections) {
        List<Transaction> transactions = new ArrayList<>();
        for (PairFilesContainer pair : filesEntries.values()) {
            transactions.addAll(fileReader.parseInfoFile(config.getDataPath(), pair.getPathInfoFile()));
        }
        return transactions;
    }

    private Set<String> getSectionNames(Map<String, PairFilesContainer> filesEntries) {
        Set<String> sectionNames = new HashSet<>();

        for (PairFilesContainer pair : filesEntries.values()) {
//            sectionNames.addAll(parseDataFile(pair.getPathDataFile()));
            sectionNames.addAll(fileReader.parseDataFile(config.getDataPath(), pair.getPathDataFile()));
        }
        return sectionNames;
    }


}
