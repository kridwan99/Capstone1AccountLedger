import java.util.Scanner;
import java.time.LocalDate;

public class UserInterface {
    private Team team;
    private Scanner scanner;

    public UserInterface() {
        this.team = new Team("Real Madrid", "Santiago Bernabéu Stadium");
        this.scanner = new Scanner(System.in);
    }

    public void display() {
        while (true) {
            System.out.println("\nReal Madrid Team Management System");
            System.out.println("1 - List All Players");
            System.out.println("2 - Search Players by Name");
            System.out.println("3 - Search Players by Age Range");
            System.out.println("4 - Add a Player");
            System.out.println("5 - Remove a Player");
            System.out.println("99 - Quit");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> team.displayPlayers();
                case 2 -> processSearchByName();
                case 3 -> processSearchByAgeRange();
                case 4 -> processAddPlayer();
                case 5 -> processRemovePlayer();
                case 99 -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void processSearchByName() {
        System.out.print("Enter player name: ");
        String name = scanner.nextLine();
        team.displayPlayers(team.searchByName(name));
    }

    private void processSearchByAgeRange() {
        System.out.print("Enter min age: ");
        int minAge = scanner.nextInt();
        System.out.print("Enter max age: ");
        int maxAge = scanner.nextInt();
        scanner.nextLine();
        team.displayPlayers(team.searchByAgeRange(minAge, maxAge));
    }

    private void processAddPlayer() {
        System.out.print("Enter player ID: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter player name: ");
        String name = scanner.nextLine();

        System.out.print("Enter position: ");
        String position = scanner.nextLine();

        System.out.print("Enter age: ");
        int age = scanner.nextInt();

        System.out.print("Enter goals scored: ");
        int goals = scanner.nextInt();

        System.out.print("Enter assists: ");
        int assists = scanner.nextInt();

        System.out.print("Enter contract expiry date (yyyy-mm-dd): ");
        scanner.nextLine();
        LocalDate contractExpiry = LocalDate.parse(scanner.nextLine());

        team.addPlayer(new Player(id, name, position, age, goals, assists, contractExpiry));
        System.out.println("Player added successfully!");
    }

    private void processRemovePlayer() {
        System.out.print("Enter player ID to remove: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        team.removePlayer(id);
        System.out.println("Player removed successfully!");
    }
}
