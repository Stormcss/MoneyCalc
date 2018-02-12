package ru.strcss.projects.moneycalc.dto.crudcontainers;

import lombok.Data;
import ru.strcss.projects.moneycalc.dto.ValidationResult;

@Data
public abstract class AbstractContainer {
    public String login;
    public abstract ValidationResult isValid();
}
