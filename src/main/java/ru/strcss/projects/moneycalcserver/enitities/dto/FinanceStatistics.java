package ru.strcss.projects.moneycalcserver.enitities.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "FinanceStatistics")
@Builder
public class FinanceStatistics {
    @Id
    private String _id;
    private List<Transaction> transactions;

}
