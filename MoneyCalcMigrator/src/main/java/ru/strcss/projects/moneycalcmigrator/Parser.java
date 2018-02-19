package ru.strcss.projects.moneycalcmigrator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.strcss.projects.moneycalc.dto.Status;
import ru.strcss.projects.moneycalc.enitities.SpendingSection;
import ru.strcss.projects.moneycalc.enitities.Transaction;
import ru.strcss.projects.moneycalcmigrator.dto.PairFilesContainer;
import ru.strcss.projects.moneycalcmigrator.dto.TransactionParseContainer;
import ru.strcss.projects.moneycalcmigrator.utils.ConfigContainer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
class Parser {
//    private List<Map<SpendingSection, List<Transaction>>> parsedTransactions = new ArrayList<>();

    //map <Data - Info>
    private Map<String, PairFilesContainer> filesEntries = new HashMap<>(32);

    private final ConfigContainer config;

    private final Saver saver;

    @Autowired
    public Parser(ConfigContainer config, Saver saver) {
        this.config = config;
        this.saver = saver;
    }


    void parse() throws IOException {
//        saver = new Saver(config);

        log.debug("Walking through folder and pairing files...");
        // looking for the files and pairing Data and Info files
        groupFiles();

        log.info("filesEntries: {}", filesEntries);

        log.debug("Parsing Data files and saving Sections ...");
        //  getting section names
        Set<String> sectionNames = getSectionNames();

        //  saving them
        List<SpendingSection> spendingSections = saver.saveSections(sectionNames);

        log.debug("Getting list of Transactions from files ...");
        // getting list of Transactions
        List<Transaction> transactionsToAdd = getTransactionList(spendingSections);

        //saving them
        log.debug("Saving Transactions ...");
        Status savingStatus = saver.saveTransactions(transactionsToAdd, config.getLogin());
        log.debug("Saving Transactions status is {}", savingStatus);


    }

    private List<Transaction> getTransactionList(List<SpendingSection> spendingSections) throws IOException {
        List<Transaction> transactions = new ArrayList<>();
        for (PairFilesContainer pair : filesEntries.values()) {
            transactions.addAll(parseInfoFile(pair.getPathInfoFile()));
        }
        return transactions;
    }

    private Set<String> getSectionNames() throws IOException {
        Set<String> sectionNames = new HashSet<>();

        for (PairFilesContainer pair : filesEntries.values()) {
            sectionNames.addAll(parseDataFile(pair.getPathDataFile()));
        }
        return sectionNames;
    }

    /**
     * Return Set of SpendingSection names from specific file
     *
     * @param fileName - file name
     * @return Set of SpendingSection names
     */
    private Set<String> parseDataFile(String fileName) throws IOException {
        try (Stream<String> stream = Files.lines(Paths.get(config.getDataPath() + "\\" + fileName))) {
            return stream
                    .skip(14)
                    .limit(2)
                    .collect(Collectors.toSet());
        }
    }

    /**
     * Return List of Transactions from specific file
     *
     * @param fileName - file name
     * @return List of Transactions
     */
    private List<Transaction> parseInfoFile(String fileName) throws IOException {
        try (Stream<String> stream = Files.lines(Paths.get(config.getDataPath() + "\\" + fileName))) {
            // TODO: 19.02.2018 add other sections as well
            return stream.map(this::buildTransaction)
                    .peek(t -> log.debug("transaction: {}", t))
                    .collect(Collectors.toList());
        }
    }

    private Transaction buildTransaction(String line) {
        TransactionParseContainer parseContainer = parseTransactionLine(line);
        return Transaction.builder()
                .sectionID(parseContainer.getId() - 1) // FIXME: 19.02.2018 Only 0 and 1 are created
                .date(parseContainer.getDate())
                .sum(parseContainer.getSum())
                .description(parseContainer.getDescription())
                .currency("RUR")
                .build();
    }

    private TransactionParseContainer parseTransactionLine(String line) {
        String[] part = line.split(" ", 4);
        return TransactionParseContainer.builder()
                .id(Integer.parseInt(part[0]))
                // FIXME: 19.02.2018 Date must be wrapped!
                .date(part[1])
                .sum(Integer.parseInt(part[2]))
                .description(part[3].replaceFirst(".$", ""))
                .build();
    }

    private void groupFiles() throws IOException {
        Pattern periodPattern = Pattern.compile("MoneyCalc(Data|Info)_(.*?).txt");

        try (Stream<Path> paths = Files.walk(Paths.get(config.getDataPath()))) {
            paths.filter(Files::isRegularFile)
                    .map(Path::getFileName)
                    .forEach(path -> {
                        Matcher matcher = periodPattern.matcher(path.toString());
                        while (matcher.find()) {
                            String period = matcher.group(2);
                            PairFilesContainer tempContainer = filesEntries.get(period);
                            filesEntries.put(matcher.group(2), generateContainer(matcher.group(1), tempContainer, path.toString()));
                        }
                    });
        }
    }


    private PairFilesContainer generateContainer(String type, PairFilesContainer container, String path) {
        if (container == null) {
            if (type.equals("Data"))
                container = new PairFilesContainer(path, null);
            else
                container = new PairFilesContainer(null, path);
        } else {
            if (type.equals("Data"))
                container = new PairFilesContainer(path, container.getPathInfoFile());
            else
                container = new PairFilesContainer(container.getPathDataFile(), path);
        }
        return container;
    }

}
