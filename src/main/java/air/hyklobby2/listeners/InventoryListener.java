package air.hyklobby2.listeners;

import air.hyklobby2.HotbarTools;
import air.hyklobby2.Hyklobby2;
import air.hyklobby2.PlayerVisibility;
import air.hyklobby2.Storage;
import air.hyklobby2.jumpnrun.Levels;
import air.hyklobby2.ui.JumpnRunUI;
import air.hyklobby2.ui.Navigator;
import air.hyklobby2.ui.Settings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        ItemStack item = event.getCurrentItem();
        if (item == null || item.getItemMeta() == null) return;

        String name = item.getItemMeta().getDisplayName();
        World world = player.getWorld();

        if (item.getItemMeta().getPersistentDataContainer().has(Navigator.getAirItem())) {
            event.setCancelled(true);

            if (name.contains("sᴜʀᴠɪᴠᴀʟ")) {
                player.teleport(new Location(world, -37.5, 59.00, -37.5,135,-20));
            } else if (name.contains("ᴍɪɴɪɢᴀᴍᴇs")) {
                player.teleport(new Location(world, 38.5, 59.00, -37.5, -135, -20));
            } else if (name.contains("ᴇᴠᴇɴᴛs")) {
                player.teleport(new Location(world,-38.5, 59.00, 39.5, 45, -20));
            } else if (name.contains("ғғᴀ")) {
                player.teleport(new Location(world,38.5, 59.00, 38.5, -45, -20));
            } else if (name.contains("sᴘᴀᴡɴ")) {
                player.teleport(new Location(player.getWorld(), 0.5, 64, 0.5, 90, 0));
            } else return;

            player.closeInventory();

        } else if (item.getItemMeta().getPersistentDataContainer().has(Settings.getAirItem())) {
            event.setCancelled(true);

            if (name.contains("ᴊᴜᴍᴘ'ɴ ʀᴜɴ sᴇᴛᴛɪɴɢs")) {
                JumpnRunUI.openGUI(player);
            } else if (name.contains("ᴘʟᴀʏᴇʀꜱ")) {
                Storage.setHidePlayers(player,!Storage.arePlayersHidden(player));
                item.setType(Storage.arePlayersHidden(player) ? Material.GRAY_DYE : Material.GREEN_DYE);
                if (Storage.arePlayersHidden(player)) {
                    PlayerVisibility.hidePlayers(Hyklobby2.plugin,player);
                } else {
                    PlayerVisibility.showPlayers(Hyklobby2.plugin,player);
                }
                ItemMeta meta = item.getItemMeta();
                meta.displayName(Component.text((Storage.arePlayersHidden(player) ? "ꜱʜᴏᴡ" : "ʜɪᴅᴇ") + " ᴘʟᴀʏᴇʀꜱ").color(NamedTextColor.GOLD));
                item.setItemMeta(meta);
            }

        } else if (item.getItemMeta().getPersistentDataContainer().has(JumpnRunUI.getAirItem())) {
            event.setCancelled(true);

            if (Levels.getBlocks().contains(item.getType())) {
                event.getInventory().forEach(invItem -> {
                    if (invItem == null || !invItem.hasItemMeta()) return;

                    ItemMeta m = invItem.getItemMeta();
                    m.setEnchantmentGlintOverride(false);
                    invItem.setItemMeta(m);
                });

                ItemMeta meta = item.getItemMeta();
                meta.setEnchantmentGlintOverride(true);
                item.setItemMeta(meta);
                Storage.setBlock(player,false,item.getType().name());
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getOldCursor().getItemMeta().getPersistentDataContainer().has(HotbarTools.getAirItem())) {
            if (event.getWhoClicked() instanceof Player player && !player.getGameMode().equals(GameMode.CREATIVE)) event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();

        if (!item.hasItemMeta()) return;

        if (item.getItemMeta().getPersistentDataContainer().has(HotbarTools.getAirItem())) event.setCancelled(true);
    }

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent event) {
        if (event.getOffHandItem().getPersistentDataContainer().has(HotbarTools.getAirItem())) {
            Player player = event.getPlayer();
            if (!player.getGameMode().equals(GameMode.CREATIVE)) event.setCancelled(true);
        }
    }
}
