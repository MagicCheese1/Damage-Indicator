package com.github.magiccheese1.damageindicator;

import com.github.magiccheese1.damageindicator.versions.PacketManager;
import com.github.magiccheese1.damageindicator.versions.PacketManager1_16_R3;
import com.github.magiccheese1.damageindicator.versions.PacketManager1_17_R1;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

  public static String serverVersion;

  @Override
  public void onEnable() {
    // Save the default config from src/resources/config.yml
    this.saveDefaultConfig();
    // Get current config
    FileConfiguration config = this.getConfig();

    // Get current minecraft version
    serverVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].trim();
    PacketManager packetManager = null;
    switch (serverVersion) {
      case "1_16_R3":
        packetManager = new PacketManager1_16_R3();
        break;
      default:
        packetManager = new PacketManager1_17_R1();
    }
    getServer().getPluginManager().registerEvents(new BukkitEventListener(this, config, packetManager), this);
  }

}
