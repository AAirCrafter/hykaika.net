package air.hyklobby2.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SpawnCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        Player player = (Player) sender;

        Location spawn = new Location(player.getWorld(), 0.5, 64, 0.5, 90, 0);
        player.teleport(spawn);
        player.sendMessage("§aDu wurdest zum Spawn teleportiert.");
        return true;
    }
}