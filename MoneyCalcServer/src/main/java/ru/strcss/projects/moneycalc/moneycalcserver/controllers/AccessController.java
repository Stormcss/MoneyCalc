package ru.strcss.projects.moneycalc.moneycalcserver.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Access;
import ru.strcss.projects.moneycalc.moneycalcserver.mapper.AccessMapper;
import ru.strcss.projects.moneycalc.moneycalcserver.model.exceptions.RequestFailedException;

import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.NO_PERSON_EXIST;

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
    public Access getAccess() {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        Access access = accessMapper.getAccess(login);

        if (access == null) {
            log.error("Can not return Access for login \'{}\' - no Person found", login);
            throw new RequestFailedException(HttpStatus.NOT_FOUND, NO_PERSON_EXIST);
        }
        log.debug("returning Access for login \'{}\': {}", login, access);
        return access;
    }

    @PutMapping
    public Access updateAccess(@RequestBody Access access) {
        throw new UnsupportedOperationException("Not supported yet");
    }
}
