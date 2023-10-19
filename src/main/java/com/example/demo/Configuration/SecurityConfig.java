package com.example.demo.Configuration;

import com.example.demo.Handlers.CustomSuccessHandler;
import com.example.demo.Security.UserDetailsServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.reactive.config.CorsRegistry;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    CustomSuccessHandler successHandler;
    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    };
    @Bean
    public UserDetailsService getUserDetails(){
        return new UserDetailsServiceImpl();
    }
    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity httpSecurity) throws Exception{
        httpSecurity
                .authorizeHttpRequests(
                        (request) -> request
                                .requestMatchers("/","/shop/**","/products/**","/login/**","/css/**","/js/**","/productImages/**","/home/**","/register/**","/cart/**","/verifyotp/**","/images/**","/img/**","/assets/**","/page/**","/pageShop/**").permitAll()
                                .requestMatchers("/users/**").hasAnyAuthority("user","admin")
                                .requestMatchers("/admin/**").hasAuthority("admin")
                                .anyRequest().authenticated()
                )
                .sessionManagement(
                        (session) -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                                .invalidSessionUrl("/login")
                                .maximumSessions(1)
                                .maxSessionsPreventsLogin(false)
                )
                .formLogin(
                        (login) -> login
                                .loginPage("/login")
                                .successHandler(successHandler)
                                .usernameParameter("email")
                                .failureHandler(new AuthenticationFailureHandler() {

                                    @Override
                                    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                                                        AuthenticationException exception) throws IOException, ServletException {
                                        String email = request.getParameter("email");
                                        String error = exception.getMessage();
                                        System.out.println("A failed login attempt with email: "
                                                + email + ". Reason: " + error);

                                        String redirectUrl = request.getContextPath() + "/login?error";
                                        response.sendRedirect(redirectUrl);
                                    }
                                })
                                .permitAll()
                )
//                .headers( headers -> headers
//                        .cacheControl()
//                        .disable())
                .logout(
                        logout -> logout
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                .invalidateHttpSession(true)
                                .deleteCookies("SESSION")
                                .permitAll()
                )
                .csrf(AbstractHttpConfigurer :: disable)
//                .cors(Customizer.withDefaults())
                .exceptionHandling(configurer -> configurer
                        .accessDeniedPage("/access-denied"));
        ;
        return httpSecurity.build();
    }
    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(this.getUserDetails());
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

//    public void addCorsMappings(CorsRegistry corsRegistry){
//        corsRegistry.addMapping("/**")
//                .allowedOrigins("http://localhost:8080/payNow")
//                .allowedMethods("GET","POST","PUT","DELETE");
//    }
}
