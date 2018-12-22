package ru.strcss.projects.moneycalc.testutils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.SpendingSection;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Transaction;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.testng.Assert.assertTrue;

public class TestUtils {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    /**
     * Asserting that List is ordered ASC by Id
     */
    public static void assertReturnedSectionsOrder(List<SpendingSection> spendingSections) {
        assertTrue(isIdOrderASC(spendingSections), "Spending Section list is not ASC ordered by Id!");
    }

    /**
     * Checking if SpendingSections are ordered ASC by Id
     */
    private static boolean isIdOrderASC(List<SpendingSection> spendingSections) {
        if (spendingSections.size() < 2)
            return true;
        for (int i = 1; i < spendingSections.size(); i++) {
            if (spendingSections.get(i - 1).getId() > spendingSections.get(i).getId())
                return false;
        }
        return true;
    }

    /**
     * Checking if SpendingSections are ordered ASC by Date
     */
    public static boolean assertTransactionsOrderedByDate(List<Transaction> transactionList) {
        if (transactionList.size() < 2)
            return true;
        for (int i = 1; i < transactionList.size(); i++) {
            LocalDate dateTransaction1 = transactionList.get(i - 1).getDate();
            LocalDate dateTransaction2 = transactionList.get(i).getDate();
            if (dateTransaction1.isAfter(dateTransaction2))
                return false;
        }
        return true;
    }

    /**
     * Return max Spending Section Id
     */
    public static int getMaxSpendingSectionId(List<SpendingSection> spendingSections) {
        return spendingSections.stream().map(SpendingSection::getSectionId).mapToInt(Integer::intValue).max().orElse(-1);
    }

    /**
     * Serialize object to json
     */
    public static String serializeToJson(Object obj) {
        String json;
        try {
            json = OBJECT_MAPPER.writeValueAsString(obj);
        } catch (IOException e) {
            throw new RuntimeException("Serialization has failed", e);
        }
        return json;
    }
}
