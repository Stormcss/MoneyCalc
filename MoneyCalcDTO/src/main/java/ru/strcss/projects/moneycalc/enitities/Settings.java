package ru.strcss.projects.moneycalc.enitities;

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
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "\"Settings\"")
public class Settings implements Validationable, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "\"periodFrom\"")
    private LocalDate periodFrom;

    @Column(name = "\"periodTo\"")
    private LocalDate periodTo;

    public ValidationResult isValid() {
        List<String> reasons = new ArrayList<>();
        if (periodFrom == null) reasons.add("periodFrom is empty");
//        if (periodFrom == null || periodFrom.isEmpty()) reasons.add("periodFrom is empty");
        if (periodTo == null) reasons.add("periodTo is empty");
//        if (periodTo == null || periodTo.isEmpty()) reasons.add("periodTo is empty");
        return new ValidationResult(reasons.isEmpty(), reasons);
    }
}
