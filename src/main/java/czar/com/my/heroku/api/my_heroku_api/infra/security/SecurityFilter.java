package czar.com.my.heroku.api.my_heroku_api.infra.security;

import com.auth0.jwt.JWT;
import czar.com.my.heroku.api.my_heroku_api.models.Users;
import czar.com.my.heroku.api.my_heroku_api.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    @Autowired
    TokenService tokenService;

    @Autowired
    UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = recoverToken(request);
        String login = tokenService.validateToken(token);

        if(login != null) {
            assert token != null;
            String tokenType = JWT.decode(token).getClaim("type").asString();

            if (!"accessToken".equals(tokenType)) {
                throw new RuntimeException("Invalid or missing token");
            }

            Users user = userRepository.findByEmail(login).orElseThrow(() -> new RuntimeException("User Not Found"));
            List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null) {
            return  null;
        }

        return authHeader.replace("Bearer ", "");
    }
}
