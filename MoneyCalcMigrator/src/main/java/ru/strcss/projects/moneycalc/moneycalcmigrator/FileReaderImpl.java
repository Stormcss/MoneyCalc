package ru.strcss.projects.moneycalc.moneycalcmigrator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Transaction;
import ru.strcss.projects.moneycalc.moneycalcmigrator.api.FileReader;
import ru.strcss.projects.moneycalc.moneycalcmigrator.model.dto.PairFilesContainer;
import ru.strcss.projects.moneycalc.moneycalcmigrator.model.dto.TransactionParseContainer;
import ru.strcss.projects.moneycalc.moneycalcmigrator.model.exceptions.MigratorException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class FileReaderImpl implements FileReader {

    private Comparator<String> namedDatesComparator() {
        return (stringDate1, stringDate2) -> {
            LocalDate date1 = LocalDate.parse(stringDate1, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            LocalDate date2 = LocalDate.parse(stringDate2, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            int isAfter = date1.isAfter(date2) ? 1 : 0;
            return date1.isBefore(date2) ? -1 : isAfter;
        };
    }

    public Map<String, PairFilesContainer> groupFiles(String filesPath) {
        Pattern periodPattern = Pattern.compile("MoneyCalc(Data|Info)_(.*?).txt");
        Map<String, PairFilesContainer> filesEntries = new TreeMap<>(namedDatesComparator());

        try (Stream<Path> paths = Files.walk(Paths.get(filesPath), 1)) {
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
        } catch (IOException e) {
            throw new MigratorException("Can not group Data and Info files!", e);
        }
        return filesEntries;
    }

    /**
     * Return Set of SpendingSection names from specific file
     *
     * @param fileName - file name
     * @return Set of SpendingSection names
     */
    public List<String> parseDataFile(String folderPath, String fileName) {
        try (Stream<String> stream = Files.lines(Paths.get(folderPath + "/" + fileName))) {
            return stream
                    .skip(14)
                    .limit(2)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new MigratorException("Can not parse Data File!", e);
        }
    }

    /**
     * Return List of Transactions from specific file
     *
     * @param fileName - file name
     * @return List of Transactions
     */
    @Override
    public List<Transaction> parseInfoFile(String folderPath, String fileName, Map<Integer, Integer> idMapper) {
        try (Stream<String> stream = Files.lines(Paths.get(folderPath + "/" + fileName))) {
            return stream.map(line -> buildTransaction(line, idMapper))
                    .filter(Objects::nonNull)
                    .peek(t -> log.trace("transaction: {}", t))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new MigratorException("Can not parse Info File!", e);
        }
    }

    private PairFilesContainer generateContainer(String type, PairFilesContainer container, String path) {
        if (container == null) {
            if ("Data".equals(type))
                container = new PairFilesContainer(path, null);
            else
                container = new PairFilesContainer(null, path);
        } else {
            if ("Data".equals(type))
                container = new PairFilesContainer(path, container.getPathInfoFile());
            else
                container = new PairFilesContainer(container.getPathDataFile(), path);
        }
        return container;
    }

    private Transaction buildTransaction(String line, Map<Integer, Integer> idMapper) {
        TransactionParseContainer parseContainer = parseTransactionLine(line, idMapper);
        if (parseContainer == null)
            return null;
        return Transaction.builder()
                .sectionId(parseContainer.getId())
                .date(parseContainer.getDate())
                .sum(parseContainer.getSum())
                .title(parseContainer.getDescription())
                .currency("RUR")
                .build();
    }

    private TransactionParseContainer parseTransactionLine(String line, Map<Integer, Integer> idMapper) {
        int sectionId;
        String[] part = line.split(" ", 4);
        int idInFile = Integer.parseInt(part[0]);

        if (idInFile > 2)
            sectionId = idMapper.get(idInFile);
        else if (idInFile == 1 || idInFile == 2)
            sectionId = idInFile;
        else
            return null;
        return TransactionParseContainer.builder()
                .id(sectionId)
                .date(formatDate(part[1]))
                .sum(Integer.parseInt(part[2]))
                .description(part.length == 4 ? part[3].replaceFirst(".$", "") : null)
                .build();
    }

    private LocalDate formatDate(String oldDate) {
        String[] part = oldDate.split("\\.");
        return LocalDate.of(Integer.valueOf(part[2]), Integer.valueOf(part[1]), Integer.valueOf(part[0]));
    }
}
