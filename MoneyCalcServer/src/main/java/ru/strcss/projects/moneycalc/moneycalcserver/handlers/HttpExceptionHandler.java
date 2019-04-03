package ru.strcss.projects.moneycalc.moneycalcserver.handlers;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.strcss.projects.moneycalc.moneycalcserver.model.exceptions.ErrorDescription;
import ru.strcss.projects.moneycalc.moneycalcserver.model.exceptions.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * Created by Stormcss
 * Date: 31.03.2019
 */
@Slf4j
@ControllerAdvice
public class HttpExceptionHandler {

    @ExceptionHandler(RequestFailedException.class)
    public ResponseEntity requestFailedExceptionHandler(RequestFailedException e) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        ErrorDescription errorDescription = new ErrorDescription();
        errorDescription.setUserMessage(e.getMessage());
        return new ResponseEntity<>(errorDescription, headers, e.getHttpStatus());
    }

    @ExceptionHandler({JsonParseException.class, JsonMappingException.class, MismatchedInputException.class, HttpMessageNotReadableException.class})
    public ResponseEntity jsonParseExceptionHandler(Exception e, HttpServletRequest request) {
        log.error("Unable to parse input message! Exception message: {}", e.getMessage());
        ErrorDescription errorDescription = new ErrorDescription();
        errorDescription.setUserMessage("Некорректный формат сообщения");
        errorDescription.setErrorCode("IncorrectFormatMessage");
        return new ResponseEntity<>(errorDescription, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({PersistenceException.class})
    public ResponseEntity sqlConnectionExceptionHandler(Exception e, HttpServletRequest request) {
        log.error("Unable to connect DB! Exception message:", e.getMessage());
        ErrorDescription errorDescription = new ErrorDescription();
        errorDescription.setUserMessage("Ошибка связи с БД");
        errorDescription.setErrorCode("SqlConnectionError");
        return new ResponseEntity<>(errorDescription, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity othersExceptionHandler(Exception e, HttpServletRequest request) {
        log.error("Other Exception message: {}", e);
        ErrorDescription errorDescription = new ErrorDescription();
        errorDescription.setDeveloperMessage(e.toString() + ": " + Arrays.toString(e.getStackTrace()));
        errorDescription.setUserMessage("Внутренняя ошибка MoneyCalcServer");
        errorDescription.setErrorCode("UntypedError");
        return new ResponseEntity<>(errorDescription, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
