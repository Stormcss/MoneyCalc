//package ru.strcss.projects.moneycalc.dto.crudcontainers;
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import ru.strcss.projects.moneycalc.dto.ValidationResult;
//
//import java.util.List;
//
//@Data
//@AllArgsConstructor
//public class UpdateContainer<E> extends AbstractContainer{
//
//    private String login;
//    private E payload;
//
//    @Override
//    public ValidationResult isValid() {
//        List<String> reasons = validateStringFields(new FieldPairs("login", login));
//        if (payload == null) reasons.add(getReason(new );
//        return new ValidationResult(reasons.isEmpty(), reasons);
//    }
//}