package air.hyklobby2;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static air.hyklobby2.Hyklobby2.plugin;

public class PlayerVisibility {
    public static final Set<UUID> playersHiddenFor = new HashSet<>();

    public static void hidePlayers(JavaPlugin plugin, Player player) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.equals(player)) continue;
            player.hidePlayer(plugin, p);
            Storage.setHidePlayers(player,true);
        }
    }

    public static void showPlayers(JavaPlugin plugin, Player player) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.equals(player)) continue;
            player.showPlayer(plugin, p);
            Storage.setHidePlayers(player,false);
        }
    }

    public static void updateHiddenPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!playersHiddenFor.contains(player.getUniqueId())) continue;
            if (Storage.getHiddenPlayers().contains(player)) continue;
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.equals(player)) continue;
                player.hidePlayer(plugin, p);
            }
        }
    }
}
