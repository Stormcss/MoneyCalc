package ru.strcss.projects.moneycalc.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.strcss.projects.moneycalc.Validationable;
import ru.strcss.projects.moneycalc.dto.ValidationResult;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Transactions")
@Table(name = "\"Transactions\"")
public class Transaction implements Validationable, Serializable {

    /**
     * id of Transaction - transaction id in DB Table
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Person Id - used for linking Person with current Transaction in DB
     */
    @Column(name = "\"personId\"")
    private Integer personId;

    @Column(name = "\"sectionId\"")
    private Integer sectionId;

    private LocalDate date;

    private Integer sum;
    private String currency;
    private String title;
    private String description;

    @Override
    public ValidationResult isValid() {
        List<String> reasons = new ArrayList<>();
        if (sum == null || sum == 0) reasons.add("Transaction sum can not be empty or 0!");
        if (sectionId == null || sectionId < 0) reasons.add("SectionID can not be empty or < 0!");
        return new ValidationResult(reasons.isEmpty(), reasons);
    }
}
