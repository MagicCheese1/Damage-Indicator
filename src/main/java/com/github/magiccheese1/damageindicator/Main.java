package com.github.magiccheese1.damageindicator;

import java.util.ArrayList;
import java.util.List;


import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
  private List<ArmorStand> toBeRemovedArmorstands;

  @Override
  public void onEnable() {
    // Save the default config from src/resources/config.yml
    this.saveDefaultConfig();
    // Get current config
    FileConfiguration config = this.getConfig();

    toBeRemovedArmorstands = new ArrayList<>();

    getServer().getPluginManager()
        .registerEvents(new DamageIndicatorManager(this, config, toBeRemovedArmorstands), this);
  }

  @Override
  public void onDisable() {
    for (ArmorStand armorStand : toBeRemovedArmorstands) {
      armorStand.remove();
    }
  }
}
