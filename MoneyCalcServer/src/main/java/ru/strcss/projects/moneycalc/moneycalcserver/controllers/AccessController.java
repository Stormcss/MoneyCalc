package ru.strcss.projects.moneycalc.moneycalcserver.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.MoneyCalcRs;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Access;
import ru.strcss.projects.moneycalc.moneycalcserver.mapper.AccessMapper;

import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.ACCESS_RETURNED;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.NO_PERSON_EXIST;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerUtils.responseError;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerUtils.responseSuccess;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/access")
public class AccessController implements AbstractController {
    // TODO: 06.03.2018 finish me
    private AccessMapper accessMapper;

    /**
     * Get Access object
     *
     * @return response object with Identifications payload
     */
    @GetMapping
    public ResponseEntity<MoneyCalcRs<Access>> getAccess() {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        Access access = accessMapper.getAccess(login);

        if (access == null) {
            log.error("Can not return Access for login \'{}\' - no Person found", login);
            return responseError(NO_PERSON_EXIST);
        }
        log.debug("returning Access for login \'{}\': {}", login, access);
        return responseSuccess(ACCESS_RETURNED, access);
    }

    @PutMapping
    public ResponseEntity<MoneyCalcRs<Access>> updateAccess(@RequestBody Access access) {
        throw new UnsupportedOperationException("Not supported yet");
    }
}
