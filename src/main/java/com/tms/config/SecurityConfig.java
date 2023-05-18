package com.tms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.tms.filters.UserAuthenticationFilter;
import com.tms.filters.UserAuthorizationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final UserDetailsService userDetailsService;

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}

	UserAuthenticationFilter getUserAuthenticationFilter() throws Exception {
		UserAuthenticationFilter userAuthenticationFilter = new UserAuthenticationFilter(authenticationManagerBean());
		userAuthenticationFilter.setFilterProcessesUrl("/login");
		return userAuthenticationFilter;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.cors().and().authorizeRequests().antMatchers("/login").permitAll()
		.and()
		.authorizeRequests().antMatchers("/swagger-ui/index.html", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
		.and()
		.authorizeRequests().antMatchers(HttpMethod.GET, "/employees").permitAll()
		.and()
		.authorizeRequests().antMatchers(HttpMethod.POST, "/employees/save").permitAll()
		.and()
		.authorizeRequests().antMatchers(HttpMethod.GET, "/employees/exist/*").permitAll()
		.and()
		.authorizeRequests().antMatchers(HttpMethod.GET, "/employees/refresh_token").permitAll()
		.and()
		.authorizeRequests().anyRequest().authenticated()
		.and()

		.addFilter(getUserAuthenticationFilter())
		.addFilterBefore(new UserAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/swagger-resources/", "/webjars/").antMatchers(HttpMethod.OPTIONS, "/**");
	}

}