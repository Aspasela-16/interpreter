import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class MathOperations {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        double a, b, result = 0;
        String operation;

        // Leximi i vlerave
        System.out.print("Shkruani vleren per a: ");
        a = scanner.nextDouble();
        
        System.out.print("Shkruani vleren per b: ");
        b = scanner.nextDouble();

        // Zgjedhja e operacionit
        System.out.print("Zgjidh operacionin (+, -, *, /): ");
        operation = scanner.next();

        // Kryerja e operacionit
        switch (operation) {
            case "+":
                result = a + b;
                break;
            case "-":
                result = a - b;
                break;
            case "*":
                result = a * b;
                break;
            case "/":
                if (b != 0) {
                    result = a / b;
                } else {
                    System.out.println("Gabim: Nuk mund të pjestoni me zero!");
                    scanner.close();
                    return;
                }
                break;
            default:
                System.out.println("Operacion i pavlefshem!");
                scanner.close();
                return;
        }

        // Shfaqja e rezultatit
        System.out.println("Rezultati: " + result);

        // Ruajtja e rezultatit në një skedar
        try (FileWriter writer = new FileWriter("ABC.txt")) {
            writer.write("Vlera a: " + a + "\n");
            writer.write("Vlera b: " + b + "\n");
            writer.write("Operacioni: " + operation + "\n");
            writer.write("Rezultati: " + result + "\n");
            System.out.println("Te dhenat u ruajten ne skedarin ABC.txt.");
        } catch (IOException e) {
            System.out.println("Gabim gjate ruajtjes se skedarit!");
            e.printStackTrace();
        }

        scanner.close();
    }
}
