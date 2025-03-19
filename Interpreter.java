import java.io.*;
import java.util.*;
import java.util.regex.*;

public class Interpreter {
    private static final Map<String, Integer> variables = new HashMap<>();
    
    public static void main(String[] args) {
        String filename = "ABC.txt";
        ensureFileExists(filename);
        
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append(" ");
            }
            
            List<String> expressions = splitExpressions(content.toString());
            parseAndExecute(expressions);
        } catch (IOException e) {
            System.out.println("Gabim gjatë leximit të skedarit: " + e.getMessage());
        }
    }
    
    private static void ensureFileExists(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
                writer.write("Lexo a;\n");
                writer.write("a = a + 5;\n");
                writer.write("b = 0;\n");
                writer.write("c = a + b;\n");
                writer.write("Afisho a;\n");
                writer.write("b = c / a;\n");
                writer.write("d = c - 1;\n");
                writer.write("Afisho d;\n");
                System.out.println("Skedari u krijua me sukses: " + filename);
            } catch (IOException e) {
                System.out.println("Gabim gjatë krijimit të skedarit: " + e.getMessage());
            }
        }
    }
    
    private static List<String> splitExpressions(String input) {
        return Arrays.asList(input.split(";"));
    }
    
    private static void parseAndExecute(List<String> expressions) {
        for (String expr : expressions) {
            execute(expr.trim());
        }
    }
    
    private static void execute(String line) {
        if (line.isEmpty()) return;
        
        if (line.startsWith("Lexo")) {
            String varName = line.split(" ")[1].trim();
            variables.put(varName, 0);
        } else if (line.startsWith("Afisho")) {
            String varName = line.split(" ")[1].trim();
            System.out.println(variables.getOrDefault(varName, 0));
        } else if (line.contains("=")) {
            String[] parts = line.split("=");
            String varName = parts[0].trim();
            String expr = parts[1].trim();
            int result = evaluateExpression(expr);
            variables.put(varName, result);
        }
    }
    
    private static int evaluateExpression(String expr) {
        for (String var : variables.keySet()) {
            expr = expr.replace(var, String.valueOf(variables.get(var)));
        }
        return eval(expr);
    }
    
    private static int eval(String expr) {
        try {
            Stack<Integer> values = new Stack<>();
            Stack<Character> ops = new Stack<>();
            
            for (int i = 0; i < expr.length(); i++) {
                char c = expr.charAt(i);
                
                if (Character.isDigit(c)) {
                    StringBuilder sb = new StringBuilder();
                    while (i < expr.length() && Character.isDigit(expr.charAt(i))) {
                        sb.append(expr.charAt(i++));
                    }
                    i--; // Rikthe një hap pas
                    values.push(Integer.parseInt(sb.toString()));
                } else if (c == '+' || c == '-' || c == '*' || c == '/') {
                    while (!ops.isEmpty() && precedence(ops.peek()) >= precedence(c)) {
                        values.push(applyOperation(ops.pop(), values.pop(), values.pop()));
                    }
                    ops.push(c);
                }
            }
            
            while (!ops.isEmpty()) {
                values.push(applyOperation(ops.pop(), values.pop(), values.pop()));
            }
            
            return values.pop();
        } catch (Exception e) {
            System.out.println("Gabim në përllogaritje: " + e.getMessage());
            return 0;
        }
    }
    
    private static int precedence(char op) {
        return switch (op) {
            case '+', '-' -> 1;
            case '*', '/' -> 2;
            default -> -1;
        };
    }
    
    private static int applyOperation(char op, int b, int a) {
        switch (op) {
            case '+' -> { return a + b; }
            case '-' -> { return a - b; }
            case '*' -> { return a * b; }
            case '/' -> {
                if (b == 0) {
                    System.out.println("Gabim: Ndarje me zero!");
                    return 0;
                }
                return a / b;
            }
            default -> { return 0; }
        }
    }
}
