package com.github.magiccheese1.damageindicator;

import com.github.magiccheese1.damageindicator.versions.*;
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
        switch (serverVersion) {
            case "v1_16_R3":
                packetManager = PacketManager1_16_R3.make();
                break;
            case "v1_17_R1":
                packetManager = PacketManager1_17_R1.make();
                break;
            case "v1_18_R1":
            case "v1_18_R2":
                packetManager = PacketManager1_18_R1.make();
                break;
            default:
                throw new RuntimeException("Failed to create version specific server accessor");
        }
        getLogger().info(String.format("Using server version accessor for %s", serverVersion));
        getServer().getPluginManager().registerEvents(new BukkitEventListener(this, packetManager), this);
    }

}
