package fr.flowsqy.stelyeventreward.inventory;

import fr.flowsqy.stelyeventreward.StelyEventRewardPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SetSessionManager implements Listener {

    private final Map<UUID, ItemStack[]> sessions;
    private final File rewardFolder;

    public SetSessionManager(StelyEventRewardPlugin plugin, File pluginDataFolder) {
        sessions = new HashMap<>();
        rewardFolder = new File(pluginDataFolder, "rewards");
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public boolean toggle(Player player) {
        if (!sessions.containsKey(player.getUniqueId())) {
            sessions.put(player.getUniqueId(), player.getInventory().getContents());
            player.getInventory().clear();
            return false;
        }
        // TODO Store changes
        resetPlayer(player);
        return true;
    }

    public void resetAll() {
        for (Map.Entry<UUID, ItemStack[]> entry : sessions.entrySet()) {
            resetInventory(Bukkit.getPlayer(entry.getKey()), entry.getValue());
        }
        sessions.clear();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onDisconnect(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        resetPlayer(player);
    }

    private void resetPlayer(Player player) {
        final ItemStack[] originalContent = sessions.remove(player.getUniqueId());
        resetInventory(player, originalContent);
    }

    private void resetInventory(Player player, ItemStack[] originalContent) {
        if (originalContent == null)
            return;
        final PlayerInventory inventory = player.getInventory();
        inventory.clear();
        inventory.setContents(originalContent);
    }

}
