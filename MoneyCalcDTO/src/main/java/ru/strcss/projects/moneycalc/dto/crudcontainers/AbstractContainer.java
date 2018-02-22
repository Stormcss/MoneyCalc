package ru.strcss.projects.moneycalc.dto.crudcontainers;

import lombok.Data;
import lombok.Getter;
import ru.strcss.projects.moneycalc.dto.ValidationResult;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Data
public abstract class AbstractContainer {
    public String login;

    public abstract ValidationResult isValid();

    public List<String> validateStringFields(FieldPairs... fields) {
        List<String> reasons = new ArrayList<>();
        for (FieldPairs field : fields) {
            switch (field.getField()) {
                case "login":
                    if (field.getValue() == null || field.getValue().isEmpty()) reasons.add("Login is empty");
                    break;
                case "rangeFrom":
                case "rangeTo":
                    if (field.getValue() == null || field.getValue().isEmpty()) reasons.add(field.getField() + " is empty");
                    else if (!isDateCorrect(field.getValue())) reasons.add(field.getField() + " has incorrect date");
                    break;
                case "id":
                    if (field.getValue() == null || field.getValue().isEmpty()) reasons.add("id is empty");
                    break;
            }
        }
        return reasons;
    }

    private boolean isDateCorrect(String date) {
        try {
            LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeException e) {
            return false;
        }
        return true;
    }

    @Getter
    protected class FieldPairs{
        private String field;
        private String value;

        public FieldPairs(String field, String value) {
            this.field = field;
            this.value = value;
        }
    }
}
