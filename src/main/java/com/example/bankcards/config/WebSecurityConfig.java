package com.example.bankcards.config;

import com.example.bankcards.security.jwt.AccessDeniedHandlerJwt;
import com.example.bankcards.security.jwt.AuthEntryPointJwt;
import com.example.bankcards.security.jwt.AuthTokenFilter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@SecurityScheme(
        type = SecuritySchemeType.HTTP,
        name = "bearerAuth",
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class WebSecurityConfig {

    private final AuthEntryPointJwt authEntryPointJwt;
    private final AccessDeniedHandlerJwt accessDeniedHandlerJwt;
    private final AuthTokenFilter authTokenFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {
        return security.csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                //.cors(cors -> cors.configurationSource(this.corsConfigurationSource()))

                .exceptionHandling(e -> e.authenticationEntryPoint(authEntryPointJwt).accessDeniedHandler(accessDeniedHandlerJwt))

                .addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class)

                .authorizeHttpRequests(auth -> auth.requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/actuator/health",
                                "/api/v1/user/createNew",
                                "/api/v1/authentification/signIn")
                        .permitAll()
                        .requestMatchers(
                                "/api/v1/card/getByIdWithMask/{cardId}",
                                "/api/v1/card/getByIdAndOwnerIdWithMask/{cardId}/{ownerId}",
                                "/api/v1/card/deleteById/{cardId}",
                                "/api/v1/card/getAllByOwnerId/{ownerId}",
                                "/api/v1/card/transfer",
                                "/api/v1/card/getBalance/{cardId}"
                        )
                        .hasAnyRole("USER", "ADMIN")
                        //.hasAnyAuthority("ROLE  _USER", "ROLE_ADMIN")
                        .requestMatchers("/api/v1/**").hasRole("ADMIN")
                )

                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(5);
    }


    /*@Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("*"));
        config.setAllowedMethods(List.of("*"));
        config.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }*/

}
