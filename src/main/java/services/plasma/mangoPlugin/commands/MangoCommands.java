package services.plasma.mangoPlugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import services.plasma.mangoPlugin.MangoPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MangoCommands implements CommandExecutor, TabCompleter {

    private final MangoPlugin plugin;
    private final List<String> subCommands = Arrays.asList("give", "reload", "help");

    public MangoCommands(MangoPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "give":
                return handleGiveCommand(sender, args);
            case "reload":
                return handleReloadCommand(sender);
            case "help":
                sendHelp(sender);
                return true;
            default:
                sender.sendMessage("§cUnknown sub-command. Type §f/mango help §cfor help.");
                return true;
        }
    }

    /**
     * Handle the give command
     */
    private boolean handleGiveCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("mangoplugin.give")) {
            sender.sendMessage("§cYou don't have permission to use this command.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§cUsage: §f/mango give <player> [mango|seed] [amount]");
            return true;
        }

        // Get target player
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("§cPlayer not found: " + args[1]);
            return true;
        }

        // Default to mango if not specified
        String itemType = args.length >= 3 ? args[2].toLowerCase() : "mango";

        // Default to 1 if amount not specified
        int amount = 1;
        if (args.length >= 4) {
            try {
                amount = Integer.parseInt(args[3]);
                if (amount < 1) amount = 1;
                if (amount > 64) amount = 64;
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid amount: " + args[3]);
                return true;
            }
        }

        // Give the item
        ItemStack itemToGive;
        if (itemType.equals("seed")) {
            itemToGive = plugin.getMangoItems().getMangoSeedItem();
            itemToGive.setAmount(amount);
            target.getInventory().addItem(itemToGive);
            sender.sendMessage("§aGave " + amount + " mango seed(s) to " + target.getName());
        } else {
            itemToGive = plugin.getMangoItems().getMangoItem();
            itemToGive.setAmount(amount);
            target.getInventory().addItem(itemToGive);
            sender.sendMessage("§aGave " + amount + " mango(s) to " + target.getName());
        }

        return true;
    }

    /**
     * Handle the reload command
     */
    private boolean handleReloadCommand(CommandSender sender) {
        if (!sender.hasPermission("mangoplugin.reload")) {
            sender.sendMessage("§cYou don't have permission to reload the plugin.");
            return true;
        }

        plugin.reloadConfig();
        plugin.getConfigUtils().reloadConfig();
        sender.sendMessage("§aMango Plugin configuration reloaded!");

        return true;
    }

    /**
     * Send help message
     */
    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6===== Mango Plugin Commands =====");
        sender.sendMessage("§e/mango give <player> [mango|seed] [amount] §7- Give mango items");
        sender.sendMessage("§e/mango reload §7- Reload the plugin configuration");
        sender.sendMessage("§e/mango help §7- Show this help message");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            return filterCompletions(subCommands, args[0]);
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("give")) {
                return null; // Return null to show online players
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("give")) {
                return filterCompletions(Arrays.asList("mango", "seed"), args[2]);
            }
        }

        return completions;
    }

    /**
     * Filter completions based on the current argument
     */
    private List<String> filterCompletions(List<String> options, String arg) {
        return options.stream()
                .filter(option -> option.toLowerCase().startsWith(arg.toLowerCase()))
                .collect(Collectors.toList());
    }
}