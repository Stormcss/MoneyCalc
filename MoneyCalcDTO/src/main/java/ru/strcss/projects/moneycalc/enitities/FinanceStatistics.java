package ru.strcss.projects.moneycalc.enitities;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Data
@Deprecated
public class FinanceStatistics {
    private String _id;
    @DBRef
    private PersonTransactions personTransactions;

}
