package ru.strcss.projects.moneycalc.moneycalcserver.configuration.security.jwt;

import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import ru.strcss.projects.moneycalc.moneycalcserver.configuration.security.SecurityConstants;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;


public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    public JWTAuthorizationFilter(AuthenticationManager authManager, SecurityConstants securityPropertiesHolder) {
        super(authManager);
        this.securityPropertiesHolder = securityPropertiesHolder;
    }

    private SecurityConstants securityPropertiesHolder;

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader(securityPropertiesHolder.getHeaderString());

        if (header == null || !header.startsWith(securityPropertiesHolder.getTokenPrefix())) {
            chain.doFilter(req, res);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(securityPropertiesHolder.getHeaderString());
        if (token != null) {
            // parse the token.
            String user = Jwts.parser()
                    .setSigningKey(securityPropertiesHolder.getSecret().getBytes())
                    .parseClaimsJws(token.replace(securityPropertiesHolder.getTokenPrefix(), ""))
                    .getBody()
                    .getSubject();

            if (user != null) {
                return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
            }
            return null;
        }
        return null;
    }
}
