package com.github.magiccheese1.damageindicator;

import com.github.magiccheese1.damageindicator.versions.PacketManager;
import com.github.magiccheese1.damageindicator.versions.v1_16_R3.PacketManagerFactory1_16_R3;
import com.github.magiccheese1.damageindicator.versions.v1_17_R1.PacketManagerFactory1_17_R1;
import com.tchristofferson.configupdater.ConfigUpdater;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        // Save the default config from src/resources/config.yml
        saveDefaultConfig();
        // The config needs to exist before using the updater
        File configFile = new File(getDataFolder(), "config.yml");
        try {
            ConfigUpdater.update(this, "config.yml", configFile, Collections.emptyList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        reloadConfig();
        // Register Command
        getCommand("damageindicator").setExecutor(new CommandReload(this));

        // Get current minecraft version
        final String serverVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].trim();
        PacketManager packetManager;
        if (serverVersion.equals("v1_16_R3")) {
            packetManager = PacketManagerFactory1_16_R3.make();
        } else {
            packetManager = PacketManagerFactory1_17_R1.make();
        }
        getLogger().info(String.format("Using server version accessor for %s", serverVersion));
        getServer().getPluginManager().registerEvents(new BukkitEventListener(this, packetManager), this);
    }

}
