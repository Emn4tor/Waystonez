package de.emn4tor.commands;

/*
 *  @author: Emn4tor
 *  @created: 29.03.2025
 */

import de.emn4tor.WaystonePlugin;
import de.emn4tor.WaystoneRecipe;
import de.emn4tor.gui.WaystoneGUI;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WaystoneCommands implements CommandExecutor {

    private final WaystonePlugin plugin;

    public WaystoneCommands(WaystonePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            // Open the waystone GUI
            new WaystoneGUI(plugin, player).open();
            return true;
        }

        if (args.length >= 1) {
            String subCommand = args[0].toLowerCase();

            switch (subCommand) {
                case "help":
                    sendHelpMessage(player);
                    break;

                case "list":
                    new WaystoneGUI(plugin, player).open();
                    break;

                case "give":
                    if (player.hasPermission("waystones.admin")) {
                        player.getInventory().addItem(WaystoneRecipe.createWaystoneItem(plugin));
                        player.sendMessage("§6You received a Waystone item.");
                    } else {
                        player.sendMessage("§cYou don't have permission to use this command.");
                    }
                    break;

                default:
                    player.sendMessage("§cUnknown subcommand. Use /waystone help for a list of commands.");
                    break;
            }

            return true;
        }

        return false;
    }

    private void sendHelpMessage(Player player) {
        player.sendMessage("§6=== Waystone Commands ===");
        player.sendMessage("§e/waystone §7- Open your waystone list");
        player.sendMessage("§e/waystone list §7- Open your waystone list");
        player.sendMessage("§e/waystone help §7- Show this help message");

        if (player.hasPermission("waystones.admin")) {
            player.sendMessage("§e/waystone give §7- Give yourself a waystone item");
        }
    }
}

