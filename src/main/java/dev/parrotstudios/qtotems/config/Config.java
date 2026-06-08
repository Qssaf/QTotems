package dev.parrotstudios.qtotems.config;

import java.io.File;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class Config {
  final String name;

  FileConfiguration config;
  
  protected abstract void loadFields();

  public void load() {
    config = YamlConfiguration.loadConfiguration(new File("plugins/QTotems/" + name + ".yml"));
    loadFields();
  }

  public void reload(){
    load();
    loadFields();
  }

}
