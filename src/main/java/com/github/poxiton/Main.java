package com.github.poxiton;

import com.github.poxiton.EntityHider.Policy;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
  private EntityHider entityHider;

  @Override
  public void onEnable() {
    this.saveDefaultConfig();
    FileConfiguration config = this.getConfig();
    entityHider = new EntityHider(this, Policy.BLACKLIST);
    getServer().getPluginManager().registerEvents(new DamageIndicatorListener(this, entityHider, config), this);
  }
}
