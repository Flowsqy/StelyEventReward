package fr.flowsqy.stelyeventreward.commands;

import fr.flowsqy.stelyeventreward.StelyEventRewardPlugin;
import fr.flowsqy.stelyeventreward.inventory.SetSessionManager;
import fr.flowsqy.stelyeventreward.io.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SetRewardCommandHandler implements TabExecutor {

    private final Messages messages;
    private final SetSessionManager setSessionManager;
    private final SubCommand[] subCommands;

    public SetRewardCommandHandler(StelyEventRewardPlugin plugin) {
        messages = plugin.getMessages();
        setSessionManager = plugin.getSetSessionManager();
        subCommands = new SubCommand[4];
        initSubCommands();
    }

    private void initSubCommands() {
        subCommands[0] = new SubCommand(
                "help",
                "h",
                this::helpExecutor,
                (player, args) -> Collections.emptyList()
        );
        subCommands[1] = new SubCommand(
                "edit",
                "e",
                this::editExecutor,
                (player, args) -> Collections.emptyList()
        );
        subCommands[2] = new SubCommand(
                "save",
                "s",
                this::saveExecutor,
                (player, args) -> Collections.emptyList()
        );
        subCommands[3] = new SubCommand(
                "message",
                "m",
                this::messageExecutor,
                this::messageTabCompleter
        );
    }

    private void helpExecutor(Player player, String[] args) {

    }

    private void editExecutor(Player player, String[] args) {

    }

    private void saveExecutor(Player player, String[] args) {

    }

    private void messageExecutor(Player player, String[] args) {

    }

    private List<String> messageTabCompleter(Player player, String[] args) {
        return Collections.emptyList();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof final Player player)) {
            return messages.sendMessage(sender, "util.only-player");
        }

        final SubCommand subCommand;
        if (args.length > 0) {
            final String subCommandName = args[0];
            final Optional<SubCommand> optionalSubCommand = Stream.of(subCommands)
                    .filter(subCmd -> subCmd.match(subCommandName))
                    .findAny();
            subCommand = optionalSubCommand.orElseGet(() -> subCommands[0]);
        } else {
            subCommand = subCommands[0];
        }

        subCommand.executor().accept(player, args);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return Collections.emptyList();
        }
        if (args.length > 0) {
            final String arg = args[0].toLowerCase(Locale.ROOT);
            if (arg.isEmpty()) {
                return Stream.of(subCommands).map(SubCommand::name).collect(Collectors.toList());
            }
            if (args.length == 1) {
                return Stream.of(subCommands)
                        .map(SubCommand::name)
                        .filter(name -> name.startsWith(arg))
                        .collect(Collectors.toList());
            }
            final Optional<SubCommand> optionalSubCommand = Stream.of(subCommands)
                    .filter(subCmd -> subCmd.match(arg))
                    .findAny();
            if (optionalSubCommand.isPresent()) {
                return optionalSubCommand.get().tabCompleter.apply(player, args);
            }
        }
        return Collections.emptyList();
    }

    private final record SubCommand(
            String name,
            String alias,
            BiConsumer<Player, String[]> executor,
            BiFunction<Player, String[], List<String>> tabCompleter
    ) {

        public boolean match(String arg) {
            return arg.equalsIgnoreCase(name) || arg.equalsIgnoreCase(alias);
        }

        public String getHelpPath() {
            return "setreward.help." + name;
        }

    }

}
