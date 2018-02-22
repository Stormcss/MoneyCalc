package ru.strcss.projects.moneycalcmigrator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.strcss.projects.moneycalc.dto.Status;
import ru.strcss.projects.moneycalc.enitities.SpendingSection;
import ru.strcss.projects.moneycalc.enitities.Transaction;
import ru.strcss.projects.moneycalcmigrator.dto.ConfigContainer;
import ru.strcss.projects.moneycalcmigrator.dto.PairFilesContainer;

import java.io.IOException;
import java.util.*;

@Slf4j
@Component
class Parser {
//    private List<Map<SpendingSection, List<Transaction>>> parsedTransactions = new ArrayList<>();


    private final ConfigContainer config;
    private final Saver saver;
    private final FileReader fileReader;

    @Autowired
    public Parser(ConfigContainer config, Saver saver, FileReader fileReader) {
        this.config = config;
        this.saver = saver;
        this.fileReader = fileReader;
    }


    void parse() throws IOException {
//        Map<String, PairFilesContainer> filesEntries = new HashMap<>(32);

        log.debug("Walking through folder and pairing files Data and Info files...");
        Map<String, PairFilesContainer> filesEntries = fileReader.groupFiles(config.getDataPath());

        log.info("filesEntries: {}", filesEntries);

        log.debug("Parsing Data files and saving Sections ...");
        Set<String> sectionNames = getSectionNames(filesEntries);

        //  saving them
        List<SpendingSection> spendingSections = saver.saveSections(sectionNames);

        log.debug("Getting list of Transactions from files ...");
        // getting list of Transactions
        List<Transaction> transactionsToAdd = getTransactionList(filesEntries, spendingSections);

        //saving them
        log.debug("Saving Transactions ...");

        Status savingStatus = saver.saveTransactions(transactionsToAdd, config.getLogin());
        log.debug("Saving Transactions status is {}", savingStatus);
    }

    private List<Transaction> getTransactionList(Map<String, PairFilesContainer> filesEntries, List<SpendingSection> spendingSections) throws IOException {
        List<Transaction> transactions = new ArrayList<>();
        for (PairFilesContainer pair : filesEntries.values()) {
            transactions.addAll(fileReader.parseInfoFile(config.getDataPath(), pair.getPathInfoFile()));
        }
        return transactions;
    }

    private Set<String> getSectionNames(Map<String, PairFilesContainer> filesEntries) throws IOException {
        Set<String> sectionNames = new HashSet<>();

        for (PairFilesContainer pair : filesEntries.values()) {
//            sectionNames.addAll(parseDataFile(pair.getPathDataFile()));
            sectionNames.addAll(fileReader.parseDataFile(config.getDataPath(), pair.getPathDataFile()));
        }
        return sectionNames;
    }






}
