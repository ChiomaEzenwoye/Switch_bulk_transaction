package com.example.switchbulktransaction.config.security;


import com.example.switchbulktransaction.config.cryptography.Keymanager;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private Keymanager keymanager;

    @Bean
    @Order(1)
    public SecurityFilterChain authServerSecurity(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        return http.build();
    }

    @Bean public SecurityFilterChain resourceServerSecurity(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(
                        auth -> auth .requestMatchers("/api/admin/**","/actuator/**" ).hasRole("ADMIN")
                                .requestMatchers("/api/v1/bulk-transactions").hasRole("USER")
                                .requestMatchers("/h2-console/**").permitAll()
                                .anyRequest().authenticated() )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())) );

        return http.build();
    }


    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Collection<String> roles = jwt.getClaimAsStringList("roles");
            if (roles == null || roles.isEmpty()) {
                return List.of();
            }
          
            return roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toList());
        });
        return converter;
    }


    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        Collection<String> roles = jwt.getClaimAsStringList("roles");
        if (roles == null) {
            roles = List.of();
        }

        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }


    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                .issuer("http://localhost:8081")
                .build();
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() throws Exception {
        RSAKey rsaKey = new RSAKey.Builder((java.security.interfaces.RSAPublicKey) keymanager.getPublicKey())
                .privateKey(keymanager.getPrivateKey())
                .keyID("auth-server-key")
                .build();
        return new ImmutableJWKSet<>(new JWKSet(rsaKey));
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) throws Exception {
        RSAKey rsaKey = (RSAKey) jwkSource
                .get(new JWKSelector(new JWKMatcher.Builder().keyType(KeyType.RSA).build()), null)
                .get(0);

        return NimbusJwtDecoder.withPublicKey(rsaKey.toRSAPublicKey()).build();
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
        return context -> {
            if (AuthorizationGrantType.CLIENT_CREDENTIALS.equals(context.getAuthorizationGrantType())) {
                RegisteredClient client = context.getRegisteredClient();


                List<String> roles;
                switch (client.getClientId()) {
                    case "user-client-id":
                        roles = List.of("USER");
                        break;
                    case "admin-client-id":
                        roles = List.of("ADMIN");
                        break;
                    default:
                        roles = List.of();
                }

                context.getClaims().claim("roles", roles);
            }
        };
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        RegisteredClient userClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("user-client-id")
                .clientSecret(encoder.encode("secret"))
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("https://service-a.com/login/oauth2/code/serviceA")
                .scope("read:transactions")
                .scope("write:transactions")
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(false).build())
                .build();

        RegisteredClient adminClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("admin-client-id")
                .clientSecret(encoder.encode("secret"))
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .scope("read:transactions")
                .scope("write:transactions")
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(false).build())
                .build();

        return new InMemoryRegisteredClientRepository(userClient, adminClient);
    }


}
