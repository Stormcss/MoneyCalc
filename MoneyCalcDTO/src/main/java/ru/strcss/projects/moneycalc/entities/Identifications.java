package ru.strcss.projects.moneycalc.entities;

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
@Table(name = "\"Identifications\"")
public class Identifications implements Validationable, Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    public ValidationResult isValid() {
        List reasons = new ArrayList<>();
        if (name == null || name.isEmpty()) reasons.add("name is empty");
        return new ValidationResult(reasons.isEmpty(), reasons, "Identifications");
    }
}
