package air.hyklobby2.jumpnrun;

import air.hyklobby2.Storage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static air.hyklobby2.Hyklobby2.plugin;
import static air.hyklobby2.jumpnrun.JumpnRun.*;

public class Leaderboard {
    private static final Map<UUID, TextDisplay> leaderboardDisplays = new HashMap<>();
    public static Location loc = new Location(Bukkit.getWorld("world"),4.5, 61.2, -7.99,0,0);

    public static void createLeaderboard(Player player) {
        removeLeaderboard(player);

        loc.getWorld().getNearbyEntities(loc.clone().add(0, 2, 0), 1, 1, 1).forEach(entity -> {
            if (entity instanceof TextDisplay) {
                TextDisplay td = (TextDisplay) entity;
                if (!leaderboardDisplays.containsValue(td)) entity.remove();
            }
        });

        TextDisplay display = player.getWorld().spawn(loc.clone().add(0, 2, 0),TextDisplay.class);

        display.setAlignment(TextDisplay.TextAlignment.CENTER);
        display.setBillboard(Display.Billboard.FIXED);
        display.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
        display.setTextOpacity((byte) -1);
        display.setShadowed(true);
        display.setSeeThrough(false);
        display.setDefaultBackground(false);
        display.setInterpolationDuration(0);
        display.setInterpolationDelay(-1);
        display.setPersistent(false);
        display.setVisibleByDefault(false);

        player.showEntity(plugin,display);
        for (Player other : Bukkit.getOnlinePlayers()) {
            if (!other.equals(player)) other.hideEntity(plugin, display);
        }

        leaderboardDisplays.put(player.getUniqueId(), display);
        updateLeaderboard(player);
    }

    public static void updateLeaderboard(Player player) {
        if (!leaderboardDisplays.containsKey(player.getUniqueId())) return;

        TextDisplay display = leaderboardDisplays.get(player.getUniqueId());

        Map<Integer,UUID> topScores = Storage.getTopScores(5);

        Component leaderboardText = Component.text()
                .append(Component.text("LEADERBOARD\n\n")
                        .color(jnrC)
                        .decorate(net.kyori.adventure.text.format.TextDecoration.BOLD))
                .build();

        int rank = 1;

        for (Map.Entry<Integer,UUID> entry : topScores.entrySet()) {
            String playerName = Bukkit.getOfflinePlayer(entry.getValue()).getName();
            if (playerName == null) playerName = "Unknown";

            TextColor color = rank <= 3 ?
                    (rank == 1 ? TextColor.color(0xE7CB77) :
                            rank == 2 ? TextColor.color(0xD9D9D9) :
                                    TextColor.color(0xE3A181)) :
                    NamedTextColor.WHITE;

            leaderboardText = leaderboardText.append(
                    Component.text(String.format("%d. %s: ", rank, playerName)).color(color)
                            .append(Component.text(entry.getKey()).color(sC)
                            .append(Component.text("\n"))
            ));

            rank++;
        }

        leaderboardText = leaderboardText.append(Component.text("..."));

        int playerScore = Storage.getHighScore(player.getUniqueId());
        leaderboardText = leaderboardText.append(
                Component.text("\n-----------").color(jnrC).append(
                    Component.text("\nDein Score: ")
                            .color(NamedTextColor.GRAY)
                            .append(Component.text(playerScore)
                            .color(hsC)
                )
        ));

        display.text(leaderboardText);
    }

    public static void updateAllLeaderboards() {
        for (UUID playerId : leaderboardDisplays.keySet()) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null && player.isOnline()) updateLeaderboard(player);
        }
    }

    public static void removeLeaderboard(Player player) {
        if (leaderboardDisplays.containsKey(player.getUniqueId())) {
            leaderboardDisplays.get(player.getUniqueId()).remove();
            leaderboardDisplays.remove(player.getUniqueId());
        }
    }

    public static void clearLeaderbaords() {
        for (TextDisplay display : leaderboardDisplays.values()) {
            if (display != null) display.remove();
        }
        leaderboardDisplays.clear();
    }
}