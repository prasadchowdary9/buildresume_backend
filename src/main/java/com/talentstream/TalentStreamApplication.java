package com.talentstream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.validation.Cas30ServiceTicketValidator;
import org.jasig.cas.client.validation.TicketValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;



@SpringBootApplication
@EnableWebMvc
@EnableScheduling
public class TalentStreamApplication {
	private static final Logger logger = LoggerFactory.getLogger(TalentStreamApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(TalentStreamApplication.class, args);
	}
	@Bean  
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}
	 @Bean
	    public Docket api() {
	        return new Docket(DocumentationType.SWAGGER_2)
	                .select()
	                .apis(RequestHandlerSelectors.basePackage("com.talentstream")) 
	                .paths(PathSelectors.any())
	                .build();
	    }
	 @Bean
	    public CasAuthenticationFilter casAuthenticationFilter(
	      AuthenticationManager authenticationManager,
	      ServiceProperties serviceProperties) throws Exception {
	        CasAuthenticationFilter filter = new CasAuthenticationFilter();
	        filter.setAuthenticationManager(authenticationManager);
	        filter.setServiceProperties(serviceProperties);
	        return filter;
	    }

	    @Bean
	    public ServiceProperties serviceProperties() {
	        logger.info("service properties");
	        ServiceProperties serviceProperties = new ServiceProperties();
	        serviceProperties.setService("https://localhost:8900/login");
	        serviceProperties.setSendRenew(false);
	        return serviceProperties;
	    }

	    @Bean
	    public TicketValidator ticketValidator() {
	        return new Cas30ServiceTicketValidator("https://localhost:8443/cas");
	    }

	    @Bean
	    public CasAuthenticationProvider casAuthenticationProvider(
	      TicketValidator ticketValidator,
	      ServiceProperties serviceProperties) {
	        CasAuthenticationProvider provider = new CasAuthenticationProvider();
	        provider.setServiceProperties(serviceProperties);
	        provider.setTicketValidator(ticketValidator);
	        provider.setUserDetailsService(
	          s -> new User("username", "password", true, true, true, true,
	          AuthorityUtils.createAuthorityList("ROLE_ADMIN")));
	        provider.setKey("CAS_PROVIDER_LOCALHOST_8900");
	        return provider;
	    }


	    @Bean
	    public SecurityContextLogoutHandler securityContextLogoutHandler() {
	        return new SecurityContextLogoutHandler();
	    }

	    @Bean
	    public LogoutFilter logoutFilter() {
	        LogoutFilter logoutFilter = new LogoutFilter("https://localhost:8443/logout", securityContextLogoutHandler());
	        logoutFilter.setFilterProcessesUrl("/logout/cas");
	        return logoutFilter;
	    }

	    @Bean
	    public SingleSignOutFilter singleSignOutFilter() {
	        SingleSignOutFilter singleSignOutFilter = new SingleSignOutFilter();
	        singleSignOutFilter.setLogoutCallbackPath("/exit/cas");
	        singleSignOutFilter.setIgnoreInitConfiguration(true);
	        return singleSignOutFilter;
	    }
	 }