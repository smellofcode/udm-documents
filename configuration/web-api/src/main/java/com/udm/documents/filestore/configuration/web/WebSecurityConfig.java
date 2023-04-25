/*
MIT License

Copyright (c) 2023 smellofcode

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package com.udm.documents.filestore.configuration.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = false)
@Configuration
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.cors()
                .and()
                .csrf()
                .disable()
                .httpBasic()
                .disable()
                .formLogin()
                .disable()
                .authorizeHttpRequests(requestCustomizer -> requestCustomizer
                        .requestMatchers(HttpMethod.OPTIONS, "/**")
                        .permitAll()
                        .requestMatchers(
                                "/documents/**",
                                "/actuator/**",
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/swagger-ui",
                                "/swagger-ui/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/raw-costs")
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                // .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
                .build();
    }

    /**
     * Since we are using a custom header we need to instruct the token resolver to look for a header with the name
     * "X-UDM-TOKEN" instead of the standard "Authorization" header. There it should find the bearer token.
     */
    //    @Bean
    //    BearerTokenResolver bearerTokenResolver() {
    //        var bearerTokenResolver = new DefaultBearerTokenResolver();
    //        bearerTokenResolver.setBearerTokenHeaderName("X-UDM-TOKEN");
    //        return bearerTokenResolver;
    //    }

    /**
     * Parse the token's authorities and store them in the security context, so that annotations such as @PreAuthorize
     * can be used to control a user's access to certain methods.
     */
    //    @Bean
    //    public JwtAuthenticationConverter jwtAuthenticationConverter() {
    //        var grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    //        grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
    //        grantedAuthoritiesConverter.setAuthorityPrefix("");
    //
    //        var jwtAuthenticationConverter = new JwtAuthenticationConverter();
    //        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
    //        return jwtAuthenticationConverter;
    //    }
}
