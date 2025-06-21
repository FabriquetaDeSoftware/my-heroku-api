package czar.com.my.heroku.api.my_heroku_api.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import czar.com.my.heroku.api.my_heroku_api.dto.request.SignInRequestDto;
import czar.com.my.heroku.api.my_heroku_api.dto.request.SignUpRequestDto;
import czar.com.my.heroku.api.my_heroku_api.dto.response.SignInResponseDto;
import czar.com.my.heroku.api.my_heroku_api.dto.response.SignUpResponseDto;
import czar.com.my.heroku.api.my_heroku_api.infra.security.TokenService;
import czar.com.my.heroku.api.my_heroku_api.models.Users;
import czar.com.my.heroku.api.my_heroku_api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    public SignUpResponseDto signUp(SignUpRequestDto data) {
        Optional<Users> user = this.userRepository.findByEmail(data.email());

        if(user.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists with this email");
        }

        Users newUser = new Users();
        newUser.setEmail(data.email());
        newUser.setPassword(this.passwordEncoder.encode(data.password()));
        newUser.setName(data.name());

        this.userRepository.save(newUser);

        Map<String, String> tokens = this.tokenService.generateToken(newUser);

        String accessToken = tokens.get("accessToken");
        String refreshToken = tokens.get("refreshToken");

        return new SignUpResponseDto(accessToken, refreshToken, newUser.getName());
    }

    public SignInResponseDto signIn(SignInRequestDto data) {
        Users user = this.userRepository.findByEmail(data.email()).orElseThrow(() -> new RuntimeException("Email Or Password Invalid"));;

        if(!this.passwordEncoder.matches(data.password(), user.getPassword())) {
            throw new RuntimeException("Email Or Password Invalid");
        }

        Map<String, String> tokens = this.tokenService.generateToken(user);

        String accessToken = tokens.get("accessToken");
        String refreshToken = tokens.get("refreshToken");

        return new SignInResponseDto(accessToken, refreshToken, user.getName());
    }

    public SignInResponseDto refreshToken(String data){
        DecodedJWT decodedJWT = JWT.decode(data);

        String subject = decodedJWT.getSubject();
        String tokenType = decodedJWT.getClaim("type").asString();

        if(!"refreshToken".equals(tokenType)) {
            throw new RuntimeException("Invalid or missing token");
        }

        Users user = this.userRepository.findByEmail(subject).orElseThrow(() -> new RuntimeException("Email Or Password Invalid"));

        Map<String, String> tokens = this.tokenService.generateToken(user);

        String accessToken = tokens.get("accessToken");
        String refreshToken = tokens.get("refreshToken");

        return new SignInResponseDto(accessToken, refreshToken, user.getName());
    }

}
