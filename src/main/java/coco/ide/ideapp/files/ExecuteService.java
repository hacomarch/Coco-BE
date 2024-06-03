package coco.ide.ideapp.files;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExecuteService {
    public String executeCode(String filePath, String language, String fileName) throws IOException {
        String absolutePath = Paths.get(filePath).toAbsolutePath().toString();
        List<String> command = new ArrayList<>(
                List.of(
                        "docker", "run", "--rm", "-i",
                        "-v", absolutePath + ":/user_files",
                        "-w", "/user_files"
                )
        );

        String[] dockerCommands = getDockerCommands(language.toLowerCase(), fileName);
        command.addAll(Arrays.asList(dockerCommands));

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();

        StringBuilder output = new StringBuilder();
        StringBuilder errorOutput = new StringBuilder();

        try (BufferedReader stdOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
             BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {

            String line;
            while ((line = stdOutput.readLine()) != null) {
                output.append(line).append("\n");
            }
            while ((line = stdError.readLine()) != null) {
                errorOutput.append(line).append("\n");
            }
        }

        try {
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return output.toString();
            } else {
                return "Error:\n" + errorOutput;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Execution interrupted", e);
        }
    }

    private String[] getDockerCommands(String language, String fileName) {
        return switch (language) {
            case "java" ->
                    new String[]{"openjdk:latest", "sh", "-c", "javac " + fileName + " && java " + fileName.substring(0, fileName.lastIndexOf('.'))};
            case "python" -> new String[]{"python:latest", "python", fileName};
            case "javascript" -> new String[]{"node:latest", "node", fileName};
            case "c" ->
                    new String[]{"gcc:latest", "sh", "-c", "gcc " + fileName + " -o program && ./program && rm program"};
            case "cpp" ->
                    new String[]{"gcc:latest", "sh", "-c", "g++ " + fileName + " -o program && ./program && rm program"};
            case "typescript" -> new String[]{"node:latest", "npx", "ts-node", fileName};
            default -> null;
        };
    }
}

