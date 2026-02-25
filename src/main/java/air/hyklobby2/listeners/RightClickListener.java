package air.hyklobby2.listeners;

import air.hyklobby2.*;
import air.hyklobby2.jumpnrun.JumpnRun;
import air.hyklobby2.ui.Navigator;
import air.hyklobby2.ui.Settings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class RightClickListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction().isRightClick()) {
            Player player = event.getPlayer();
            ItemStack item = event.getItem();
            if (item == null) return;
            String name = item.getItemMeta().getDisplayName();

            if (!item.getItemMeta().getPersistentDataContainer().has(HotbarTools.getAirItem())) return;

            if (name.contains("ᴊᴜᴍᴘ'ɴ ʀᴜɴ")) {
                if (JumpnRun.playingPlayers.contains(player)) {
                    player.sendMessage(Component.text("\uE017 Du bist bereits im Spiel!").color(NamedTextColor.RED));
                    return;
                }
                if (!JumpnRun.startJumpNRun(player)) player.sendMessage("Error starting Parkour");
            } else if (name.contains("ɴᴀᴠɪɢᴀᴛᴏʀ")) {
                Navigator.openGUI(player);
            } else if (name.contains("sᴇᴛᴛɪɴɢs")) {
                Settings.openGUI(player);
            }
        }
    }
}