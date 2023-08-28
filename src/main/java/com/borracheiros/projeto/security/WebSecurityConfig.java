  package com.borracheiros.projeto.security;

  import org.springframework.context.annotation.Bean;
  import org.springframework.context.annotation.Configuration;
  import org.springframework.http.HttpMethod;
  import org.springframework.security.config.annotation.web.builders.HttpSecurity;
  import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
  import org.springframework.security.config.http.SessionCreationPolicy;
  import org.springframework.security.web.SecurityFilterChain;
  import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

  @Configuration
  @EnableWebSecurity
  public class WebSecurityConfig {

  //  @Bean
  //   public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

  // return http.build();
  //   }

    // @Bean
    // public PasswordEncoder passwordEncoder() {
    // return new BCryptPasswordEncoder();
    // }
  }
