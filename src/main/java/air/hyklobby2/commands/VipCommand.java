package air.hyklobby2.commands;

import air.hyklobby2.Storage;
import air.hyklobby2.ui.Settings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class VipCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        Player player = (Player) sender;

        if (!player.isOp()) {
            player.sendMessage(Component.text("Dafür hast du keine Berechtigung!").color(NamedTextColor.RED));
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(Component.text("Usage: /vip <user> <time>").color(NamedTextColor.RED));
            return true;
        }

        Player receiver = Bukkit.getPlayer(args[0]);
        String time = args[1];

        if (receiver != null && time != null) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"/lpv user " + receiver.getName() + " parent set vip");
            Storage.addVip(player.getUniqueId(),time);
            player.sendMessage(Component.text("Du hast " + receiver.getName() + " für " + time + " VIP gegeben.").color(NamedTextColor.WHITE));
            System.out.println("[HYK] " + player.getName() + " made " + receiver.getName() + " vip for " + time);
        }

        return true;
    }
}