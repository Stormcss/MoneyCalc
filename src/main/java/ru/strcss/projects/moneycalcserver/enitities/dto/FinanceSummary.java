package ru.strcss.projects.moneycalcserver.enitities.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "FinanceSummary")
@Builder
public class FinanceSummary {
    @Id
    private String _id;

    private List<FinanceSummaryBySection> financeSections;
    private int daysSpend;
    private int daysInMonth;
}
