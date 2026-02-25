package air.hyklobby2;

import air.hyklobby2.commands.VipCommand;
import air.hyklobby2.commands.SpawnCommand;
import air.hyklobby2.jumpnrun.JumpnRun;
import air.hyklobby2.jumpnrun.Leaderboard;
import air.hyklobby2.ui.JumpnRunUI;
import air.hyklobby2.listeners.InventoryListener;
import air.hyklobby2.listeners.PlayerJoinListener;
import air.hyklobby2.listeners.PlayerPickupEvent;
import air.hyklobby2.listeners.RightClickListener;
import air.hyklobby2.ui.Navigator;
import air.hyklobby2.ui.Settings;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.TextDisplay;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

import static air.hyklobby2.jumpnrun.Leaderboard.loc;

public final class Hyklobby2 extends JavaPlugin {
    public static JavaPlugin plugin;

    @Override
    public void onEnable() {
        plugin = this;
        this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        this.getServer().getPluginManager().registerEvents(new RightClickListener(), this);
        this.getServer().getPluginManager().registerEvents(new InventoryListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerPickupEvent(), this);
        Objects.requireNonNull(getCommand("spawn")).setExecutor(new SpawnCommand());
        HotbarTools.init(this);
        Navigator.init(this);
        Settings.init(this);
        JumpnRunUI.init(this);
        this.getLogger().info("HykLobby initialized");

        for (World world : Bukkit.getWorlds()) {
            for (org.bukkit.entity.Entity entity : world.getEntities()) {
                if (entity instanceof TextDisplay display) if (display.getLocation().distance(loc) < 10) display.remove();
            }
        }

        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, JumpnRun::run, 0L, 1L);
    }

    @Override
    public void onDisable() {
        Leaderboard.clearLeaderbaords();
        this.getLogger().info("HykLobby disabled");
    }
}