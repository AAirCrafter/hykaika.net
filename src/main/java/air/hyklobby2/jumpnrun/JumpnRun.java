package air.hyklobby2.jumpnrun;

import air.hyklobby2.Storage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Player;

import java.util.*;

import static air.hyklobby2.Hyklobby2.plugin;

public class JumpnRun {
    private static final Map<Player,Location> oldBlock = new HashMap<>();
    private static final Map<Player,Location> currentBlock = new HashMap<>();
    private static final Map<Player,Location> newStayBlock = new HashMap<>();
    public static final Set<Player> playingPlayers = new HashSet<>();
    static List<Integer> possibleYValues = List.of(220,221,222,223,224);
    private static final Map<Player,Integer> playerScores = new HashMap<>();
    private static final Map<Player, BlockDisplay> glowBlock = new HashMap<>();
    public static TextColor jnrC = TextColor.color(0x58c7ff);
    public static TextColor hsC = TextColor.color(0xFF5959);
    public static TextColor lnC = TextColor.color(0xC459FF);
    public static TextColor sC = TextColor.color(0x67FF59);

    public static void run() {
        Iterator<Player> it = playingPlayers.iterator();

        while (it.hasNext()) {
            Player player = it.next();
            player.sendActionBar(Component.text("Score: ").color(jnrC)
                    .append(Component.text(playerScores.get(player)).color(NamedTextColor.GOLD)));

            if (!player.isOnline()) {
                clearOldPath(player);
                it.remove();
                continue;
            }

            if (player.getLocation().getY() < 218) {
                Location spawn = new Location(player.getWorld(), 0.5, 64, 0.5, 90, 0);
                player.teleport(spawn);
                clearOldPath(player);
                it.remove();
                player.playSound(player, Sound.ENTITY_ITEM_BREAK, 1f, 1f);

                int score = playerScores.get(player);
                Storage.addToTotalScore(player.getUniqueId(),score);

                if (Storage.getHighScore(player.getUniqueId()) <= score) {
                    player.sendMessage(Component.text("\uE017 Neuer Highscore: ").color(jnrC)
                            .append(Component.text(score).color(hsC)));
                    if (Levels.isRankUp(player.getUniqueId(),score)) {
                        player.sendMessage(Component.text("\uE017 Du bist jetzt ").color(jnrC)
                                .append(Component.text(Levels.getLevelName(score)).color(lnC)
                                        .hoverEvent(HoverEvent.showText(Component.text("Erreiche einen Score von ").color(NamedTextColor.GREEN)
                                                .append(Component.text(Levels.getRequiredScore(Levels.getLevel(score))))))));
                    }
                    Storage.saveHighScore(player.getUniqueId(),score);
                } else {
                    player.sendMessage(Component.text("\uE017 Dein Score: ").color(jnrC)
                            .append(Component.text(score).color(sC)));
                }

                Leaderboard.updateLeaderboard(player);
                Leaderboard.updateAllLeaderboards();

                continue;
            }

            if (!currentBlock.containsKey(player) || !newStayBlock.containsKey(player)) continue;

            Location stayBlock = newStayBlock.get(player);
            Location playerLoc = player.getLocation();

            if (playerLoc.getBlockX() == stayBlock.getBlockX() &&
                    playerLoc.getBlockZ() == stayBlock.getBlockZ() &&
                    Math.abs(playerLoc.getY()) - stayBlock.getY() <= 1 &&

                    !currentBlock.get(player).equals(stayBlock)) {

                Bukkit.getScheduler().runTask(plugin, () -> {
                    generateNextJump(stayBlock, player);
                    player.playSound(player, Sound.BLOCK_AMETHYST_BLOCK_STEP,0.5F,1F);

                    if (oldBlock.containsKey(player) && oldBlock.get(player) != null) {
                        Location oldLoc = oldBlock.get(player);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            if (oldLoc != null && oldLoc.getWorld() != null) oldLoc.getWorld().getBlockAt(oldLoc).setType(Material.AIR);
                        }, 2L);
                    }

                    int currentScore = playerScores.get(player);
                    playerScores.replace(player, currentScore + 1);
                });
            }
        }
    }

    private static BlockDisplay createGlowingBlock(Player player, Location loc) {
        BlockDisplay display = loc.getWorld().spawn(loc.clone().add(0, 0, 0), BlockDisplay.class, bd -> {
            bd.setBlock(Bukkit.createBlockData(Material.GLASS));
            bd.setGlowing(true);
            bd.setPersistent(false);
            bd.setShadowRadius(0);
            bd.setShadowStrength(0);
        });

        for (Player other : Bukkit.getOnlinePlayers()) {
            if (!other.equals(player)) other.hideEntity(plugin, display);
        }

        return display;
    }

    public static boolean startJumpNRun(Player player) {
        if (player == null || !player.isOnline()) return false;
        clearOldPath(player);
        playingPlayers.add(player);
        playerScores.put(player,0);
        Leaderboard.createLeaderboard(player);
        Random random = new Random();

        for (int i = 0; i <= 20; i++) {
            int max = 30;
            int min = -30;
            int x = random.nextInt(max - min) + min;
            int z = random.nextInt(max - min) + min;
            int y = possibleYValues.get(random.nextInt(possibleYValues.size()));
            Location loc = new Location(player.getWorld(), x, y, z);

            if (isSafePos(loc)) {
                Block startBlock = loc.getWorld().getBlockAt(loc);
                startBlock.setType(Storage.getBlock(player));

                currentBlock.put(player,loc.clone());
                player.getWorld().setType(loc,Storage.getBlock(player));
                player.teleport(new Location(player.getWorld(), x + 0.5, y + 1, z + 0.5));
                generateNextJump(loc,player);
                return true;
            };
        }
        return true;
    }

    private static boolean isImpossible(Location oldLoc, Location newLoc) {
        double dx = Math.abs(newLoc.getX() - oldLoc.getX());
        double dz = Math.abs(newLoc.getZ() - oldLoc.getZ());

        if (dx == 4 && dz == 4 && oldLoc.getBlockY() == newLoc.getBlockY()) return true;
        if (oldLoc.getBlockY() + 1 != newLoc.getBlockY()) return false;

        boolean fourone = ((dx == 4 && dz == 0) || (dz == 4 && dx == 0));

        return (dx == 4 || dz == 4) && !fourone;
    }

    public static void generateNextJump(Location cBlock, Player player) {
        if (player == null || !player.isOnline()) return;

        if (newStayBlock.containsKey(player) && newStayBlock.get(player) != null) {
            oldBlock.put(player,currentBlock.get(player).clone());
            currentBlock.put(player,newStayBlock.get(player).clone());
        } else currentBlock.put(player,cBlock.clone());

        Random random = new Random();
        int X = cBlock.getBlockX();
        int Y = cBlock.getBlockY();
        int Z = cBlock.getBlockZ();

        for (int i = 0; i <= 20; i++) {
            int x = random.nextInt((X + 4) - (X - 4)) + (X - 4);
            int z = random.nextInt((Z + 4) - (Z - 4)) + (Z - 4);
            int y = random.nextInt((Y + 2) - (Y - 1)) + (Y - 1);
            Location loc = new Location(cBlock.getWorld(), x, y, z);
            if (isSafePos(loc) && !isImpossible(cBlock,loc) && loc.getY() > 218) {
                newStayBlock.put(player,loc);
                player.getWorld().setType(loc,Storage.getBlock(player));
                if (glowBlock.containsKey(player)) glowBlock.get(player).remove();
                BlockDisplay glow = createGlowingBlock(player, loc);
                glowBlock.put(player, glow);
                break;
            }
        }
    }

    private static boolean isSafePos(Location loc) {
        World world = loc.getWorld();
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();

        return world.getBlockAt(x, y, z).getType() == Material.AIR &&
                world.getBlockAt(x, y - 1, z).getType() == Material.AIR &&
                world.getBlockAt(x, y + 1, z).getType() == Material.AIR &&
                world.getBlockAt(x, y + 2, z).getType() == Material.AIR;
    }

    public static void clearOldPath(Player player) {
        if (oldBlock.containsKey(player)) {
            if (oldBlock.get(player) != null) oldBlock.get(player).getWorld().getBlockAt(oldBlock.get(player)).setType(Material.AIR);
            oldBlock.remove(player);
        }

        if (currentBlock.containsKey(player)) {
            if (currentBlock.get(player) != null) currentBlock.get(player).getWorld().getBlockAt(currentBlock.get(player)).setType(Material.AIR);
            currentBlock.remove(player);
        }

        if (newStayBlock.containsKey(player)) {
            if (newStayBlock.get(player) != null) newStayBlock.get(player).getWorld().getBlockAt(newStayBlock.get(player)).setType(Material.AIR);
            newStayBlock.remove(player);
        }

        if (glowBlock.containsKey(player)) {
            glowBlock.get(player).remove();
            glowBlock.remove(player);
        }
    }
}
