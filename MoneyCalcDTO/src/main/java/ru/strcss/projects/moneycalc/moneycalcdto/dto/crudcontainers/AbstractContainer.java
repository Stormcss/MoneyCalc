package ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers;

import ru.strcss.projects.moneycalc.moneycalcdto.Validationable;

public abstract class AbstractContainer implements Validationable {

//    public List<String> validateStringFields(FieldPairs... fields) {
//        List<String> reasons = new ArrayList<>();
//        for (FieldPairs field : fields) {
//            switch (field.getField()) {
//                case "rangeFrom":
//                case "rangeTo":
//                    if (field.getValue() == null || field.getValue().isEmpty())
//                        reasons.add(field.getField() + " is empty");
//                    else if (!isDateCorrect(field.getValue())) reasons.add(field.getField() + " has incorrect date");
//                    break;
//                case "id":
//                    if (field.getValue() == null || field.getValue().isEmpty()) reasons.add("id is empty");
//                    break;
//            }
//        }
//        return reasons;
//    }

//    private boolean isDateCorrect(String date) {
//        try {
//            LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
//        } catch (DateTimeException e) {
//            return false;
//        }
//        return true;
//    }

//    @Getter
//    @ToString
//    protected class FieldPairs {
//        private String field;
//        private String value;
//
//        public FieldPairs(String field, String value) {
//            this.field = field;
//            this.value = value;
//        }
//    }
}
