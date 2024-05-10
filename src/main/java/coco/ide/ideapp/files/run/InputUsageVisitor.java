package coco.ide.ideapp.files.run;

import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import lombok.Getter;

import java.util.*;

public class InputUsageVisitor extends VoidVisitorAdapter<Void> {

    private static final Set<String> SCANNER_METHODS = new HashSet<>();

    static {
        SCANNER_METHODS.add("next");
        SCANNER_METHODS.add("nextLine");
        SCANNER_METHODS.add("nextInt");
        SCANNER_METHODS.add("nextDouble");
        SCANNER_METHODS.add("nextFloat");
        SCANNER_METHODS.add("nextLong");
        SCANNER_METHODS.add("nextShort");
        SCANNER_METHODS.add("nextByte");
        SCANNER_METHODS.add("nextBoolean");
    }

    @Getter
    private boolean inputRequired = false;
    @Getter
    int methodCount = 0;
    private final List<String> inputSequence = new ArrayList<>();

    @Override
    public void visit(MethodCallExpr methodCall, Void arg) {
        super.visit(methodCall, arg);
        String methodName = methodCall.getNameAsString();
        if (SCANNER_METHODS.contains(methodName)) {
            inputRequired = true;
            methodCount++;
            inputSequence.add(methodName);
        }
    }

    @Override
    public void visit(VariableDeclarator variable, Void arg) {
        super.visit(variable, arg);
        if (variable.getType().asString().equals("Scanner")) {
            inputRequired = true;
        }
    }

    public List<String> getInputSequence() {
        return Collections.unmodifiableList(inputSequence);
    }
}
