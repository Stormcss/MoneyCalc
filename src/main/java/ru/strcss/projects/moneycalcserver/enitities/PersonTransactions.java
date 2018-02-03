package ru.strcss.projects.moneycalcserver.enitities;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * Transactions grouped by Person
 */
@Getter
@Builder
@ToString
@Document(collection = "Transactions")
public class PersonTransactions {
    @Id
    private String _id;
    private String login;
    private List<Transaction> transactions;
}
