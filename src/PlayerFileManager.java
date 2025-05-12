import java.io.*;
import java.util.*;
import java.time.LocalDate;

public class PlayerFileManager {
    private static final String FILE_PATH = "players.csv";

    public static List<Player> loadPlayers() {
        List<Player> players = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 7) {
                    players.add(new Player(
                            Integer.parseInt(parts[0]), parts[1], parts[2],
                            Integer.parseInt(parts[3]), Integer.parseInt(parts[4]),
                            Integer.parseInt(parts[5]), LocalDate.parse(parts[6])
                    ));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return players;
    }

    public static void savePlayers(List<Player> players) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (Player player : players) {
                writer.println(player.toCSV());
            }
        } catch (IOException e) {
            System.err.println("Error writing file: " + e.getMessage());
        }
    }
}
