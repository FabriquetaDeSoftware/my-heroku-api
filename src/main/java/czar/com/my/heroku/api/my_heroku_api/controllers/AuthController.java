package czar.com.my.heroku.api.my_heroku_api.controllers;

import czar.com.my.heroku.api.my_heroku_api.dto.request.SignInRequestDto;
import czar.com.my.heroku.api.my_heroku_api.dto.request.SignUpRequestDto;
import czar.com.my.heroku.api.my_heroku_api.dto.response.SignInResponseDto;
import czar.com.my.heroku.api.my_heroku_api.dto.response.SignUpResponseDto;
import czar.com.my.heroku.api.my_heroku_api.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/sign-up")
    public ResponseEntity<SignUpResponseDto> signUp(@RequestBody SignUpRequestDto body) {
        SignUpResponseDto response = authService.signUp(body);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<SignInResponseDto> signIn(@RequestBody SignInRequestDto body) {
        SignInResponseDto response = authService.signIn(body);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token/{refreshToken}")
    public ResponseEntity<SignInResponseDto> refreshToken(@PathVariable("refreshToken") String param) {
        SignInResponseDto response = authService.refreshToken(param);

        return ResponseEntity.ok(response);
    }

    @GetMapping("test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Success");
    }

}
