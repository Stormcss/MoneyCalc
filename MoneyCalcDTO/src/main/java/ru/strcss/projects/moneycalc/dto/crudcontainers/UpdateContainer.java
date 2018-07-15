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
//    private Class<E> eClass = (Class<E>) new Object();
//
//    @Override
//    public ValidationResult isValid() {
//        System.out.println("eClass = " + eClass);
//        List<String> reasons = validateStringFields(new FieldPairs("login", login));
////        if (payload == null) reasons.add(getReason((E) new Object()) );
//        return new ValidationResult(reasons.isEmpty(), reasons);
//    }
//}