package czar.com.my.heroku.api.my_heroku_api.controllers;

import czar.com.my.heroku.api.my_heroku_api.dto.response.ListAllContainersResponseDto;
import czar.com.my.heroku.api.my_heroku_api.dto.response.ListImagesResponseDto;
import czar.com.my.heroku.api.my_heroku_api.dto.response.ListRunningContainersResponseDto;
import czar.com.my.heroku.api.my_heroku_api.services.ContainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/container")
@RequiredArgsConstructor
public class ContainerController {
    @Autowired
    private ContainerService containerService;

    @GetMapping("/list-running-containers")
    public ResponseEntity<List<ListRunningContainersResponseDto>> listRunningContainers() {
        List<ListRunningContainersResponseDto> response = this.containerService.listRunningContainers();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/list-all-containers")
    public ResponseEntity<List<ListAllContainersResponseDto>> listAllContainers() {
        List<ListAllContainersResponseDto> response = this.containerService.listAllContainers();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/start-container/{containerId}")
    public ResponseEntity<Boolean> startContainer(@PathVariable("containerId") String containerId) {
        Boolean response = this.containerService.startContainer(containerId);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/pause-container/{containerId}")
    public ResponseEntity<Boolean> pauseContainer(@PathVariable("containerId") String containerId) {
        Boolean response = this.containerService.stopContainer(containerId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-logs-container/{containerId}")
    public ResponseEntity<String> getLogsContainer(@PathVariable("containerId") String containerId) {
        String response = this.containerService.getContainerLogs(containerId);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete-container/{containerId}")
    public ResponseEntity<Boolean> deleteContainer(@PathVariable("containerId") String containerId) {
        Boolean response = this.containerService.deleteContainer(containerId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/list-all-images")
    public ResponseEntity<List<ListImagesResponseDto>> listAllImages() {
        List<ListImagesResponseDto> response = this.containerService.listAllImages();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete-image/{imageId}")
    public ResponseEntity<Boolean> deleteImage(@PathVariable("imageId") String imageId) {
        Boolean response = this.containerService.deleteImage(imageId);

        return ResponseEntity.ok(response);
    }

}
