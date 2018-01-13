package ru.strcss.projects.moneycalcserver.controllers.Utils;

import ru.strcss.projects.moneycalcserver.enitities.dto.AjaxRs;
import ru.strcss.projects.moneycalcserver.enitities.dto.Person;
import ru.strcss.projects.moneycalcserver.enitities.dto.Status;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class ApiControllerUtils {

    public static boolean validateRegisterPerson(Person person) throws NoSuchFieldException, IllegalAccessException {
        List fields = Arrays.asList(Person.class.getField("Access"),
                Person.class.getField("PersonalIdentifications"));
        return validateFields(person, fields);
    }

    private static boolean validateFields(Object obj, List<Field> list) throws IllegalAccessException {
        for (Field field : list){
            if (field.get(obj) == null || field.get(obj) == "") {
                return false;
            }
        }
        return true;
    }

    public static AjaxRs responseError(String message){
        return AjaxRs.builder()
                .message(message)
                .status(Status.ERROR)
                .build();
    }
    public static AjaxRs responseSuccess(String message, Object payload){
        return AjaxRs.builder()
                .message(message)
                .status(Status.SUCCESS)
                .payload(payload)
                .build();
    }
}
