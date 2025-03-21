import java.io.*;
import java.util.HashMap;
import java.util.Scanner;

public class MathInterpreter {
    private static final String FILE_NAME = "ABC.txt";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        HashMap<String, Double> variables = loadVariablesFromFile();
        String input;

        while (true) {
            System.out.print("Fut te dhenat ");
            input = scanner.nextLine().trim();
            
            if (input.equalsIgnoreCase("exit")) {
                saveVariablesToFile(variables);
                break;
            }
            
            interpretuesKomandash(input, variables);
        }
        scanner.close();
    }

    private static void interpretuesKomandash(String command, HashMap<String, Double> variables) {
        if (command.startsWith("Lexo ")) {
            String varName = command.substring(5).replace(";", "").trim();
            Scanner scanner = new Scanner(System.in);
            System.out.print("Shkruani vleren per " + varName + ": ");
            double value = scanner.nextDouble();
            variables.put(varName, value);
            saveVariablesToFile(variables);
        } else if (command.startsWith("Afisho ")) {
            String varName = command.substring(7).replace(";", "").trim();
            if (variables.containsKey(varName)) {
                System.out.println(varName + " = " + variables.get(varName));
            } else {
                System.out.println("Gabim: Variabla " + varName + " nuk eshte e definuar!");
            }
        } else if (command.contains("=")) {
            String[] parts = command.replace(";", "").split("=");
            String varName = parts[0].trim();
            String expression = parts[1].trim();
            try {
                double result = llogaritShprehjen(expression, variables);
                variables.put(varName, result);
                saveVariablesToFile(variables);
            } catch (Exception e) {
                System.out.println("Gabim ne shprehje: " + expression);
            }
        } else {
            System.out.println("Komande e panjohur!");
        }
    }

    private static double llogaritShprehjen(String expression, HashMap<String, Double> variables) {
        for (String var : variables.keySet()) {
            expression = expression.replace(var, variables.get(var).toString());
        }
        
        try {
            return eval(expression);
        } catch (Exception e) {
            throw new RuntimeException("Gabim ne vleresimin e shprehjes");
        }
    }

    private static double eval(String expression) {
        return new Object() {
            int pos = -1, ch;
            void nextChar() {
                ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
            }
            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }
            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < expression.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }
            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }
            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }
            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();
                double x;
                int startPos = this.pos;
                if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(expression.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }
                return x;
            }
        }.parse();
    }

    private static void saveVariablesToFile(HashMap<String, Double> variables) {
        try (FileWriter writer = new FileWriter(FILE_NAME)) {
            for (String key : variables.keySet()) {
                writer.write(key + " = " + variables.get(key) + "\n");
            }
            System.out.println("Te dhenat u ruajten ne skedarin ABC.txt.");
        } catch (IOException e) {
            System.out.println("Gabim gjate ruajtjes se skedarit!");
            e.printStackTrace();
        }
    }

    private static HashMap<String, Double> loadVariablesFromFile() {
        HashMap<String, Double> variables = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    variables.put(parts[0].trim(), Double.parseDouble(parts[1].trim()));
                }
            }
            System.out.println("Te dhenat u ngarkuan nga skedari ABC.txt.");
        } catch (IOException e) {
            System.out.println("Asnje skedar ABC.txt nuk u gjet, do te krijohet nje i ri.");
        }
        return variables;
    }
}
