package ru.strcss.projects.moneycalcmigrator;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import ru.strcss.projects.moneycalc.enitities.Transaction;
import ru.strcss.projects.moneycalcmigrator.dto.PairFilesContainer;

import java.util.*;

@Primary
@Configuration
@Qualifier("FileReaderMock")
public class FileReaderConfiguration extends FileReader{

    Map<String, PairFilesContainer> groupFiles(String filesPath){
        Map<String, PairFilesContainer> filesEntries = new HashMap<>(32);
        filesEntries.put("07.02.2018", new PairFilesContainer("MoneyCalcData_07.02.2018.txt", "MoneyCalcInfo_07.02.2018.txt"));
        return filesEntries;
    }

    public Set<String> parseDataFile(String folderPath, String fileName){
        return new HashSet<>(Arrays.asList("Something", "Shit"));
    }

    public List<Transaction> parseInfoFile(String folderPath, String fileName){
        return Arrays.asList(Transaction.builder().build());
    }
}
