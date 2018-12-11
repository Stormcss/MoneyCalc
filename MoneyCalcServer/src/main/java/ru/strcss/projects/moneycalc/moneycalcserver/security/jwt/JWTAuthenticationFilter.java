package ru.strcss.projects.moneycalc.moneycalcserver.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.strcss.projects.moneycalc.entities.Access;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static ru.strcss.projects.moneycalc.moneycalcserver.security.SecurityConstants.EXPIRATION_TIME;
import static ru.strcss.projects.moneycalc.moneycalcserver.security.SecurityConstants.HEADER_STRING;
import static ru.strcss.projects.moneycalc.moneycalcserver.security.SecurityConstants.SECRET;
import static ru.strcss.projects.moneycalc.moneycalcserver.security.SecurityConstants.TOKEN_PREFIX;

@Slf4j
@AllArgsConstructor
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) {
        Access access = null;
        try {
            access = new ObjectMapper()
                    .readValue(req.getInputStream(), Access.class);

            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            access.getLogin(),
                            access.getPassword(),
                            new ArrayList<>())
            );
        } catch (BadCredentialsException bce) {
            log.info("Bad credentials for '{}'", access);
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain,
                                            Authentication auth) {

        String token = Jwts.builder()
                .setSubject(((User) auth.getPrincipal()).getUsername())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET.getBytes())
                .compact();
        res.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
    }
}
