package ru.strcss.projects.moneycalcmigrator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.strcss.projects.moneycalc.enitities.SpendingSection;
import ru.strcss.projects.moneycalc.enitities.Transaction;
import ru.strcss.projects.moneycalcmigrator.dto.PairFilesContainer;
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
class Parser {


    private List<Map<SpendingSection, List<Transaction>>> parsedTransactions = new ArrayList<>();

    //map <Data - Info>
    private Map<String, PairFilesContainer> filesEntries = new HashMap<>(32);

    private ConfigContainer config;
    private Saver saver;

    @Autowired
    public Parser(ConfigContainer config) {
        this.config = config;
    }


    void parse() throws IOException {
        // looking for the files and pairing Data and Info files
        groupFiles();

        System.out.println("filesEntries = " + filesEntries);

        //  getting section names
        Set<String> sectionNames = getSectionNames();
        //  saving them
        Saver saver = new Saver(config);
        saver.saveSections(sectionNames);

        System.out.println("sectionNames = " + sectionNames);
    }

    private Set<String> getSectionNames() throws IOException {
        Set<String> sectionNames = new HashSet<>();

        for (PairFilesContainer pair : filesEntries.values()) {
            sectionNames.addAll(parseDataFile(pair.getPathDataFile()));
        }
        return sectionNames;
    }

    private Set<String> parseDataFile(String fileName) throws IOException {
        try (Stream<String> stream = Files.lines(Paths.get(config.getDataPath() + "\\" + fileName))) {
            return stream
                    .skip(14)
                    .limit(2)
                    .collect(Collectors.toSet());
        }
    }

//    private List<Transaction> parseInfoFile(String fileName) throws IOException {
//        try (Stream<String> stream = Files.lines(Paths.get(config.getDataPath() + "\\" + fileName))) {
//            return stream.collect(Collectors.toSet());
//        }
//    }


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
