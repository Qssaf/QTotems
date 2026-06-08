package dev.parrotstudios.qtotems.command;

import dev.parrotstudios.qtotems.QTotems;
import dev.parrotstudios.qtotems.totem.QTotem;
import dev.parrotstudios.qtotems.totem.QTotemRegistry;
import dev.parrotstudios.qtotems.utils.TextUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class QTotemsCommand implements CommandExecutor, TabCompleter {




    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                QTotems.getConfigManager().getMainConfig().reload();
                QTotemRegistry.reload();
                sender.sendMessage(TextUtils.textWithPrefix(QTotems.getConfigManager().getMainConfig().getReloadedMessage()));
                return true;
            }
            if (args.length == 1) {
                if (QTotemRegistry.getQTotem(args[0]) != null) {
                    sender.sendMessage(TextUtils.textWithPrefix(QTotems.getConfigManager().getMainConfig().getOnlyPlayersMessage()));
                    return true;
                }
                sender.sendMessage(TextUtils.textWithPrefix(QTotems.getConfigManager().getMainConfig().getInvalidTotemMessage()));
                return true;
            }
            if (args.length == 2) {
                QTotem totem = QTotemRegistry.getQTotem(args[0]);
                Player target = QTotems.getInstance().getServer().getPlayer(args[1]);
                if (totem == null) {
                    sender.sendMessage(TextUtils.textWithPrefix(QTotems.getConfigManager().getMainConfig().getInvalidTotemMessage()));
                    return true;
                }
                if (target == null) {
                    sender.sendMessage(TextUtils.textWithPrefix(QTotems.getConfigManager().getMainConfig().getInvalidTargetMessage()));
                    return true;
                }
                target.getInventory().addItem(totem.getTotemItem());
                target.sendMessage(TextUtils.textWithPrefix(QTotems.getConfigManager().getMainConfig().getGaveSelfMessage()));
                sender.sendMessage(TextUtils.textWithPrefix(QTotems.getConfigManager().getMainConfig().getGaveTargetMessage().replace("%target%", target.getName())));
                return true;

            }
            sender.sendMessage(TextUtils.textWithPrefix(QTotems.getConfigManager().getMainConfig().getUsageMessage()));
            return true;
        }
        if (args.length == 0 || args.length > 2) {
            player.sendMessage(TextUtils.textWithPrefix(QTotems.getConfigManager().getMainConfig().getUsageMessage()));
            return true;
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                QTotems.getConfigManager().getMainConfig().reload();
                QTotemRegistry.reload();
                player.sendMessage(TextUtils.textWithPrefix(QTotems.getConfigManager().getMainConfig().getReloadedMessage()));
                return true;
            }
            QTotem totem = QTotemRegistry.getQTotem(args[0]);
            if (totem == null) {
                player.sendMessage(TextUtils.textWithPrefix(QTotems.getConfigManager().getMainConfig().getInvalidTotemMessage()));
                return true;
            }
            player.getInventory().addItem(totem.getTotemItem());
            player.sendMessage(TextUtils.textWithPrefix(QTotems.getConfigManager().getMainConfig().getGaveSelfMessage()));
            return true;
        }
        QTotem totem = QTotemRegistry.getQTotem(args[0]);
        Player target = QTotems.getInstance().getServer().getPlayer(args[1]);
        if (totem == null) {
            player.sendMessage(TextUtils.textWithPrefix(QTotems.getConfigManager().getMainConfig().getInvalidTotemMessage()));
            return true;
        }
        if (target == null) {
            player.sendMessage(TextUtils.textWithPrefix(QTotems.getConfigManager().getMainConfig().getInvalidTargetMessage()));
            return true;
        }
        if (target == player) {
            player.getInventory().addItem(totem.getTotemItem());
            player.sendMessage(TextUtils.textWithPrefix(QTotems.getConfigManager().getMainConfig().getGaveSelfMessage()));
            return true;
        }

        target.getInventory().addItem(totem.getTotemItem());
        target.sendMessage(TextUtils.textWithPrefix(QTotems.getConfigManager().getMainConfig().getGaveSelfMessage()));
        player.sendMessage(TextUtils.textWithPrefix(QTotems.getConfigManager().getMainConfig().getGaveTargetMessage().replace("%target%", target.getName())));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            return QTotemRegistry.getTotemNames()
                    .stream()
                    .filter(totemName -> totemName.toLowerCase().startsWith(args[0].toLowerCase()))
                    .toList();
        }
        if (args.length == 2) {
            if (QTotemRegistry.getQTotem(args[0]) == null) return List.of();
            return QTotems.getInstance().getServer().getOnlinePlayers().stream().map(Player::getName).filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase())).toList();
        }
        return List.of();
    }
}