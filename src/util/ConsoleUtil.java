package util;

import java.util.Scanner;

public class ConsoleUtil {
    private static Scanner scanner = new Scanner(System.in);

    public static String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public static int readInt(String prompt) {
        while (true) {
            try {
                String s = readLine(prompt);
                return Integer.parseInt(s);
            } catch (Exception e) {
                System.out.println("Invalid number. Try again.");
            }
        }
    }
}
