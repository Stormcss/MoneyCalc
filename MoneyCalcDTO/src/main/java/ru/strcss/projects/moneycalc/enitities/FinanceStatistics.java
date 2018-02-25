package ru.strcss.projects.moneycalc.enitities;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@Builder
@Deprecated
public class FinanceStatistics {
//    @Id
    private String _id;
//    /**
//     * Transaction - every single Transaction
//     * PersonTransactions       = List<Transaction> - Transactions grouped by Person

    @DBRef
    private PersonTransactions personTransactions;

}
