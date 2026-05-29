package com.openclassrooms.starterjwt.security;

import com.openclassrooms.starterjwt.security.jwt.AuthTokenFilter;
import com.openclassrooms.starterjwt.security.services.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WebSecurityConfigTest {

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    @Mock
    private AuthenticationManager authenticationManager;

    @Test
    void passwordEncoder_shouldReturnBCryptPasswordEncoder() {
        WebSecurityConfig config = new WebSecurityConfig();

        PasswordEncoder encoder = config.passwordEncoder();

        assertInstanceOf(BCryptPasswordEncoder.class, encoder);
    }

    @Test
    void authenticationJwtTokenFilter_shouldReturnAuthTokenFilter() {
        WebSecurityConfig config = new WebSecurityConfig();

        AuthTokenFilter filter = config.authenticationJwtTokenFilter();

        assertNotNull(filter);
        assertInstanceOf(AuthTokenFilter.class, filter);
    }

    @Test
    void authenticationProvider_shouldWireUserDetailsServiceAndPasswordEncoder() {
        WebSecurityConfig config = new WebSecurityConfig();
        ReflectionTestUtils.setField(config, "userDetailsService", userDetailsService);

        DaoAuthenticationProvider provider = config.authenticationProvider();

        assertSame(userDetailsService, ReflectionTestUtils.getField(provider, "userDetailsService"));
        assertNotNull(ReflectionTestUtils.getField(provider, "passwordEncoder"));
        assertTrue(new BCryptPasswordEncoder().matches("password123", new BCryptPasswordEncoder().encode("password123")));
    }

    @Test
    void authenticationManager_shouldDelegateToAuthenticationConfiguration() throws Exception {
        WebSecurityConfig config = new WebSecurityConfig();
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(authenticationManager);

        AuthenticationManager result = config.authenticationManager(authenticationConfiguration);

        assertSame(authenticationManager, result);
    }
}
