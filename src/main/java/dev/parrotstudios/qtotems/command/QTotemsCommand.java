package dev.parrotstudios.qtotems.command;

import dev.parrotstudios.qtotems.QTotems;
import dev.parrotstudios.qtotems.config.ConfigManager;
import dev.parrotstudios.qtotems.totem.QTotem;
import dev.parrotstudios.qtotems.totem.QTotemRegistry;
import dev.parrotstudios.qtotems.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class QTotemsCommand implements CommandExecutor, TabCompleter {
    private static String msgOnlyPlayers() {
        return ConfigManager.getString("messages.onlyPlayers", "<red>Only players can use this command!");
    }

    private static String msgUsage() {
        return ConfigManager.getString("messages.usage", "<yellow>Usage: /qtotems <totem> {player}");
    }

    private static String msgReloaded() {
        return ConfigManager.getString("messages.reloaded", "<green>Reloaded config!");
    }

    private static String msgInvalidTotem() {
        return ConfigManager.getString("messages.invalidTotem", "<red>Invalid totem!");
    }

    private static String msgGaveSelf() {
        return ConfigManager.getString("messages.gaveSelf", "<green>Gave you a custom totem!");
    }

    private static String msgGaveTarget(String targetName) {
        String tmpl = ConfigManager.getString("messages.gaveTarget", "<green>Gave %target% a custom totem!");
        return tmpl.replace("%target%", targetName);
    }

    private static String msgInvalidTarget() {
        return ConfigManager.getString("messages.invalidTarget", "<red>Invalid target!");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player)) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                ConfigManager.reloadConfig();
                QTotemRegistry.reload();
                sender.sendMessage(Utils.textWithPrefix(msgReloaded()));
                return true;
            }
            sender.sendMessage(Utils.textWithPrefix(msgOnlyPlayers()));
            return true;
        }
        Player player = (Player) sender;
        if(args.length == 0 || args.length > 2) {
            player.sendMessage(Utils.textWithPrefix(msgUsage()));
            return true;
        }
        if(args.length == 1){
            if(args[0].equalsIgnoreCase("reload")){
                ConfigManager.reloadConfig();
                QTotemRegistry.reload();
                player.sendMessage(Utils.textWithPrefix(msgReloaded()));
                return true;
            }
            QTotem totem = QTotemRegistry.getTotem(args[0]);
            if(totem == null) {
                player.sendMessage(Utils.textWithPrefix(msgInvalidTotem()));
                return true;
            }
            player.getInventory().addItem(totem.getTotemItem());
            player.sendMessage(Utils.textWithPrefix(msgGaveSelf()));
            return true;
        }
        QTotem totem = QTotemRegistry.getTotem(args[0]);
        Player target = QTotems.getInstance().getServer().getPlayer(args[1]);
        if(totem == null) {
            player.sendMessage(Utils.textWithPrefix(msgInvalidTotem()));
            return true;
        }
        if(target == null){
            player.sendMessage(Utils.textWithPrefix(msgInvalidTarget()));
            return true;
        }

        target.getInventory().addItem(totem.getTotemItem());
        target.sendMessage(Utils.textWithPrefix(msgGaveSelf()));
        player.sendMessage(Utils.textWithPrefix(msgGaveTarget(target.getName())));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if(args.length == 1){
            return QTotemRegistry.getTotemNames()
                    .stream()
                    .filter(totemName -> totemName.toLowerCase().startsWith(args[0].toLowerCase()))
                    .toList();
        }
        if(args.length == 2){
            if(!QTotemRegistry.getTotemNames().contains(args[0].toLowerCase())) return List.of();
            return QTotems.getInstance().getServer().getOnlinePlayers().stream().map(Player::getName).filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase())).toList();
        }
        return List.of();
    }
}