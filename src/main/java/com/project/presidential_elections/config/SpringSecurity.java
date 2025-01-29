package com.project.presidential_elections.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SpringSecurity {

    private final UserDetailsService userDetailsService;

    public SpringSecurity(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authorize) ->
                        authorize.requestMatchers("/user/register").permitAll()
                                .requestMatchers("/user/save").permitAll()
                                .requestMatchers("/home").permitAll()
                                .requestMatchers("/form").permitAll()
                                .requestMatchers("/submit").permitAll()
                                .requestMatchers("/user/home-page").hasAnyRole("ADMIN", "CANDIDATE", "USER")
                                .requestMatchers("/user/account").hasAnyRole("ADMIN", "CANDIDATE", "USER")
                                .requestMatchers("/user/description/page").hasAnyRole("ADMIN", "CANDIDATE", "USER")
                                .requestMatchers("/user/description/save").hasAnyRole("ADMIN", "CANDIDATE", "USER")
                                .requestMatchers("/candidate/apply").hasAnyRole("ADMIN", "CANDIDATE", "USER")
                                .requestMatchers("/candidate/profile/{id}").hasAnyRole("ADMIN", "CANDIDATE", "USER")
                                .requestMatchers("/candidate/profile/{id}/vote").hasAnyRole("ADMIN", "CANDIDATE", "USER")
                                .anyRequest().authenticated()
                ).formLogin(
                        form -> form
                                .loginPage("/user/login")
                                .loginProcessingUrl("/user/login")
                                .defaultSuccessUrl("/user/home-page")
                                .permitAll()
                ).logout(
                        logout -> logout
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                .permitAll()
                );
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(new AntPathRequestMatcher("/h2-console/**"));
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }
}
