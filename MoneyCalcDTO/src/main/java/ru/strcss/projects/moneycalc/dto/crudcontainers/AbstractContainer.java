package ru.strcss.projects.moneycalc.dto.crudcontainers;

import lombok.Getter;
import lombok.ToString;
import ru.strcss.projects.moneycalc.Validationable;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractContainer implements Validationable/*, Visitor*/ {

    public List<String> validateStringFields(FieldPairs... fields) {
        List<String> reasons = new ArrayList<>();
        for (FieldPairs field : fields) {
            switch (field.getField()) {
                case "rangeFrom":
                case "rangeTo":
                    if (field.getValue() == null || field.getValue().isEmpty())
                        reasons.add(field.getField() + " is empty");
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
    @ToString
    protected class FieldPairs {
        private String field;
        private String value;

        public FieldPairs(String field, String value) {
            this.field = field;
            this.value = value;
        }
    }
}
