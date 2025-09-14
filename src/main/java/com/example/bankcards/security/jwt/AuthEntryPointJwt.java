package com.example.bankcards.security.jwt;

import com.example.bankcards.exception.ResponseError;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

@Component
@Slf4j
@RestControllerAdvice
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        log.error(String.format("Попытка обращения неавторизованного пользователя к ресурсу '%s'. Доступ запрещён",
                request.getRequestURI())
        );

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        this.mapper.writeValue(response.getOutputStream(),
                new ResponseError("Для доступа к этому ресурсу необходима авторизация"));
    }
}
