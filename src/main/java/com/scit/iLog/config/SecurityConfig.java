package com.scit.iLog.config;

import com.scit.iLog.domain.MemberEntity;
import com.scit.iLog.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final MemberRepository memberRepository;

    @Bean
    SecurityFilterChain httpSecurity(HttpSecurity http) throws Exception {
        http.csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable());
        http.authorizeHttpRequests(auth ->
                auth
                        .requestMatchers(
                                "/",
                                "/auth/signIn",
                                "/auth/signUp",
                                "/auth/idPwFind",
                                "/member/join",
                                "/children/**",
                                "/children/analysis/**",
                                "/children/analysisResult",
                                "/children/analysisResults",
                                "/children/statisticsDetails",
                                "/member/join",
                                "/member/*/info",
                                "/guides",
                                "/guides/guideListView",	// 25/02/10 김보경 추가
                                "/dashboard",
                                "/children/diaryDetails", // 25/2/5 로그인 기능 구현시, hasAnyRole 쪽으로 옮겨야 함
                                "/children/details",	  // 25/2/5 로그인 기능 구현시, hasAnyRole 쪽으로 옮겨야 함
                                "/parentDashboard",
                                "/teacherDashboard",
                                "/surveys",
                                "/children/diaryDetails", // 로그인 기능 구현시, hasAnyRole 쪽으로 옮겨야 함
                                "/customerCenter",
                                "/member/duplicate",
                                "/member/joinProc",
                                "/member/idCheck",
                                "/member/login",
                                "/board/boardList",
                                "/children/diaries",
                                "/children/diaries/new",
                                "/children/infoDetails",
                                "/children/detailsInsert",
                                "/board/boardDetail",
                                "/board/download",
                                "/reply/replyInsert",
                                "/children/diaryStatistics",
                                "/children/diaries",
                                "/children/details",
                                "/survey/surveysList",	//2025-02-07 이도훈 추가
                                "/survey/surveySelect",	//2025-02-07 이도훈 추가
                                "/survey/surveyHealth",	//2025-02-07 이도훈 추가
                                "/survey/surveyMental",	//2025-02-07 이도훈 추가
                                "/analysis/**",				// 2025.02.06_확인하고 싶어 추가해둠!
                                "/js/**",
                                "/css/**",
                                "/images/**")
                        .permitAll()
                        .requestMatchers(
                                "/member/logout",
                                "/member/my/update",
                                "/member/deleteUser",
                                "/board/boardWrite",
                                "/board/boardUpdate",
                                "/board/boardDelete",
                                "/reply/replyWrite",
                                "/reply/replyDelete",
                                "/mypage/**",
                                "/member/mypage"
                        ).hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/admin/**")
                        .hasRole("ADMIN")
                        .anyRequest()
                        .authenticated()
        );
        http.formLogin(formAuth ->
                formAuth
                        .loginPage("/auth/signIn")
                        .loginProcessingUrl("/auth/signIn")
//                        .successHandler(loginSuccessHandler) //(추가) 로그인 성공시 처리할 핸들러 등록
//                        .failureHandler(loginFailureHandler) //(추가) 로그인 실패시 처리할 핸들러 등록
                        .usernameParameter("userId")
                        .passwordParameter("userPwd")
                        .defaultSuccessUrl("/")
//                        .failureUrl("/member/login?error=true") //핸들러를 등록하면 필요없음
        );
        http.logout(logout ->
                logout
                        .logoutUrl("/member/logout")
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

    @Bean
    UserDetailsService userDetailsService() {
        return (username) -> {
            MemberEntity member = memberRepository
                    .findByUserId(username)
                    .orElseThrow(() -> new EntityNotFoundException("로그인 처리시 회원 조회 실패"));
            return new MemberDetails(
                    member.getUserId(),
                    member.getPassword(),
                    member.getRole().name()
            );
        };
    }

    @RequiredArgsConstructor
    static final class MemberDetails implements UserDetails {
        private final String userId;
        private final String password;
        private final String role;

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return List.of(new SimpleGrantedAuthority(this.role));
        }

        @Override
        public String getPassword() {
            return this.password;
        }

        @Override
        public String getUsername() {
            return this.userId;
        }
    }
}

