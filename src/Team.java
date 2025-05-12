import java.util.ArrayList;
import java.util.List;

public class Team {
    private String name;
    private String stadium;
    private List<Player> roster;

    public Team(String name, String stadium) {
        this.name = name;
        this.stadium = stadium;
        this.roster = PlayerFileManager.loadPlayers(); // Load players from file
    }

    public void addPlayer(Player player) {
        roster.add(player);
        PlayerFileManager.savePlayers(roster); // Save to file
    }

    public void removePlayer(int id) {
        roster.removeIf(player -> player.getId() == id);
        PlayerFileManager.savePlayers(roster); // Save to file
    }

    public List<Player> searchByName(String name) {
        List<Player> result = new ArrayList<>();
        for (Player player : roster) {
            if (player.getName().equalsIgnoreCase(name)) {
                result.add(player);
            }
        }
        return result;
    }

    public List<Player> searchByAgeRange(int minAge, int maxAge) {
        List<Player> result = new ArrayList<>();
        for (Player player : roster) {
            if (player.getAge() >= minAge && player.getAge() <= maxAge) {
                result.add(player);
            }
        }
        return result;
    }

    public void displayPlayers() {
        if (roster.isEmpty()) {
            System.out.println("No players in the team.");
        } else {
            roster.forEach(System.out::println);
        }
    }

    public void displayPlayers(List<Player> players) {
        if (players.isEmpty()) {
            System.out.println("No players found.");
        } else {
            players.forEach(System.out::println);
        }
    }
}
