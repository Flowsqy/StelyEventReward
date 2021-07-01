package fr.flowsqy.stelyeventreward.commands;

import fr.flowsqy.stelyeventreward.StelyEventRewardPlugin;
import org.bukkit.command.PluginCommand;

public class CommandManager {

    public CommandManager(StelyEventRewardPlugin plugin) {
        final PluginCommand setCommand = plugin.getCommand("seteventreward");
        if (setCommand != null) {
            final SetRewardCommandHandler setHandler = new SetRewardCommandHandler(plugin);
            setCommand.setExecutor(setHandler);
            setCommand.setTabCompleter(setHandler);
        }
        final PluginCommand giveCommand = plugin.getCommand("eventreward");
        if (giveCommand != null) {
            final GiveRewardCommandHandler giveHandler = new GiveRewardCommandHandler();
            giveCommand.setExecutor(giveHandler);
            giveCommand.setTabCompleter(giveHandler);
        }
    }

}
