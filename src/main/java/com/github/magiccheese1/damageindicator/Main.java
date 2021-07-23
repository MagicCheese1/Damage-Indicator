package com.github.magiccheese1.damageindicator;

import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

  @Override
  public void onEnable() {
    // Save the default config from src/resources/config.yml
    this.saveDefaultConfig();
    // Get current config
    FileConfiguration config = this.getConfig();

    getServer().getPluginManager().registerEvents(new DamageIndicatorManager(this, config), this);
  }

}
