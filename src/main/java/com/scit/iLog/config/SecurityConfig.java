package com.scit.iLog.config;

import com.scit.iLog.domain.RelationType;
import com.scit.iLog.domain.member.MemberEntity;
import com.scit.iLog.domain.member.MemberRole;
import com.scit.iLog.exception.WrongSignInIdException;
import com.scit.iLog.repository.MemberRepository;
import com.scit.iLog.util.LoginFailureHandler;
import com.scit.iLog.util.LoginSuccessHandler;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final MemberRepository memberRepository;

    private final LoginSuccessHandler loginSuccessHandler;
	private final LoginFailureHandler loginFailureHandler;

    @Bean
    SecurityFilterChain httpSecurity(HttpSecurity http) throws Exception {
        http.csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable());
        http.authorizeHttpRequests(auth ->
                auth
                		.requestMatchers(HttpMethod.DELETE, "/children/**").permitAll()
                        .requestMatchers(
                                "/",
                                "/**",
                                "/auth/signIn",
                                "/auth/signUp",
                                "/auth/checkSignInIdExists",	// 2025-02-17~20 이도훈 추가
                                "/auth/idPwFind",
                                "/member/join",
                                "/member/*/info",
                                "/children/**",
                                "/guides",
                                "/guides/guideListView",	// 25/02/10 김보경 추가
                                "/dashboard",
                                "/parentDashboard",
                                "/teacherDashboard",
                                "/surveys",
                                "/claims",
                                "/claims/new",
                                "/claims/insertClaim",
                                "/member/duplicate",
                                "/member/joinProc",
                                "/member/idCheck",
                                "/member/login",
                                "/board/boardList",
                                "/board/boardDetail",
                                "/board/download",
                                "/reply/replyInsert",
                                "/survey/surveysList",	//2025-02-07 이도훈 추가
                                "/survey/surveySelect",	//2025-02-07 이도훈 추가
                                "/survey/surveyHealth",	//2025-02-07 이도훈 추가
                                "/survey/surveyMental",	//2025-02-07 이도훈 추가
                                "/analysis/**",				// 2025.02.06_확인하고 싶어 추가해둠!
                                "/js/**",
                                "/css/**",
                                "/images/**"
                                )
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
                        // 2025-02-17~20 이도훈 추가
                        .successHandler(loginSuccessHandler) //(추가) 로그인 성공시 처리할 핸들러 등록
                        // 2025-02-17~20 이도훈 추가
                        .failureHandler(loginFailureHandler) //(추가) 로그인 실패시 처리할 핸들러 등록
                        .usernameParameter("signInId")
                        .passwordParameter("userPwd")
//                        .defaultSuccessUrl("/")
//                        .failureUrl("/member/login?error=true") //핸들러를 등록하면 필요없음
        );
        http.logout(logout ->
                logout
                        .logoutUrl("/auth/logout")
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


    //로그인 시 검증
    /**
     * 2025-02-17~20 이도훈
     * 커스텀 예외처리 추가.
     * @return
     */
    @Bean
    UserDetailsService userDetailsService() {
        return (signInId) -> {
        	//signInId를 이용해 DB에서 사용자 정보를 찾음. Optional<MemberEntity>을 반환.
        	MemberEntity member = memberRepository
                    .findBySignInId(signInId)
                    //사용자가 존재하지 않으면 커스텀 예외(WrongSignInIdException)를 발생시킴.
                    .orElseThrow(() -> new WrongSignInIdException(
                    		String.format(
                    				"회원 조회 실패 아이디 : %s", signInId
                    				)
                    		));
            //사용자가 존재할 경우, MemberDetails 객체 생성 및 반환
            return MemberDetails.builder()
                            .id(member.getId())
                            .name(member.getName())
                            .signInId(member.getSignInId())
                            .password(member.getPassword())
                            .email(member.getEmail())
                            .relationType(member.getRelationType())
                            .role(member.getRole())
                            .personalInformationCollectionAndUsageAgreement(member.isPersonalInformationCollectionAndUsageAgreement())
                            .build();
        };
    }

    @Getter
    @Builder
    @RequiredArgsConstructor
    public static final class MemberDetails implements UserDetails {
        private final Long id;
        private final String name;
        private final String signInId;
        private final String password;
        private final String email;
        private final RelationType relationType;
        private final MemberRole role;
        private final Boolean personalInformationCollectionAndUsageAgreement;

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return List.of(new SimpleGrantedAuthority(this.role.toString()));
        }

        //실제 패스워드로 사용하는 변수
        @Override
        public String getPassword() {
            return this.password;
        }

        //실제 아이디를 사용하는 변수
        @Override
        public String getUsername() {
            return this.signInId;
        }

        @Override
        public boolean isAccountNonExpired() {
            return UserDetails.super.isAccountNonExpired();
        }

        @Override
        public boolean isAccountNonLocked() {
            return UserDetails.super.isAccountNonLocked();
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return UserDetails.super.isCredentialsNonExpired();
        }

        @Override
        public boolean isEnabled() {
            return UserDetails.super.isEnabled();
        }
    }
}

