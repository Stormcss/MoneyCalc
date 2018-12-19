package ru.strcss.projects.moneycalc.moneycalcmigrator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Transaction;
import ru.strcss.projects.moneycalc.moneycalcmigrator.api.FileReader;
import ru.strcss.projects.moneycalc.moneycalcmigrator.dto.PairFilesContainer;
import ru.strcss.projects.moneycalc.moneycalcmigrator.dto.TransactionParseContainer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class FileReaderImpl implements FileReader {

    public Map<String, PairFilesContainer> groupFiles(String filesPath) {
        Pattern periodPattern = Pattern.compile("MoneyCalc(Data|Info)_(.*?).txt");
        Map<String, PairFilesContainer> filesEntries = new HashMap<>(32);

        try (Stream<Path> paths = Files.walk(Paths.get(filesPath))) {
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
            throw new RuntimeException("Can not group Data and Info files!", e);
        }
        return filesEntries;
    }

    /**
     * Return Set of SpendingSection names from specific file
     *
     * @param fileName - file name
     * @return Set of SpendingSection names
     */
    public Set<String> parseDataFile(String folderPath, String fileName) {
        try (Stream<String> stream = Files.lines(Paths.get(folderPath + "/" + fileName))) {
            return stream
                    .skip(14)
                    .limit(2)
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            throw new RuntimeException("Can not parse Data File!", e);
        }
    }

    /**
     * Return List of Transactions from specific file
     *
     * @param fileName - file name
     * @return List of Transactions
     */
    public List<Transaction> parseInfoFile(String folderPath, String fileName) {
        try (Stream<String> stream = Files.lines(Paths.get(folderPath + "/" + fileName))) {
            // TODO: 19.02.2018 add other sections as well
            return stream.map(this::buildTransaction)
                    .peek(t -> log.trace("transaction: {}", t))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Can not parse Info File!", e);
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

    private Transaction buildTransaction(String line) {
        TransactionParseContainer parseContainer = parseTransactionLine(line);
        return Transaction.builder()
                .sectionId(parseContainer.getId()) // FIXME: 19.02.2018 Only 0 and 1 are created
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
                .date(formatDate(part[1]))
                .sum(Integer.parseInt(part[2]))
                .description(part[3].replaceFirst(".$", ""))
                .build();
    }

    private LocalDate formatDate(String oldDate) {
        String[] part = oldDate.split("\\.");
        return LocalDate.of(Integer.valueOf(part[2]), Integer.valueOf(part[1]), Integer.valueOf(part[0]));
    }
}
