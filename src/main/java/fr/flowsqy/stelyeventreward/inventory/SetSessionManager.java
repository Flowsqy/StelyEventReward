package fr.flowsqy.stelyeventreward.inventory;

import fr.flowsqy.stelyeventreward.StelyEventRewardPlugin;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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
            sessions.put(player.getUniqueId(), new SetSession(player.getInventory().getContents(), player.getGameMode()));
            player.getInventory().clear();
            player.setGameMode(GameMode.CREATIVE);
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
    }

    private final record SetSession(ItemStack[] originalContent, GameMode previousGameMode) {
    }

}
