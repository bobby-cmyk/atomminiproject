package vttp.miniproject.atomnotes.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import vttp.miniproject.atomnotes.securityFilters.ApiTokenFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private ApiTokenFilter apiTokenFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                // To allow API key access
                .csrf(
                    csrf -> {
                        csrf.ignoringRequestMatchers(
                            request -> request.getHeader("Authorization") != null);
                        }
                )
                .authorizeHttpRequests(
                    authorizeHttp -> {
                        authorizeHttp.requestMatchers(
                            "/signup", 
                            "/login", 
                            "/logout",
                            "/oauth2/authorization/**",
                            "/login/oauth2/**",
                            "/css/**", 
                            "/logos/**",
                            "/favicon/**")
                            .permitAll();
                        authorizeHttp.anyRequest().authenticated();
                    }
                )

                .addFilterBefore(apiTokenFilter, UsernamePasswordAuthenticationFilter.class)

                .formLogin(login -> {
                    login
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/task/all", true)
                        .failureUrl("/login?error=true")
                        .permitAll();
                })
                .oauth2Login(oauth2 -> {
                    oauth2
                        .loginPage("/login")
                        .defaultSuccessUrl("/task/all", true)
                        .failureUrl("/login?error=true");
                })
                .logout(logout -> {
                    logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID", 
                            "XSRF-TOKEN", 
                            "SESSION", 
                            "OAuth2AuthorizationRequest");
                })
                .build();
    }
}
