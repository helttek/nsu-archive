package fit.snake_game;

import java.util.*;

public class Main {
    public static int port;

    public static void main(String[] args) {
        System.out.println("Enter port: ");
        Scanner scanner = new Scanner(System.in);
        port = scanner.nextInt();
        MainFX.main(new String[]{String.valueOf(port)});
    }
}
