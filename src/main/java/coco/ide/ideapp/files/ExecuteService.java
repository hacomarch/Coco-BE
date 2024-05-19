package coco.ide.ideapp.files;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor

//Todo: 언어 추가 한가득 하기
public class ExecuteService {

    private final FileRepository fileRepository;

    public String executeCode(String filePath, String language, Long fileId) throws IOException {

        log.info("in executeCode");

        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 파일"));
        int dotIndex = file.getName().lastIndexOf('.');

        String absolutePath = Paths.get(filePath).toAbsolutePath().toString();
        log.info("Path = {}", absolutePath);

        List<String> commandDocker = new ArrayList<>();
        if (language.equalsIgnoreCase("java")) {
            commandDocker = List.of(
                    "sudo", "docker", "run", "--rm", "-i",
                    "-v", absolutePath + ":/user_files",
                    "-w", "/user_files",
                    "openjdk:latest",
                    "sh", "-c",
                    "javac " + file.getName() + " && java " + file.getName().substring(0, dotIndex)
            );
        } else if (language.equalsIgnoreCase("Python")) {
            log.info("language = {}", language);
            commandDocker = List.of(
                    "sudo", "docker", "run", "--rm", "-i",
                    "-v", absolutePath + ":/user_files",
                    "-w", "/user_files",
                    "python:latest",
                    "python", file.getName()
            );
        } else if (language.equalsIgnoreCase("javascript")) {
            log.info("language = {}", language);
            commandDocker = List.of(
                    "sudo", "docker", "run", "--rm", "-i",
                    "-v", absolutePath + ":/user_files",
                    "-w", "/user_files",
                    "node:latest",
                    "node", file.getName()
            );
        } else if (language.equalsIgnoreCase("c")) {
            log.info("language = {}", language);
            commandDocker = List.of(
                    "sudo", "docker", "run", "--rm", "-i",
                    "-v", absolutePath + ":/user_files",
                    "-w", "/user_files",
                    "gcc:latest",
                    "sh", "-c", "gcc " + file.getName() + " -o program && ./program && rm program"
            );
        } else if (language.equalsIgnoreCase("cpp")) {
            log.info("language = {}", language);
            commandDocker = List.of(
                    "sudo", "docker", "run", "--rm", "-i",
                    "-v", absolutePath + ":/user_files",
                    "-w", "/user_files",
                    "gcc:latest",
                    "sh", "-c", "g++ " + file.getName() + " -o program && ./program && rm program"
            );
        } else if (language.equalsIgnoreCase("csharp")) {
            log.info("language = {}", language);
            commandDocker = List.of(
                    "sudo", "docker", "run", "--rm", "-i",
                    "-v", absolutePath + ":/user_files",
                    "-w", "/user_files",
                    "mcr.microsoft.com/dotnet/sdk:latest",
                    "sh", "-c", "dotnet new console -n Program && mv " + file.getName() + " Program/Program.cs && cd Program && dotnet run && cd .. && rm -rf Program"
            );
        }
        else if (language.equalsIgnoreCase("go")) {
            log.info("language = {}", language);
            commandDocker = List.of(
                    "sudo", "docker", "run", "--rm", "-i",
                    "-v", absolutePath + ":/user_files",
                    "-w", "/user_files",
                    "golang:latest",
                    "go", "run", file.getName()
            );
        } else if (language.equalsIgnoreCase("rust")) {
            log.info("language = {}", language);
            commandDocker = List.of(
                    "sudo", "docker", "run", "--rm", "-i",
                    "-v", absolutePath + ":/user_files",
                    "-w", "/user_files",
                    "rust:latest",
                    "rustc", file.getName(), "-o", "program",
                    "&&", "./program"
            );
        } else if (language.equalsIgnoreCase("dart")) {
            log.info("language = {}", language);
            commandDocker = List.of(
                    "sudo", "docker", "run", "--rm", "-i",
                    "-v", absolutePath + ":/user_files",
                    "-w", "/user_files",
                    "dart:latest",
                    "dart", file.getName()
            );
        } else if (language.equalsIgnoreCase("typescript")) {
            log.info("language = {}", language);
            commandDocker = List.of(
                    "sudo", "docker", "run", "--rm", "-i",
                    "-v", absolutePath + ":/user_files",
                    "-w", "/user_files",
                    "node:latest",
                    "npx", "ts-node", file.getName()
            );
        } else if (language.equalsIgnoreCase("kotlin")) {
            log.info("language = {}", language);
            commandDocker = List.of(
                    "sudo", "docker", "run", "--rm", "-i",
                    "-v", absolutePath + ":/user_files",
                    "-w", "/user_files",
                    "openjdk:latest",
                    "kotlinc", file.getName(), "-include-runtime", "-d", "program.jar",
                    "&&", "java", "-jar", "program.jar"
            );
        } else if (language.equalsIgnoreCase("swift")) {
            log.info("language = {}", language);
            commandDocker = List.of(
                    "sudo", "docker", "run", "--rm", "-i",
                    "-v", absolutePath + ":/user_files",
                    "-w", "/user_files",
                    "swift:latest",
                    "swift", file.getName()
            );
        } else if (language.equalsIgnoreCase("ruby")) {
            log.info("language = {}", language);
            commandDocker = List.of(
                    "sudo", "docker", "run", "--rm", "-i",
                    "-v", absolutePath + ":/user_files",
                    "-w", "/user_files",
                    "ruby:latest",
                    "ruby", file.getName()
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

