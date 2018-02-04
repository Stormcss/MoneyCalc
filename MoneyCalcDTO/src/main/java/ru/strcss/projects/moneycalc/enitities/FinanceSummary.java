package ru.strcss.projects.moneycalc.enitities;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document
@Builder
public class FinanceSummary {
    @Id
    private String _id;

    private List<FinanceSummaryBySection> financeSections;
    private int daysSpend;
    private int daysInMonth;
}
