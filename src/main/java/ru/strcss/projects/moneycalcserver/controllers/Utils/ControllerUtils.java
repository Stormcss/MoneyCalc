package ru.strcss.projects.moneycalcserver.controllers.Utils;

import lombok.extern.slf4j.Slf4j;
import ru.strcss.projects.moneycalcserver.enitities.dto.AjaxRs;
import ru.strcss.projects.moneycalcserver.enitities.dto.Person;
import ru.strcss.projects.moneycalcserver.enitities.dto.Status;
import ru.strcss.projects.moneycalcserver.mongo.PersonRepository;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class ControllerUtils {

    public static boolean validateRegisterPerson(Person person) throws NoSuchFieldException, IllegalAccessException {
        List fields = Arrays.asList(Person.class.getDeclaredField("access"),
                Person.class.getDeclaredField("personalIdentifications"));
        return validateFields(person, fields);
    }

    private static boolean validateFields(Object obj, List<Field> list) throws IllegalAccessException {
        for (Field field : list) {
            field.setAccessible(true);
            if (field.get(obj) == null || field.get(obj) == "") {
                return false;
            }
        }
        return true;
    }

    public static boolean isRegisteredPerson(String id, PersonRepository repository) {
        // FIXME: 13.01.2018 Returning the whole Person is not the best practice
        return repository.findPersonByID(id) != null;
    }

//    public static Person searchExistingRegisterPerson(String id, PersonRepository repository){
//        // FIXME: 13.01.2018 Returning the whole Person is not the best practice
//        return repository.findPersonByID(id) != null;
//    }

    public static AjaxRs responseError(String message) {
        return AjaxRs.builder()
                .message(message)
                .status(Status.ERROR)
                .build();
    }

    public static <E> AjaxRs<E> responseSuccess(String message, E payload) {
        return (AjaxRs<E>) AjaxRs.builder()
                .message(message)
                .status(Status.SUCCESS)
                .payload(payload)
                .build();
    }
}
