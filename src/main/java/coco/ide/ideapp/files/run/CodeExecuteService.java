package coco.ide.ideapp.files.run;

import coco.ide.ideapp.files.File;
import coco.ide.ideapp.files.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
//TODO : 언어 추가 - 파이썬, JS, C, C++, C#, GO, Rust, Dart, Typescript, Kotlin, Swift, Ruby
public class CodeExecuteService {
    private Process process;
    private BufferedWriter processInputWriter;

    private final FileRepository fileRepository;

    public void runJavaProgram(String filePath, Long fileId, String command, WebSocketSession session) throws IOException {
        String absolutePath = "/Users/haeun/Desktop/goormton/IDE/Coco-BE/filedb/";
        String newFilePath = absolutePath + filePath;

        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 파일"));
        int dotIndex = file.getName().lastIndexOf('.');

        String language = file.getProject().getLanguage();

        List<String> commandDocker = new ArrayList<>();
        if (command.equals("run")) {
            if (language.equals("java")) {
                commandDocker = List.of(
                        "docker", "run", "--rm", "-i",
                        "-v", newFilePath + ":/user_files",
                        "-w", "/user_files",
                        "openjdk:latest",
                        "sh", "-c",
                        "javac " +  file.getName() + " && java " + file.getName().substring(0, dotIndex)
                );
            } else if (language.equals("python")) {
                commandDocker = List.of(
                        "docker", "run", "--rm", "-i",
                        "-v", newFilePath + ":/user_files",
                        "-w", "/user_files",
                        "python:latest",
                        "python", file.getName()
                );
            }

            ProcessBuilder processBuilder = new ProcessBuilder(commandDocker);
            process = processBuilder.start();

            processInputWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

            new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        session.sendMessage(new TextMessage(line));
                    }
                    session.sendMessage(new TextMessage("InputStreamClosed"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

            new Thread(() -> {
                try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = errorReader.readLine()) != null) {
                        session.sendMessage(new TextMessage("ERROR: " + line));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    public void handleUserInput(String input) {
        if (processInputWriter != null) {
            try {
                processInputWriter.write(input);
                processInputWriter.newLine();
                processInputWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
