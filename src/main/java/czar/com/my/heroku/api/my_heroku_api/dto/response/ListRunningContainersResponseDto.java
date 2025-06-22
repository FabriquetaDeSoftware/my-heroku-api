package czar.com.my.heroku.api.my_heroku_api.dto.response;

public record ListRunningContainersResponseDto(String id, String name, String cpuUsage, String memoryUsage, String memoryPercent) {
}
