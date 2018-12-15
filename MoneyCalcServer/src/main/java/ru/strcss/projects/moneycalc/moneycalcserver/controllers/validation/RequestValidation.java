package ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import ru.strcss.projects.moneycalc.moneycalcdto.Validationable;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.MoneyCalcRs;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.ValidationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerUtils.responseError;

@Getter
@Slf4j
public class RequestValidation<E> {
    private boolean isValid;
    private ResponseEntity<MoneyCalcRs<E>> validationError;

    private RequestValidation(boolean isValid, ResponseEntity<MoneyCalcRs<E>> validationError) {
        this.isValid = isValid;
        this.validationError = validationError;
    }

    public static class Validator {

        private List<Pair> additionalChecks = new ArrayList<>();
        private Validationable container;
        private String actionName;

        public Validator(Validationable container, String actionName) {
            this.container = container;
            this.actionName = actionName;
        }

        public Validator addValidation(Supplier<Boolean> supplier, Supplier<String> actionName) {
            additionalChecks.add(new Pair(supplier, actionName, null));
            return this;
        }

        public Validator addValidation(Supplier<Boolean> supplier, Supplier<String> actionName, String objectName) {
            additionalChecks.add(new Pair(supplier, actionName, objectName));
            return this;
        }

        public <E> RequestValidation<E> validate() {
            if (container == null) {
                log.error("Container is null");
                return new RequestValidation<>(false, responseError("Container is null"));
            } else {
                ValidationResult validationResult = container.isValid();
                if (!validationResult.isValidated()) {
                    log.error("{} has failed - required fields are incorrect: {}", actionName, validationResult.getReasons());
                    return new RequestValidation<>(false, responseError("Required fields are incorrect: " + validationResult.getReasons()));
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
                    return new RequestValidation<>(false, responseError(errorMsg));
                }
            }
            return new RequestValidation<>(true, null);
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
