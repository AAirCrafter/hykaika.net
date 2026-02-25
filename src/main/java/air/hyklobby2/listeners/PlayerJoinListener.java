package air.hyklobby2.listeners;

import air.hyklobby2.HotbarTools;
import air.hyklobby2.PlayerVisibility;
import air.hyklobby2.jumpnrun.JumpnRun;
import air.hyklobby2.jumpnrun.Leaderboard;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Location spawn = new Location(player.getWorld(), 0.5d, 64d, 0.5d, 90f, 0f);
        player.teleport(spawn);

        if (player.isOp()) player.getInventory().clear();
        HotbarTools.giveItems(player);
        PlayerVisibility.updateHiddenPlayers();
        Leaderboard.removeLeaderboard(player);
        Leaderboard.createLeaderboard(player);
        Leaderboard.updateAllLeaderboards();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Leaderboard.removeLeaderboard(player);
        JumpnRun.clearOldPath(player);
        if (JumpnRun.playingPlayers.contains(player)) JumpnRun.playingPlayers.remove(player);

        Leaderboard.updateAllLeaderboards();
    }
}