package ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.strcss.projects.moneycalc.Validationable;
import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.ValidationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerUtils.responseError;

@Getter
@Slf4j
public class RequestValidation<E> {
    private boolean isValid;
    private AjaxRs<E> validationError;

    public RequestValidation(boolean isValid, AjaxRs<E> validationError) {
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
            additionalChecks.add(new Pair(supplier, actionName));
            return this;
        }

        public <E> RequestValidation<E> validate() {
            ValidationResult validationResult = container.isValid();

            if (!validationResult.isValidated()) {
                log.error("{} has failed - required fields are incorrect: {}", actionName, validationResult.getReasons());
                return new RequestValidation<>(false, responseError("Required fields are incorrect: " + validationResult.getReasons()));
            }

            for (Pair pair : additionalChecks){
                if (!pair.getAction().get()) {
                    log.error(pair.getActionName().get());
                    return new RequestValidation<>(false, responseError(pair.getActionName().get()));
                }
            }
            return new RequestValidation<>(true, null);
        }
    }

    @Getter
    public static class Pair{
        private Supplier<Boolean> action;
        private Supplier<String> actionName;

        Pair(Supplier<Boolean> action, Supplier<String> actionName) {
            this.action = action;
            this.actionName = actionName;
        }
    }
}
