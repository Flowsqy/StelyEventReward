package fr.flowsqy.stelyeventreward.commands;

import fr.flowsqy.stelyeventreward.StelyEventRewardPlugin;
import fr.flowsqy.stelyeventreward.inventory.SetSessionManager;
import fr.flowsqy.stelyeventreward.io.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

public class SetRewardCommandHandler implements TabExecutor {

    private final Messages messages;
    private final SetSessionManager setSessionManager;

    public SetRewardCommandHandler(StelyEventRewardPlugin plugin) {
        messages = plugin.getMessages();
        setSessionManager = plugin.getSetSessionManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof final Player player)) {
            return messages.sendMessage(sender, "util.only-player");
        }
        if (args.length != 2) {
            return messages.sendMessage(sender, "give.help");
        }
        final String eventName = args[0];
        final String ranking = args[1];

        setSessionManager.toggle(player);

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return null;
    }
}
