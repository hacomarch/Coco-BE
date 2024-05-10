package coco.ide.ideapp.files.run;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.tools.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static javax.tools.JavaCompiler.*;

@Slf4j
@Service
public class CodeExecuteService {
    public String executeCode(String filePath, String language, String[] inputs) {
        try {
            String code = Files.lines(Paths.get(filePath)).collect(Collectors.joining("\n"));

            if (inputs == null || inputs.length == 0) {
                return "입력 인자가 없습니다. 필요한 입력 값을 제공해 주세요.";
            }

            List<Object> typeInputs;
            try {
                typeInputs = checkIfInputIsRequired(code, inputs);
            } catch (IllegalArgumentException e) {
                return e.getMessage();
            }

            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

            StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

            Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromStrings(List.of(filePath));

            CompilationTask task = compiler.getTask(null, fileManager, null, null, null, compilationUnits);

            if (task.call()) {
                System.out.println("Compilation successful");
            } else {
                System.out.println("Compilation failed");
            }

            fileManager.close();

            return runJavaProgram(filePath, typeInputs);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String runJavaProgram(String filePath, List<Object> inputs) {
        String className = new File(filePath).getName().replace(".java", "");
        String inputFilePath = Paths.get(new File(filePath).getParent(), className + ".input").toString();
        String classFilePath = Paths.get(new File(filePath).getParent(), className + ".class").toString();

        try {
            writeInputToFile(inputs, inputFilePath);

            ProcessBuilder builder = new ProcessBuilder("java", "-cp", new File(filePath).getParent(), className);
            builder.redirectInput(new File(inputFilePath));
            builder.redirectErrorStream(true);

            Process process = builder.start();

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            process.waitFor();

            deleteFile(inputFilePath);
            deleteFile(classFilePath);

            return output.toString();
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Error executing Java program: " + e.getMessage();
        } finally {
            deleteFile(inputFilePath);
            deleteFile(classFilePath);
        }
    }

    private List<Object> checkIfInputIsRequired(String code, String[] input) {
        InputUsageVisitor visitor = new InputUsageVisitor();
        CompilationUnit cu = StaticJavaParser.parse(code);
        cu.accept(visitor, null);

        if (visitor.isInputRequired()) {
            return validateAndConvertInputs(visitor, input);
        }
        return null;
    }

    private List<Object> validateAndConvertInputs(InputUsageVisitor visitor, String[] inputs) {
        if (visitor.getInputSequence().size() != inputs.length) {
            throw new IllegalArgumentException("입력 개수가 일치하지 않습니다.");
        }

        List<Object> typedInputs = new ArrayList<>();
        try {
            for (int i = 0; i < inputs.length; i++) {
                String expectedType = visitor.getInputSequence().get(i);
                String inputValue = inputs[i];
                Object convertedValue = convertInput(inputValue, expectedType);

                if (convertedValue == null) {
                    throw new IllegalArgumentException("유효하지 않은 입력 타입입니다 : " + expectedType);
                }
                typedInputs.add(convertedValue);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("입력 형식 오류 : " + e.getMessage());
        }
        return typedInputs;
    }

    private Object convertInput(String input, String type) {
        try {
            return switch (type) {
                case "nextInt" -> Integer.parseInt(input);
                case "nextDouble" -> Double.parseDouble(input);
                case "nextFloat" -> Float.parseFloat(input);
                case "nextLong" -> Long.parseLong(input);
                case "nextShort" -> Short.parseShort(input);
                case "nextByte" -> Byte.parseByte(input);
                case "nextBoolean" -> Boolean.parseBoolean(input);
                case "next", "nextLine" -> input;
                default -> null;
            };
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private void writeInputToFile(List<Object> inputs, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Object input : inputs) {
                writer.write(input.toString());
                writer.newLine();
                writer.flush();
            }
        }
    }

    private void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            boolean deleted = file.delete();
            if (!deleted) {
                log.error("Failed to delete file: {}", filePath);
            } else {
                log.info("File deleted successfully: {}", filePath);
            }
        }
    }
}
