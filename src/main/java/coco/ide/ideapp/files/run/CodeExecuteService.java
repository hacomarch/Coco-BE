package coco.ide.ideapp.files.run;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.tools.*;
import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CodeExecuteService {
    public String executeCode(String filePath, String language, String[] inputs) {
        try {
            String code = Files.lines(Paths.get(filePath)).collect(Collectors.joining("\n"));

            if (inputs == null || inputs.length == 0) {
                return "입력 인자가 잘못되었습니다. 필요한 입력 값을 제공해 주세요.";
            }

            List<Object> typeInputs = checkIfInputIsRequired(code, inputs);
            if (typeInputs == null) {
                return "입력 타입이 잘못되었습니다.";
            }

            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

            Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromStrings(Arrays.asList(filePath));

            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, null, null, compilationUnits);

            boolean result = task.call();
            log.info("result = {}", result);
            if (result) {
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

    private void writeInputToFile(List<Object> inputs, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Object input : inputs) {
                writer.write(input.toString());
                writer.newLine();
                writer.flush();
            }
        }
    }

    private List<Object> checkIfInputIsRequired(String code, String[] input) {
        InputUsageVisitor visitor = new InputUsageVisitor();
        CompilationUnit cu = StaticJavaParser.parse(code);
        cu.accept(visitor, null);

        if (visitor.isInputRequired() && checkInputType(visitor, input)) {
            return injectInputsAndRun(visitor, input);
        }
        return null;
    }

    private boolean checkInputType(InputUsageVisitor visitor, String[] inputs) {
        if (visitor.getInputSequence().size() != inputs.length) {
            return false;
        }

        try {
            for (int i = 0; i < inputs.length; i++) {
                String expectedType = visitor.getInputSequence().get(i);
                String inputValue = inputs[i];

                switch (expectedType) {
                    case "nextInt":
                        Integer.parseInt(inputValue);
                        break;
                    case "nextDouble":
                        Double.parseDouble(inputValue);
                        break;
                    case "nextFloat":
                        Float.parseFloat(inputValue);
                        break;
                    case "nextLong":
                        Long.parseLong(inputValue);
                        break;
                    case "nextShort":
                        Short.parseShort(inputValue);
                        break;
                    case "nextByte":
                        Byte.parseByte(inputValue);
                        break;
                    case "nextBoolean":
                        if (!inputValue.equalsIgnoreCase("true") && !inputValue.equalsIgnoreCase("false")) {
                            return false;
                        }
                        break;
                    case "next":
                    case "nextLine":
                        break;
                    default:
                        return false;
                }
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private List<Object> injectInputsAndRun(InputUsageVisitor visitor, String[] inputs) {
        List<Object> typedInputs = new ArrayList<>();

        for (int i = 0; i < inputs.length; i++) {
            String expectedType = visitor.getInputSequence().get(i);
            String inputValue = inputs[i];

            switch (expectedType) {
                case "nextInt":
                    typedInputs.add(Integer.parseInt(inputValue));
                    break;
                case "nextDouble":
                    typedInputs.add(Double.parseDouble(inputValue));
                    break;
                case "nextFloat":
                    typedInputs.add(Float.parseFloat(inputValue));
                    break;
                case "nextLong":
                    typedInputs.add(Long.parseLong(inputValue));
                    break;
                case "nextShort":
                    typedInputs.add(Short.parseShort(inputValue));
                    break;
                case "nextByte":
                    typedInputs.add(Byte.parseByte(inputValue));
                    break;
                case "nextBoolean":
                    typedInputs.add(Boolean.parseBoolean(inputValue));
                    break;
                case "next":
                case "nextLine":
                    typedInputs.add(inputValue);
                    break;
            }
        }

        return typedInputs;
    }

}
