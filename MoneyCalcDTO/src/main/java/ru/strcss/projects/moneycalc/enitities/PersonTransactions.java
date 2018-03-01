package ru.strcss.projects.moneycalc.enitities;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * Transactions grouped by Person
 */
@Data
@Builder
@Document(collection = "Transactions")
public class PersonTransactions {
    @Id
    private String _id;
    private String login;
    private List<Transaction> transactions;
}
