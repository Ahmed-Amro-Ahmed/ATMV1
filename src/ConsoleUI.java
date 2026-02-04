import java.util.Scanner;

public class ConsoleUI {
    private final Scanner scanner = new Scanner(System.in);

    public void displayMessage(String message) {
        System.out.println(message);
    }

    public String promptString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public int promptInt(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(promptString(prompt));
            } catch (NumberFormatException e) {
                displayMessage("Invalid input. Please enter a number.");
            }
        }
    }
}
