package com.devsuperior.bds03.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableResourceServer // process configuration to implements OAuth2 resource server
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
	
	@Autowired
	private Environment env;

	@Autowired
	private JwtTokenStore tokenStore;

	// open area
	private static final String[] PUBLIC = {
			"/oauth/token",
			"/h2-console/**"/* add more as needed */ };
	// /** means everything from that resource
	private static final String[] OPERATOR_GET = {
			"/departments/**", 
			"/employees/**" }; // only for admins and operators
	private static final String[] ADMIN = { 
			"/users/**" };

	@Override
	public void configure(ResourceServerSecurityConfigurer resources)
			throws Exception {
		resources.tokenStore(tokenStore);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		//H2
		if(Arrays.asList(env.getActiveProfiles() ).contains("test")){
			http.headers().frameOptions().disable();
		}
		
		http.authorizeRequests()
				.antMatchers(PUBLIC).permitAll() // for all resources listed in PUBLIC, free access
				.antMatchers(HttpMethod.GET, OPERATOR_GET).hasAnyRole("OPERATOR", "ADMIN") // "OPERATOR","ADMIN"  can GET in these resources 
				.anyRequest().hasRole("ADMIN"); //for anyother resource, user must be authenticated
	}

}
