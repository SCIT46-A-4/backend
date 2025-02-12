package com.scit.iLog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class JpaConfig {
	//25/2/12 ㅈ: LastModified랑 create어쩌고 하는 애너테이션 쓰려면 이 config 필요
}
