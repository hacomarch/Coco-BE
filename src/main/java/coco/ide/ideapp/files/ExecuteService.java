package coco.ide.ideapp.files;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor

//Todo: 언어 추가 한가득 하기
public class ExecuteService {

    private final FileRepository fileRepository;

    public String executeCode(String filePath, String language, Long fileId) throws IOException {

        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 파일"));
        int dotIndex = file.getName().lastIndexOf('.');

        List<String> commandDocker = new ArrayList<>();
        if (language.equals("java")) {
            commandDocker = List.of(
                    "docker", "run", "--rm", "-i",
                    "-v", filePath + ":/user_files",
                    "-w", "/user_files",
                    "openjdk:latest",
                    "sh", "-c",
                    "javac " + file.getName() + " && java " + file.getName().substring(0, dotIndex)
            );
        } else if (language.equals("python")) {
            log.info("language = {}", language);
            commandDocker = List.of(
                    "docker", "run", "--rm", "-i",
                    "-v", filePath + ":/user_files",
                    "-w", "/user_files",
                    "python:latest",
                    "python", file.getName()
            );
        }

        ProcessBuilder processBuilder = new ProcessBuilder(commandDocker);
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
}

