package czar.com.my.heroku.api.my_heroku_api.services;

import czar.com.my.heroku.api.my_heroku_api.dto.response.ListAllContainersResponseDto;
import czar.com.my.heroku.api.my_heroku_api.dto.response.ListImagesResponseDto;
import czar.com.my.heroku.api.my_heroku_api.dto.response.ListRunningContainersResponseDto;
import czar.com.my.heroku.api.my_heroku_api.plataform.OperatingSystem;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Component
public class ContainerService {
    private static String DOCKER_PATH = "";
    private static final String LIST_RUNNING_CONTAINERS = "stats --no-stream --format \"{{.ID}}\\t{{.Name}}\\t{{.CPUPerc}}\\t{{.MemUsage}}\\t{{.MemPerc}}\"";
    private static final String LIST_ALL_CONTAINERS = "ps -a --format \"{{.ID}}\\t{{.Names}}\\t{{.Status}}\\t{{.Image}}\\t{{.Ports}}\"";
    private static final String LIST_ALL_IMAGES = "image ls --format \"{{.Repository}}\\t{{.Tag}}\\t{{.ID}}\\t{{.Size}}\"";
    private static final String STOP_CONTAINER = "stop";
    private static final String START_CONTAINER = "start";
    private static final String DELETE_IMAGE = "image rm";
    private static final String REMOVE_CONTAINER = "rm";

    public ContainerService() {
        DOCKER_PATH = this.locateDockerBinary();
    }

    public List<ListRunningContainersResponseDto> listRunningContainers() {
        String dockerOutput = runDockerCommand(DOCKER_PATH + " " + LIST_RUNNING_CONTAINERS);
        String[] lines = dockerOutput.split("\n");
        List<ListRunningContainersResponseDto> containerInfo = new ArrayList<>();
        for(String result : lines) {
            if(!result.isBlank()) {
                String[] resultFields = result.split("\t");
                containerInfo.add(new ListRunningContainersResponseDto(resultFields[0], resultFields[1], resultFields[2], resultFields[3],
                        resultFields[4]));
            }
        }
        return containerInfo;
    }

    public List<ListAllContainersResponseDto> listAllContainers() {
        String dockerOutput = runDockerCommand(DOCKER_PATH + " " + LIST_ALL_CONTAINERS);
        String[] lines = dockerOutput.split("\n");
        List<ListAllContainersResponseDto> containerInfo = new ArrayList<>();
        for(String result : lines) {
            if(!result.isBlank()) {
                String[] resultFields = result.split("\t");
                String ports = resultFields.length > 4 && !resultFields[4].isBlank() ? resultFields[4] : "empty";
                containerInfo.add(new ListAllContainersResponseDto(resultFields[0], resultFields[1], resultFields[2], resultFields[3], ports));
            }
        }
        return containerInfo;
    }

    public Boolean stopContainer(String containerId) {
        runDockerCommand(DOCKER_PATH + " " + STOP_CONTAINER + " " + containerId);

        return Boolean.TRUE;
    }

    public Boolean startContainer(String containerId) {
        runDockerCommand(DOCKER_PATH + " " + START_CONTAINER + " " + containerId);

        return Boolean.TRUE;
    }

    public Boolean deleteContainer(String containerId) {
        Boolean isStopped = this.stopContainer(containerId);

        if(isStopped) {
            runDockerCommand(DOCKER_PATH + " " + REMOVE_CONTAINER + " " + containerId);

            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    public List<ListImagesResponseDto> listAllImages() {
        String dockerOutput = runDockerCommand(DOCKER_PATH + " " + LIST_ALL_IMAGES);
        String[] lines = dockerOutput.split("\n");
        List<ListImagesResponseDto> containerInfo = new ArrayList<>();
        for(String result : lines) {
            if(!result.isBlank()) {
                String[] resultFields = result.split("\t");
                containerInfo.add(new ListImagesResponseDto(resultFields[0], resultFields[1], resultFields[2], resultFields[3]));
            }
        }
        return containerInfo;
    }

    public Boolean deleteImage(String imageId) {
        runDockerCommand(DOCKER_PATH + " " + DELETE_IMAGE + " " + imageId);

        return Boolean.TRUE;
    }

    private String runDockerCommand(String command) {
        ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
        processBuilder.redirectErrorStream(true);
        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            process.waitFor();

            return output.toString();
        } catch(Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
            throw new RuntimeException("Error to execute " + command);
        }
    }

    private String locateDockerBinary() {
        List<String> commonPaths = OperatingSystem.isLinux() || OperatingSystem.isMac() ?
                List.of(
                        "/opt/homebrew/bin/docker",
                        "/usr/bin/docker",
                        "/usr/local/bin/docker",
                        "/bin/docker"
                ) :
                List.of(
                        "C:\\Program Files\\Docker\\Docker\\resources\\bin\\docker.exe",
                        "C:\\ProgramData\\DockerDesktop\\version-bin\\docker.exe"
                );

        for(String path : commonPaths) {
            if(Files.isExecutable(Paths.get(path))) {
                return path;
            }
        }
        throw new RuntimeException("Docker command not found");
    }
}
