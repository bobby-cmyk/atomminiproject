package vttp.miniproject.atomnotes.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .authorizeHttpRequests(
                    authorizeHttp -> {
                        authorizeHttp.requestMatchers("/signup", "/login", "/css/**", "/logos/**").permitAll();
                        authorizeHttp.anyRequest().authenticated();
                    }
                )
                .formLogin(login -> {
                    login.loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/task/all", true)
                        .failureUrl("/login?error=true");
                })
                .build();
    }
}
