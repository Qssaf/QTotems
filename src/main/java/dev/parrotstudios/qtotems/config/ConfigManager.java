package dev.parrotstudios.qtotems.config;


import dev.parrotstudios.qtotems.config.impl.MainConfig;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConfigManager {

  private MainConfig mainConfig = new MainConfig();

  public void loadConfigs() {
    mainConfig.load();
  }

  public void reload(){
    mainConfig.reload();
  }
}
