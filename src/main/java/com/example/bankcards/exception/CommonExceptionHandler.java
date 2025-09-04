package com.example.bankcards.exception;

import com.example.bankcards.enums.ClassNameForSaveExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;

@RestControllerAdvice
@Slf4j
public class CommonExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseError handleUsernameNotFoundDateException(UsernameNotFoundException exception) {
        log.error(exception.getMessage(), exception);
        return new ResponseError(exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseError handleUserNotFoundException(UserNotFoundException exception) {
        log.error(exception.getMessage(), exception);
        return new ResponseError(exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseError handleCardNotFoundException(CardNotFoundException exception) {
        log.error(exception.getMessage(), exception);
        return new ResponseError(exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseError handleUserAlreadyExistsException(UserAlreadyExistsException exception) {
        log.error(exception.getMessage(), exception);
        return new ResponseError(exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseError handleOtherException(OtherException exception) {
        log.error(exception.getMessage(), exception);
        return new ResponseError(exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseError handleOperationNotAllowedException(OperationNotAllowedException exception) {
        log.error(exception.getMessage(), exception);
        return new ResponseError(exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseError handleDecryptException(DecryptException exception) {
        log.error(exception.getMessage(), exception);
        return new ResponseError(exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseError handleEncryptException(EncryptException exception) {
        log.error(exception.getMessage(), exception);
        return new ResponseError(exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseError handleSaveException(DataIntegrityViolationException exception, HandlerMethod handlerMethod) {
        ClassNameForSaveExceptionHandler className = ClassNameForSaveExceptionHandler.getByValue(handlerMethod.getBeanType().getSimpleName());

        log.error(exception.getMessage(), exception);

        return switch (className) {
            case ADMIN_CONTROLLER -> new ResponseError( "Ошибка сохранения в классе администратора");
            case CARD_CONTROLLER -> new ResponseError( "Ошибка сохранения в классе карт");
            case USER_CONTROLLER -> new ResponseError("Ошибка сохранения в классе пользователей");
            default -> new ResponseError(new OtherException().getMessage());
        };
    }

}
