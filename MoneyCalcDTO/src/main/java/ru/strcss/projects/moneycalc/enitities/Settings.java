package ru.strcss.projects.moneycalc.enitities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.strcss.projects.moneycalc.Validationable;
import ru.strcss.projects.moneycalc.dto.ValidationResult;

import javax.persistence.*;
import java.io.Serializable;
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
    private String periodFrom;

    @Column(name = "\"periodTo\"")
    private String periodTo;

    public ValidationResult isValid() {
        List<String> reasons = new ArrayList<>();
        if (periodFrom == null || periodFrom.isEmpty()) reasons.add("periodFrom is empty");
        if (periodTo == null || periodTo.isEmpty()) reasons.add("periodTo is empty");
        return new ValidationResult(reasons.isEmpty(), reasons);
    }
}
