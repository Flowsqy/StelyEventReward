package fr.flowsqy.stelyeventreward;

import fr.flowsqy.stelyeventreward.commands.CommandManager;
import fr.flowsqy.stelyeventreward.inventory.SetSessionManager;
import fr.flowsqy.stelyeventreward.io.Messages;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StelyEventRewardPlugin extends JavaPlugin {

    private Messages messages;
    private SetSessionManager setSessionManager;

    @Override
    public void onEnable() {
        final Logger logger = getLogger();
        final File dataFolder = getDataFolder();

        if (!checkDataFolder(dataFolder)) {
            logger.log(Level.WARNING, "Can not write in the directory : " + dataFolder.getAbsolutePath());
            logger.log(Level.WARNING, "Disable the plugin");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        this.messages = new Messages(initFile(dataFolder));
        this.setSessionManager = new SetSessionManager(this, dataFolder);

        new CommandManager(this);
    }

    @Override
    public void onDisable() {
        setSessionManager.resetAll();
    }

    private boolean checkDataFolder(File dataFolder) {
        if (dataFolder.exists())
            return dataFolder.canWrite();
        return dataFolder.mkdirs();
    }

    private YamlConfiguration initFile(File dataFolder) {
        final File file = new File(dataFolder, "messages.yml");
        if (!file.exists()) {
            try {
                Files.copy(Objects.requireNonNull(getResource("messages.yml")), file.toPath());
            } catch (IOException ignored) {
            }
        }

        return YamlConfiguration.loadConfiguration(file);
    }

    public Messages getMessages() {
        return messages;
    }

    public SetSessionManager getSetSessionManager() {
        return setSessionManager;
    }
}