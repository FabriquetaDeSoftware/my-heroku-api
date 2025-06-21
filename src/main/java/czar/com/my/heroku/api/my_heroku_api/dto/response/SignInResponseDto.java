package czar.com.my.heroku.api.my_heroku_api.dto.response;

public record SignInResponseDto(String accessToken, String refreshToken, String name) {
}
