import java.io.*;
import java.util.*;
import java.util.regex.*;

public class interpreter {
    private static final Map<String, Integer> variables = new HashMap<>();
    private static final String filename = "ABC.txt";
    private static final Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        ensureFileExists(filename);
        getUserInputAndSaveToFile();
        processFile();
    }
    
    private static void ensureFileExists(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
                System.out.println("Skedari u krijua me sukses: " + filename);
            } catch (IOException e) {
                System.out.println("Gabim gjatë krijimit të skedarit: " + e.getMessage());
            }
        }
    }
    
    private static void getUserInputAndSaveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            
            System.out.println("Shkruani vlerat fillestare për variablat (shkruaj 'exit' për të ndaluar):");
            while (true) {
                System.out.print("Shprehja: ");
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("exit")) break;
                writer.write(input + "\n");
            }
            System.out.println("Të dhënat u ruajtën në skedar.");
        } catch (IOException e) {
            System.out.println("Gabim gjatë ruajtjes së të dhënave: " + e.getMessage());
        }
    }
    
    private static void processFile() {
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
            System.out.print("Vendos vlerën për " + varName + ": ");
            while (!scanner.hasNextInt()) {
                System.out.print("Ju lutem vendosni një numër të vlefshëm për " + varName + ": ");
                scanner.next();
            }
            int value = scanner.nextInt();
            scanner.nextLine(); // Konsumon linjën e mbetur
            variables.put(varName, value);
        } else if (line.startsWith("Afisho")) {
            String varName = line.split(" ")[1].trim();
            System.out.println(varName + " = " + variables.getOrDefault(varName, 0));
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
