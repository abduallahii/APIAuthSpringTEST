package com.ateamgroup.stoolbe.configuration;


import com.ateamgroup.stoolbe.constant.SecurityConstant;
import com.ateamgroup.stoolbe.filter.JwtAccessDeniedHandler;
import com.ateamgroup.stoolbe.filter.JwtAuthenticationEntryPoint;
import com.ateamgroup.stoolbe.filter.JwtAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {


    private JwtAuthorizationFilter jwtAuthorizationFilter ;
    private JwtAccessDeniedHandler jwtAccessDeniedHandler ;
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint ;
    private UserDetailsService userDetailsService ;
    private BCryptPasswordEncoder bCryptPasswordEncoder ;

    @Autowired
    public SecurityConfiguration(JwtAuthorizationFilter jwtAuthorizationFilter,
                                 JwtAccessDeniedHandler jwtAccessDeniedHandler,
                                 JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                                 @Qualifier("UserDetailsService") UserDetailsService userDetailsService,
                                 BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.userDetailsService = userDetailsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    // override default authentication to user ours
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    // which URL to secure
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // disable csrf && enable cors to manage who can access backend session management
        // stateless to stop tracking who is logged in who is not
        http.csrf().disable().cors().and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(SecurityConstant.PUBLIC_URLS) // allowed urls
                .permitAll()
                .anyRequest()
                .authenticated().and() // any other urls needs authentication
                .exceptionHandling().accessDeniedHandler(jwtAccessDeniedHandler) // handler
                .authenticationEntryPoint(jwtAuthenticationEntryPoint) // authentication start point
                .and()
                .addFilterBefore(jwtAuthorizationFilter , UsernamePasswordAuthenticationFilter.class);
        //used filter before any other filter

    }


    // main method
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean() ;
    }



}
