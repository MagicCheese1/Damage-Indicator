package io.github.magiccheese1.damageindicator;

import com.tchristofferson.configupdater.ConfigUpdater;
import io.github.magiccheese1.damageindicator.config.Options;
import io.github.magiccheese1.damageindicator.packetManager.PacketManager;
import io.github.magiccheese1.damageindicator.packetManager.PacketManager1_16_R3;
import io.github.magiccheese1.damageindicator.packetManager.PacketManager1_17_R1;
import io.github.magiccheese1.damageindicator.packetManager.PacketManager1_18_R1;
import io.github.magiccheese1.damageindicator.packetManager.PacketManager1_19_R1;
import io.github.magiccheese1.damageindicator.packetManager.PacketManager1_19_R2;
import io.github.magiccheese1.damageindicator.packetManager.PacketManager1_19_R3;
import io.github.magiccheese1.damageindicator.packetManager.PacketManager1_20_R1;
import io.github.magiccheese1.damageindicator.packetManager.PacketManager1_20_R2;
import io.github.magiccheese1.damageindicator.packetManager.PacketManager1_20_R3;
import io.github.magiccheese1.damageindicator.packetManager.PacketManager1_20_R4;
import io.github.magiccheese1.damageindicator.packetManager.PacketManager1_21_R1;
import io.github.magiccheese1.damageindicator.packetManager.PacketManager1_21_R2;
import io.github.magiccheese1.damageindicator.packetManager.PacketManager1_21_R3;
import io.github.magiccheese1.damageindicator.packetManager.PacketManager1_21_R4;
import io.github.magiccheese1.damageindicator.packetManager.PacketManager1_21_R5;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

import static io.github.magiccheese1.damageindicator.config.ConfigUtility.getConfigurationDamageFormat;

public class DamageIndicatorImpl extends JavaPlugin implements DamageIndicator {
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
        final String serverName = Bukkit.getServer().getClass().getPackage().getName();
        String serverVersion = null;
        if (!serverName.contains(".")) {
            serverVersion = serverName.split("\\.")[3].trim();
        } else {
            serverVersion = Bukkit.getServer().getBukkitVersion().split("-")[0].trim();
        }
        switch (serverVersion) {
            case "1.16.4", "1.16.5" -> packetManager = new PacketManager1_16_R3();
            case "1.17", "1.17.1" -> packetManager = new PacketManager1_17_R1();
            case "1.18", "1.18.1", "1.18.2" -> packetManager = new PacketManager1_18_R1();
            case "1.19", "1.19.1", "1.19.2" -> packetManager = new PacketManager1_19_R1();
            case "1.19.3" -> packetManager = new PacketManager1_19_R2();
            case "1.19.4" -> packetManager = new PacketManager1_19_R3();
            case "1.20", "1.20.1" -> packetManager = new PacketManager1_20_R1();
            case "1.20.2" -> packetManager = new PacketManager1_20_R2();
            case "1.20.3", "1.20.4" -> packetManager = new PacketManager1_20_R3();
            case "1.20.5", "1.20.6" -> packetManager = new PacketManager1_20_R4();
            case "1.21", "1.21.1", "1.21.2" -> packetManager = new PacketManager1_21_R1();
            case "1.21.3" -> packetManager = new PacketManager1_21_R2();
            case "1.21.4" -> packetManager = new PacketManager1_21_R3();
            case "1.21.5" -> packetManager = new PacketManager1_21_R4();
            case "1.21.6", "1.21.7", "1.21.8" -> packetManager = new PacketManager1_21_R5();

            default -> throw new RuntimeException("Failed to create version specific server accessor");
        }
        getLogger().info(String.format("Using server version accessor for %s", serverVersion));
        getServer().getPluginManager().registerEvents(new BukkitEventListener(this), this);

        getServer().getServicesManager().register(DamageIndicator.class, this, this, ServicePriority.Normal);
    }

    private static Location findLocation(LivingEntity entity) {
        final ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < 20; i++) {
            Location location = entity.getLocation().add(
                random.nextDouble(0, 2) - 1, entity.getEyeHeight(), random.nextDouble(0, 2) - 1);
            if (location.getBlock().isPassable())
                return location;
        }
        return entity.getLocation();
    }

    @Override
    @NotNull
    public IndicatorEntity spawnIndicator(@NotNull Location location,
                                          @Nullable Player credit,
                                          @NotNull DecimalFormat format,
                                          double value,
                                          long lifespan) {
        Collection<Player> visibleTo = new ArrayList<>();
        if (getConfig().getBoolean(Options.SHOW_DAMAGE_ONLY) && credit != null)
            visibleTo.add(credit);
        else {
            Collection<Entity> nearbyEntities = location.getWorld().getNearbyEntities(location, 16, 16, 16);
            for (Entity entity : nearbyEntities)
                if (entity instanceof Player)
                    visibleTo.add((Player) entity);
        }
        IndicatorEntity indicator = new IndicatorEntityImpl(this, packetManager, location, value, format, visibleTo);
        indicator.spawn();
        if (lifespan != 0)
            indicator.scheduleDestroy(lifespan);
        return indicator;
    }

    @Override
    @NotNull
    public IndicatorEntity spawnIndicator(@NotNull LivingEntity entity,
                                          @Nullable Player credit,
                                          @NotNull DecimalFormat format,
                                          double value,
                                          long lifespan) {
        return spawnIndicator(findLocation(entity), credit, format, value, lifespan);
    }

    @Override
    @NotNull
    public IndicatorEntity spawnIndicator(@NotNull LivingEntity entity,
                                          @Nullable Player credit,
                                          @NotNull DecimalFormat format,
                                          double value) {

        return spawnIndicator(entity, credit, format, value, (long) (getConfig().getDouble(Options.INDICATOR_TIME,
            1.5) * 20));
    }

    @Override
    @NotNull
    public IndicatorEntity spawnIndicator(@NotNull Location location,
                                          @Nullable Player credit,
                                          @NotNull DecimalFormat format,
                                          double value) {
        return spawnIndicator(location, credit, format, value, (long) (getConfig().getDouble(Options.INDICATOR_TIME,
            1.5) * 20));
    }

    @Override
    public DecimalFormat getDamageFormat() {
        return getConfigurationDamageFormat(getConfig(), Options.FORMAT_INDICATOR).orElseThrow(
            () -> new IllegalStateException("Plugin configuration did not provide indicator format"));
    }

    @Override
    public DecimalFormat getCriticalDamageFormat() {
        return getConfigurationDamageFormat(getConfig(), Options.CRITICAL_FORMAT).orElseThrow(
            () -> new IllegalStateException("Plugin configuration did not provide indicator format"));
    }

    @Override
    public DecimalFormat getPoisonDamageFormat() {
        return getConfigurationDamageFormat(getConfig(), Options.POISON_FORMAT).orElseThrow(
            () -> new IllegalStateException("Plugin configuration did not provide indicator format"));
    }


}
