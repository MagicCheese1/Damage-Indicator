package com.github.magiccheese1.damageindicator;

import com.github.magiccheese1.damageindicator.versions.PacketManager;
import com.github.magiccheese1.damageindicator.versions.PacketManager1_16_R3;
import com.github.magiccheese1.damageindicator.versions.PacketManager1_17_R1;
import com.tchristofferson.configupdater.ConfigUpdater;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Main extends JavaPlugin {

    public static String serverVersion;

    @Override
    public void onEnable() {
        // Save the default config from src/resources/config.yml
        saveDefaultConfig();
        // The config needs to exist before using the updater
        File configFile = new File(getDataFolder(), "config.yml");
        try {
            ConfigUpdater.update(this, "config.yml", configFile, Arrays.asList("..."));
        } catch (IOException e) {
            e.printStackTrace();
        }
        reloadConfig();
        // Register Command
        getCommand("damageindicator").setExecutor(new Cmd(this));
        // Get current minecraft version
        serverVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].trim();
        PacketManager packetManager;
        getLogger().info(serverVersion);
        if (serverVersion.equals("v1_16_R3")) {
            packetManager = new PacketManager1_16_R3();
        } else {
            packetManager = new PacketManager1_17_R1();
        }
        getServer().getPluginManager().registerEvents(new BukkitEventListener(this, packetManager), this);
    }

}
