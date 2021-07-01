package fr.flowsqy.stelyeventreward.inventory;

import fr.flowsqy.stelyeventreward.StelyEventRewardPlugin;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SetSessionManager implements Listener {

    private final Map<UUID, SetSession> sessions;
    private final File rewardFolder;

    public SetSessionManager(StelyEventRewardPlugin plugin, File pluginDataFolder) {
        sessions = new HashMap<>();
        rewardFolder = new File(pluginDataFolder, "rewards");
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public boolean open(Player player) {
        final boolean inSession = sessions.containsKey(player.getUniqueId());
        if (!inSession) {
            final MoveProtectionTask moveProtectionTask = new MoveProtectionTask(player);
            sessions.put(player.getUniqueId(), new SetSession(
                    player.getInventory().getContents(),
                    player.getGameMode(),
                    moveProtectionTask)
            );
            player.getInventory().clear();
            player.setGameMode(GameMode.CREATIVE);
            moveProtectionTask.start();
        }
        return !inSession;
    }

    public boolean close(Player player, String event, String ranking) {
        final SetSession session = sessions.remove(player.getUniqueId());
        if (session == null)
            return false;
        // TODO Save in a config
        resetInventory(player, session);
        return true;
    }

    public void resetAll() {
        for (Map.Entry<UUID, SetSession> entry : sessions.entrySet()) {
            resetInventory(Bukkit.getPlayer(entry.getKey()), entry.getValue());
        }
        sessions.clear();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerInteract(PlayerInteractEvent event) {
        if (fastCheckPlayer(event.getPlayer().getUniqueId()))
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerClickInventory(InventoryClickEvent event) {
        if (
                (
                        event.getView().getType() != InventoryType.CREATIVE
                                || event.getView().getType() != InventoryType.PLAYER
                )
                        && fastCheckPlayer(event.getWhoClicked().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerDragInventory(InventoryDragEvent event) {
        if (
                (
                        event.getView().getType() != InventoryType.CREATIVE
                                || event.getView().getType() != InventoryType.PLAYER
                )
                        && fastCheckPlayer(event.getWhoClicked().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerDropItem(PlayerDropItemEvent event) {
        if (fastCheckPlayer(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerPickupItem(EntityPickupItemEvent event) {
        if (event.getEntityType() == EntityType.PLAYER && fastCheckPlayer(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    private boolean fastCheckPlayer(UUID uuid) {
        return !sessions.isEmpty() && sessions.containsKey(uuid);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onDisconnect(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final SetSession session = sessions.remove(player.getUniqueId());
        resetInventory(player, session);
    }

    private void resetInventory(Player player, SetSession session) {
        if (session == null)
            return;
        final PlayerInventory inventory = player.getInventory();
        inventory.clear();
        inventory.setContents(session.originalContent());
        player.setGameMode(session.previousGameMode());
        session.moveProtectionTask().cancel();
    }

    private final record SetSession(ItemStack[] originalContent, GameMode previousGameMode,
                                    MoveProtectionTask moveProtectionTask) {
    }

    private final static class MoveProtectionTask extends BukkitRunnable {

        private final Player player;
        private final Location originalLocation;

        public MoveProtectionTask(Player player) {
            this.player = player;
            originalLocation = player.getLocation();
        }

        @Override
        public void run() {
            if (!originalLocation.equals(player.getLocation())) {
                player.teleport(originalLocation, PlayerTeleportEvent.TeleportCause.UNKNOWN);
            }
        }

        public void start() {
            runTaskTimer(JavaPlugin.getPlugin(StelyEventRewardPlugin.class), 0L, 20L);
        }

    }

}
