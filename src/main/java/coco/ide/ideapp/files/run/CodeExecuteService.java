package coco.ide.ideapp.files.run;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.tools.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CodeExecuteService {
    public String executeCode(String filePath, String language) {
        try {

            String code = Files.lines(Paths.get(filePath)).collect(Collectors.joining("\n"));

            if ("java".equalsIgnoreCase(language)) {
                String className = extractClassName(code);
                if (className == null) {
                    return "Invalid Java code: Class name not found.";
                }
                return executeJavaProgram(filePath);
            }
            return "Unsupported language";
        } catch (IOException e) {
            return "Error processing the file";
        }
    }

    private String extractClassName(String javaCode) {
        Pattern pattern = Pattern.compile("public\\s+class\\s+([\\w]+)\\s*");
        Matcher matcher = pattern.matcher(javaCode);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }


    private String executeJavaProgram(String filePath) {
        // 시스템에서 Java 컴파일러를 가져옴. JDK가 설치되어 있지 않으면 null을 반환.
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            return "JavaCompiler not available, ensure JDK is installed and not just JRE.";
        }

        // 컴파일 과정 중 발생하는 모든 진단 정보를 수집하는 DiagnosticCollector 객체 생성.
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null)) {

            // 파일 매니저를 사용하여 컴파일할 파일을 JavaFileObject로 변환.
            Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromStrings(Arrays.asList(filePath));

            // 컴파일러 작업을 설정. 출력은 표준 출력 스트림으로, 진단 정보는 diagnostics로.
            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits);

            // 컴파일 작업 실행. 성공적으로 컴파일되면 true를 반환.
            boolean success = task.call();
            if(!success) {
                StringBuilder errors = new StringBuilder();

                // 컴파일 에러가 있다면 에러 정보를 StringBuilder에 추가.
                for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                    errors.append(diagnostic.toString()).append("\n");
                }
                return "Compilation failed:\n" + errors.toString();
            }
        } catch (IOException e) {
            return "File I/O error: " + e.getMessage();
        }

        // 컴파일이 성공적으로 완료되면, 컴파일된 프로그램을 실행.
        return runJavaProgram(filePath);
    }

    private String runJavaProgram(String filePath) {
        String className = new java.io.File(filePath).getName().replace(".java", "");
        try {
            // 클래스 파일 경로
            String classFilePath = new java.io.File(filePath).getParent() + "/" + className + ".class";

            // Runtime API를 사용하여 java 명령어로 클래스 파일 실행
            Process process = Runtime.getRuntime().exec("java -cp " + new java.io.File(filePath).getParent() + " " + className);
            process.waitFor(); // 프로세스가 종료될 때까지 대기

            StringBuilder output = new StringBuilder();
            // 프로세스의 입력 스트림(표준 출력)에서 실행 결과를 읽어옴
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            // 실행 중 발생한 에러를 처리
            if (process.exitValue() != 0) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append("\n");
                    }
                }
                return "Runtime error:\n" + output.toString();
            }

            // 프로세스 실행 결과 반환
            return output.toString();

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Error executing Java program: " + e.getMessage();
        } finally {
            // 실행이 완료된 후 클래스 파일 삭제
            try {
                Files.deleteIfExists(Paths.get(new java.io.File(filePath).getParent() + "/" + className + ".class"));
            } catch (IOException e) {
                log.error("Failed to delete class file: " + e.getMessage());
            }
        }
    }


}
