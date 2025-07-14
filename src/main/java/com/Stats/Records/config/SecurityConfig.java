package com.Stats.Records.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
               .httpBasic(httpBasic -> {}) // New way to enable HTTP Basic Authentication
                .csrf(csrf -> csrf.disable()); // Disable CSRF for stateless authentication

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withUsername("admin")
                .password("{noop}password") // {noop} is used for plain-text passwords (not recommended in production)
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(user);
    }
}
