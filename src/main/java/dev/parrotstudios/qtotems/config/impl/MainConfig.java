package dev.parrotstudios.qtotems.config.impl;

import dev.parrotstudios.qtotems.QTotems;
import dev.parrotstudios.qtotems.config.Config;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.bukkit.configuration.ConfigurationSection;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MainConfig extends Config {

  String prefix;


  String onlyPlayersMessage;
  String usageMessage;
  String reloadedMessage;
  String invalidTotemMessage;
  String gaveSelfMessage;
  String gaveTargetMessage;
  String invalidTargetMessage;

  public MainConfig() {
    super("config");
  }

  @Override
  public void loadFields() {
    this.prefix = getConfig().getString("prefix", "<dark_red>[<red><bold>QTotems</bold></red>]</dark_red> ");

    ConfigurationSection messagesSection = getConfig().getConfigurationSection("messages");
    if (messagesSection != null) {
      this.onlyPlayersMessage = messagesSection.getString("onlyPlayers", "<red>Only players can use this command!");
      this.usageMessage = messagesSection.getString("usage", "<yellow>Usage: /qtotems <totem> {player}");
      this.reloadedMessage = messagesSection.getString("reloaded", "<green>Config has been reloaded successfully!</green>");
      this.invalidTotemMessage = messagesSection.getString("invalidTotem", "<red>Invalid totem!");
      this.gaveSelfMessage = messagesSection.getString("gaveSelf", "<green>Gave you a custom totem!</green>");
      this.gaveTargetMessage = messagesSection.getString("gaveTarget", "<green>Gave %target% a custom totem!</green>");
      this.invalidTargetMessage = messagesSection.getString("invalidTarget", "<red>Invalid target!");
    } else {
      QTotems.getInstance().getLogger().warning("Messages section is missing in config.yml! Using default messages.");
      this.onlyPlayersMessage = "<red>Only players can use this command!";
      this.usageMessage = "<yellow>Usage: /qtotems <totem> {player}";
      this.reloadedMessage = "<green>Config has been reloaded successfully!</green>";
      this.invalidTotemMessage = "<red>Invalid totem!";
      this.gaveSelfMessage = "<green>Gave you a custom totem!</green>";
      this.gaveTargetMessage = "<green>Gave %target% a custom totem!</green>";
      this.invalidTargetMessage = "<red>Invalid target!";
    }
  }


}