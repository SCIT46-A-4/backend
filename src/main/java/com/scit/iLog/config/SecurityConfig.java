package com.scit.iLog.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    SecurityFilterChain httpSecurity(HttpSecurity http) throws Exception {
        http.csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable());
        http.authorizeHttpRequests(auth ->
                auth
                        .requestMatchers(
                                "/",
                                "/children/analysis",
                                "/children/statisticsDetails",
                                "/member/join",
                                "/member/*/info",
                                "/guides",
                                "/dashboard",
                                "/children/diaryDetails", // 25/2/5 로그인 기능 구현시, hasAnyRole 쪽으로 옮겨야 함
                                "/children/details",	  // 25/2/5 로그인 기능 구현시, hasAnyRole 쪽으로 옮겨야 함
                                "/parentDashboard",
                                "/teacherDashboard",
                                "/surveys",
                                "/children/diaryDetails", // 로그인 기능 구현시, hasAnyRole 쪽으로 옮겨야 함
                                "/customerCenter",
                                "/user/duplicate",
                                "/user/joinProc",
                                "/user/idCheck",
                                "/user/login",
                                "/board/boardList",
                                "/children/diaries",
                                "/children/diaries/new",
                                "/children/infoDetails",
                                "/board/boardDetail",
                                "/board/download",
                                "/reply/replyInsert",
                                "/children/diaryStatistics",
                                "/children/diaries",
                                "/children/details",
                                "/children/surveyInsert",
                                "/children/surveyBody",
                                "/children/surveyMind",
                                "/js/**",
                                "/css/**",
                                "/images/**")
                        .permitAll()
                        .requestMatchers(
                                "/user/logout",
                                "/user/my/update",
                                "/user/deleteUser",
                                "/board/boardWrite",
                                "/board/boardUpdate",
                                "/board/boardDelete",
                                "/reply/replyWrite",
                                "/reply/replyDelete",
                                "/mypage/**",
                                "/user/mypage"
                        ).hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/admin/**")
                        .hasRole("ADMIN")
                        .anyRequest()
                        .authenticated()
        );
        http.formLogin(formAuth ->
                formAuth
                        .loginPage("/user/login")
                        .loginProcessingUrl("/user/loginProc")
//                        .successHandler(loginSuccessHandler) //(추가) 로그인 성공시 처리할 핸들러 등록
//                        .failureHandler(loginFailureHandler) //(추가) 로그인 실패시 처리할 핸들러 등록
                        .usernameParameter("userId")
                        .passwordParameter("userPwd")
                        .defaultSuccessUrl("/")
//                        .failureUrl("/user/login?error=true") //핸들러를 등록하면 필요없음
        );
        http.logout(logout ->
                logout
                        .logoutUrl("/user/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
        );
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
