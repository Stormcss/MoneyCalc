package ru.strcss.projects.moneycalc.enitities;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@Getter
@Document
@ToString
public class Finance {

    @Id
    private String _id;

    private FinanceSummary financeSummary;
//    private FinanceStatistics financeStatistics;
}
