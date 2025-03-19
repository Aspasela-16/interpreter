import java.io.*;
import java.util.*;
import java.util.regex.*;

public class Interpreter {
    private static final Map<String, Double> variables = new HashMap<>();
    private static final String filename = "ABC.txt";
    private static final Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        ensureFileExists(filename);
        getUserInputAndSaveToFile();
        processFile();
        displayFinalVariables();
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
            if (!variables.containsKey(varName)) {  // Kontrollo që variabla të mos jetë inicializuar nga një shprehje
                System.out.print("Vendos vlerën për " + varName + ": ");
                while (!scanner.hasNextDouble()) {
                    System.out.print("Ju lutem vendosni një numër të vlefshëm për " + varName + ": ");
                    scanner.next();
                }
                double value = scanner.nextDouble();
                scanner.nextLine(); // Konsumon linjën e mbetur
                variables.put(varName, value);
            }
        }
         else if (line.startsWith("Afisho")) {
            String varName = line.split(" ")[1].trim();
            if (!variables.containsKey(varName)) {
                System.out.println("Gabim: Variabli " + varName + " nuk është i definuar.");
            } else {
                System.out.println(varName + " = " + variables.get(varName));
            }
        } else if (line.contains("=")) {
            String[] parts = line.split("\\s*=\\s*"); // Përdor regex për të shmangur hapësirat e panevojshme
            String varName = parts[0].trim();
            String expr = parts[1].trim();

            System.out.println("Shprehja e përpunuar: " + expr); // Debugging output

            double result = evaluateExpression(expr);
            variables.put(varName, result);
        }
    }
    
    private static double evaluateExpression(String expr) {
        for (String var : variables.keySet()) {
            expr = expr.replace(var, String.valueOf(variables.get(var)));
        }
        return eval(expr);
    }
    
    private static double eval(String expr) {
        try {
            List<String> tokens = new ArrayList<>();
            Matcher matcher = Pattern.compile("\\d+(\\.\\d+)?|[+\\-*/]").matcher(expr);
            while (matcher.find()) {
                tokens.add(matcher.group());
            }

            Stack<Double> values = new Stack<>();
            Stack<Character> ops = new Stack<>();

            for (String token : tokens) {
                if (token.matches("\\d+(\\.\\d+)?")) {
                    values.push(Double.parseDouble(token));
                } else if (token.matches("[+\\-*/]")) {
                    while (!ops.isEmpty() && precedence(ops.peek()) >= precedence(token.charAt(0))) {
                        values.push(applyOperation(ops.pop(), values.pop(), values.pop()));
                    }
                    ops.push(token.charAt(0));
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
    
    private static double applyOperation(char op, double b, double a) {
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
    
    private static void displayFinalVariables() {
        System.out.println("\nVariablat përfundimtare:");
        for (Map.Entry<String, Double> entry : variables.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
    }
}
