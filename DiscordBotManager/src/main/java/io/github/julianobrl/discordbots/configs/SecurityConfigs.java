package io.github.julianobrl.discordbots.configs;

import io.github.julianobrl.discordbots.exceptions.handlers.ApplicationAccessDeniedHandler;
import io.github.julianobrl.discordbots.exceptions.handlers.ApplicationAuthenticationEntryPoint;
import io.github.julianobrl.discordbots.filters.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfigs {

    private final ApplicationAuthenticationEntryPoint authenticationEntryPoint;
    private final ApplicationAccessDeniedHandler accessDeniedHandler;
    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http

            // NO SECURITY
            .authorizeHttpRequests(
                mather ->
                    mather
                        .requestMatchers(
                            "/swagger-ui.html",
                            "/swagger-ui/*",
                            "/v3/api-docs",
                            "/v3/api-docs/swagger-config",
                            "/h2-console", "/h2-console/*", "/h2-console/**",
                             "/actuator", "/actuator/**")
                        .permitAll())

            // LOGIN ENDPOINTS REST
            .authorizeHttpRequests(
                matcher ->
                    matcher
                        .requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/login")
                        .permitAll())

            // LOGIN ENDPOINTS WEB
            .authorizeHttpRequests(
                    matcher ->
                            matcher
                                    .requestMatchers(HttpMethod.GET, "/auth/login", "/css/**", "/js/**", "/images/**", "/favicon.ico")
                                    .permitAll())

            // OTHER ENDPOINTS
            .authorizeHttpRequests(matcher -> matcher.anyRequest().authenticated())

            // CSRF
            .csrf(AbstractHttpConfigurer::disable)

            // HEADERS
            .headers(
                    httpSecurityHeadersConfigurer ->
                            httpSecurityHeadersConfigurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))

            // SESSION MANAGEMENT
            .sessionManagement(
                    configurer ->
                            configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // FILTERS
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

            // EXCEPTION HANDLING
            .exceptionHandling(
                    customizer ->
                            customizer
                                    .accessDeniedHandler(accessDeniedHandler)
                                    .authenticationEntryPoint(authenticationEntryPoint));

        return http.build();
    }

}
