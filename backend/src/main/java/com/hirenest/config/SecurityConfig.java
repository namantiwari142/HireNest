package com.hirenest.config;

import com.hirenest.security.JwtAuthenticationFilter;
import com.hirenest.security.OAuth2SuccessHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final UserDetailsService userDetailsService;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${app.cors.allowed-origins:}")
    private String extraAllowedOrigins;

    public SecurityConfig(
            JwtAuthenticationFilter jwtFilter,
            OAuth2SuccessHandler oAuth2SuccessHandler,
            UserDetailsService userDetailsService
    ) {
        this.jwtFilter = jwtFilter;
        this.oAuth2SuccessHandler = oAuth2SuccessHandler;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        List<String> patterns = new ArrayList<>(List.of(
                "https://hire-nest-iota.vercel.app",
                "https://hire-nest-*.vercel.app",
                "https://*.vercel.app",
                "http://localhost:*",
                "http://127.0.0.1:*"
        ));

        if (frontendUrl != null && !frontendUrl.isBlank()) {
            patterns.add(frontendUrl.trim().replaceAll("/+$", ""));
        }

        if (extraAllowedOrigins != null && !extraAllowedOrigins.isBlank()) {
            Arrays.stream(extraAllowedOrigins.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .forEach(patterns::add);
        }

        config.setAllowedOriginPatterns(patterns.stream().distinct().collect(Collectors.toList()));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            CorsConfigurationSource corsConfigurationSource
    ) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/login", "/api/auth/register", "/oauth2/**", "/login/oauth2/**").permitAll()
                        .requestMatchers("/api/auth/me").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/jobs/**", "/api/public/**").permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/recruiter/**").hasAnyRole("RECRUITER", "ADMIN")
                        .requestMatchers("/api/applicant/**").hasAnyRole("APPLICANT", "ADMIN")
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth.successHandler(oAuth2SuccessHandler))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
