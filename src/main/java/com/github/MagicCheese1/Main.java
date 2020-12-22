package com.github.MagicCheese1;

import com.github.MagicCheese1.EntityHider.Policy;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
  private EntityHider entityHider;

  @Override
  public void onEnable() {
    // Save the default config from src/resources/config.yml
    this.saveDefaultConfig();
    // Get current config
    FileConfiguration config = this.getConfig();

    entityHider = new EntityHider(this, Policy.BLACKLIST);

    getServer().getPluginManager().registerEvents(new DamageIndicatorListener(this, entityHider, config), this);
  }
}
