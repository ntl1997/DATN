package com.poly.viettutor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder(); // mã hóa mật khẩu Bcrypt
        }

        @Bean // Đăng ký SecurityFilterChain cho ADMIN
        @Order(1) // Đặt thứ tự ưu tiên cho SecurityFilterChain
        public SecurityFilterChain adminFilterChain(HttpSecurity http) throws Exception {
                http.csrf(csrf -> csrf.disable()); // Tắt CSRF (Cross-Site Request Forgery)
                http.securityMatcher("/admin/**"); // Chỉ áp dụng cho các request bắt đầu bằng /admin
                http.authorizeHttpRequests(auth -> auth // Cấu hình phân quyền cho các request
                                .requestMatchers("/admin/login", "/admin").permitAll() // không cần đăng nhập
                                .anyRequest().hasRole("ADMIN")); // ADMIN mới được truy cập
                http.formLogin(login -> login
                                .loginPage("/admin/login")
                                .loginProcessingUrl("/admin/login")
                                .defaultSuccessUrl("/admin/dashboard")
                                .permitAll());
                http.logout(logout -> logout
                                .logoutUrl("/admin/logout")
                                .logoutSuccessUrl("/admin")
                                .permitAll());
                http.exceptionHandling(e -> e.accessDeniedPage("/admin/login?forbidden=true"));
                return http.build();
        }

        @Bean // SecurityFilterChain cho USER
        @Order(2) // Đặt thứ tự ưu tiên cho SecurityFilterChain
        public SecurityFilterChain userFilterChain(HttpSecurity http) throws Exception {
                http.csrf(csrf -> csrf.disable()); // Tắt CSRF (Cross-Site Request Forgery)
                http.authorizeHttpRequests(auth -> auth // Cấu hình phân quyền cho các request
                                .requestMatchers("/cart").authenticated() // yêu cầu đăng nhập
                                .requestMatchers("/enroll-course/**").authenticated() // yêu cầu đăng nhập
                                .requestMatchers("/lesson/**").authenticated() // yêu cầu đăng nhập
                                .requestMatchers("/student/**").authenticated() // yêu cầu đăng nhập
                                .requestMatchers("/quiz/**").authenticated() // yêu cầu đăng nhập
                                .requestMatchers("/instructor/**").hasAnyRole("INSTRUCTOR", "ADMIN")
                                // INSTRUCTOR hoặc ADMIN mới được truy cập
                                .anyRequest().permitAll()); // Tất cả các request khác đều được phép truy cập
                http.formLogin(login -> login
                                .loginPage("/login")
                                .loginProcessingUrl("/login")
                                .permitAll());
                http.logout(logout -> logout
                                .logoutUrl("/logout")
                                .logoutSuccessUrl("/")
                                .permitAll());
                return http.build();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
                return config.getAuthenticationManager();
        }

}
