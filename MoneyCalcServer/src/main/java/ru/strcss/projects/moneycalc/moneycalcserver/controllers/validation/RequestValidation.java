package ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.strcss.projects.moneycalc.moneycalcdto.Validationable;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.ValidationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Slf4j
@Getter
@AllArgsConstructor
public class RequestValidation {
    private boolean isValid;
    private String reason;

    @RequiredArgsConstructor
    public static class Validator {

        private List<Pair> additionalChecks = new ArrayList<>();
        @NonNull
        private Validationable container;
        @NonNull
        private String actionName;

        public Validator addValidation(Supplier<Boolean> supplier, Supplier<String> actionName) {
            additionalChecks.add(new Pair(supplier, actionName, null));
            return this;
        }

        public Validator addValidation(Supplier<Boolean> supplier, Supplier<String> actionName, String objectName) {
            additionalChecks.add(new Pair(supplier, actionName, objectName));
            return this;
        }

        public RequestValidation validate() {
            if (container == null) {
                log.error("Container is null");
                return new RequestValidation(false, "Container is null");
            } else {
                ValidationResult validationResult = container.isValid();
                if (!validationResult.isValidated()) {
                    log.error("{} has failed - required fields are incorrect: {}", actionName, validationResult.getReasons());
                    String message = "Required fields are incorrect: " + validationResult.getReasons();
                    return new RequestValidation(false, message);
                }
            }

            for (Pair pair : additionalChecks) {
                if (!pair.getAction().get()) {
                    log.error(pair.getActionName().get());

                    String errorMsg;
                    if (pair.getObjectName() != null) {
                        errorMsg = String.format("%s in object: %s", pair.getActionName().get(), pair.getObjectName());
                    } else {
                        errorMsg = pair.getActionName().get();
                    }
                    return new RequestValidation(false, errorMsg);
                }
            }
            return new RequestValidation(true, null);
        }
    }

    @Getter
    @AllArgsConstructor
    private static class Pair {
        private Supplier<Boolean> action;
        private Supplier<String> actionName;
        private String objectName;
    }
}
