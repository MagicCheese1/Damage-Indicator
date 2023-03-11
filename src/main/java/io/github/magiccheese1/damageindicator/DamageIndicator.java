package io.github.magiccheese1.damageindicator;

import com.tchristofferson.configupdater.ConfigUpdater;
import io.github.magiccheese1.damageindicator.config.Options;
import io.github.magiccheese1.damageindicator.packetManager.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

public class DamageIndicator extends JavaPlugin {
    PacketManager packetManager;

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
            case "v1_19_R1":
                packetManager = PacketManager1_19_R1.make();
                break;
            case "v1_19_R2":
                packetManager = PacketManager1_19_R2.make();
                break;
            default:
                throw new RuntimeException("Failed to create version specific server accessor");
        }
        getLogger().info(String.format("Using server version accessor for %s", serverVersion));
        getServer().getPluginManager().registerEvents(new BukkitEventListener(this), this);
    }

    private static Location findLocation(Entity entity) {
        final ThreadLocalRandom random = ThreadLocalRandom.current();
        //TODO: Rework indicator positioning logic
        for (int i = 0; i < 20; i++) {
            Location location = entity.getLocation().add(
                random.nextDouble(0, 2) - 1, 1, random.nextDouble(0, 2) - 1);
            if (!location.getBlock().isPassable())
                return location;
        }
        return entity.getLocation();
    }

    public IndicatorEntity spawnIndicator(Location location, @Nullable Player credit, DecimalFormat format,
                                          double value) {
        Collection<Player> visibleTo = new ArrayList<>();
        if (getConfig().getBoolean(Options.SHOW_DAMAGE_ONLY) && credit != null)
            visibleTo.add(credit);
        else {
            Collection<Entity> nearbyEntities = location.getWorld().getNearbyEntities(location, 16, 16, 16);
            for (Entity entity : nearbyEntities)
                if (entity instanceof Player)
                    visibleTo.add((Player) entity);
        }
        IndicatorEntity indicator = new IndicatorEntity(this, packetManager, location, value, format, visibleTo);
        indicator.spawn();
        indicator.scheduleDestroy((long) getConfig().getDouble(Options.INDICATOR_TIME, 1.5) * 20);
        return indicator;
    }

    public IndicatorEntity spawnIndicator(LivingEntity entity, Player credit, DecimalFormat format, double value) {
        return spawnIndicator(findLocation(entity), credit, format, value);
    }
}
