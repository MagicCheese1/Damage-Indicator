package com.github.magiccheese1.damageindicator;

import com.github.magiccheese1.damageindicator.util.Utility;
import com.github.magiccheese1.damageindicator.versions.PacketManager;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class BukkitEventListener implements Listener {

    private final Plugin plugin;
    private final PacketManager packetManager;
    private final NamespacedKey key;

    public BukkitEventListener(@NotNull final Plugin plugin, @NotNull final PacketManager packetManager) {
        this.plugin = plugin;
        this.packetManager = packetManager;
        this.key = new NamespacedKey(plugin, "poisoned-by");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void entityDamage(EntityDamageEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.POISON) return;
        PersistentDataContainer container = event.getEntity().getPersistentDataContainer();
        if (!(event.getEntity() instanceof LivingEntity)) return;
        if (container.has(key, PersistentDataType.STRING)) {
            final FileConfiguration configuration = this.plugin.getConfig();
            newIndicator((LivingEntity) event.getEntity(),
                plugin.getServer().getPlayer(UUID.fromString(container.get(key, PersistentDataType.STRING))),
                configuration.getBoolean(Utility.SHOW_DAMAGE_ONLY),
                Utility.getConfigurationDamageFormat(configuration, Utility.POISON_FORMAT).orElseThrow(
                    () -> new IllegalStateException("Plugin configuration did not provide indicator format")),
                event.getFinalDamage());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void potionSplash(PotionSplashEvent event) {
        PotionEffect effect =
            event.getPotion().getEffects().stream().filter(x -> x.getType().equals(PotionEffectType.POISON)).findAny()
                .orElse(null);
        if (Objects.isNull(effect)) return;
        if (!(event.getPotion().getShooter() instanceof Player damager)) return;
        for (LivingEntity entity : event.getAffectedEntities()) {
            markEntityPoisonedAndQueueUnmark(entity, damager, effect.getDuration());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void AreaEffectCloudApply(AreaEffectCloudApplyEvent event) {
        if (event.getEntity().getBasePotionData().getType() != PotionType.POISON) return;
        PersistentDataContainer container = event.getEntity().getPersistentDataContainer();
        if (!container.has(key, PersistentDataType.STRING)) return;
        Player damager = event.getEntity().getServer().getPlayer(UUID.fromString(container.get(key,
            PersistentDataType.STRING)));
        for (LivingEntity entity : event.getAffectedEntities()) {
            markEntityPoisonedAndQueueUnmark(entity, damager,
                PoisonLingeringPotionEffectDuration(event.getEntity().getBasePotionData()));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void LingeringPotionSplash(LingeringPotionSplashEvent event) {
        if (event.getAreaEffectCloud().getBasePotionData().getType() != PotionType.POISON) return;
        if (!(event.getEntity().getShooter() instanceof Player shooter)) return;
        event.getAreaEffectCloud().getPersistentDataContainer().set(key, PersistentDataType.STRING,
            shooter.getUniqueId().toString());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void entityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof final LivingEntity entity)) return;

        // Don't show indicator if the damagee is an armor stand
        if (entity instanceof ArmorStand) return;

        final FileConfiguration configuration = this.plugin.getConfig();
        Player damager = null;
        DecimalFormat damageFormat = Utility.getConfigurationDamageFormat(configuration,
                Utility.FORMAT_INDICATOR)
            .orElseThrow(() -> new IllegalStateException("Plugin configuration did not provide indicator " +
                "format"));

        // Check if the damager is an arrow. If it is use arrow.isCritical().
        // If it isn't use the custom isCritical() for direct damage.
        if (event.getDamager() instanceof final Projectile projectile) {

            // Don't show indicator if the arrow doesn't belong to a player
            if (!(projectile.getShooter() instanceof Player)) return;
            damager = (Player) projectile.getShooter();

            if (projectile instanceof AbstractArrow) {
                if (((AbstractArrow) projectile).isCritical()) {
                    damageFormat
                        =
                        Utility.getConfigurationDamageFormat(configuration, Utility.CRITICAL_FORMAT).orElseThrow(
                            () -> new IllegalStateException(
                                "Plugin configuration did not provide critical indicator format"));
                }
                if (projectile instanceof Arrow) {
                    if (((Arrow) projectile).getBasePotionData().getType() == PotionType.POISON) {
                        markEntityPoisonedAndQueueUnmark(entity, damager,
                            PoisonArrowEffectDuration(((Arrow) projectile).getBasePotionData()));
                    }
                }
            }
        } else if (event.getDamager() instanceof Player) {
            damager = (Player) event.getDamager();

            if (Utility.isCritical(damager)) {
                damageFormat =
                    Utility.getConfigurationDamageFormat(configuration, Utility.CRITICAL_FORMAT).orElseThrow(
                        () -> new IllegalStateException(
                            "Plugin configuration did not provide critical indicator format"));
            }
        }

        if (damager == null) return; // Could not parse the damaging player from the event.

        newIndicator(entity, damager, configuration.getBoolean(Utility.SHOW_DAMAGE_ONLY), damageFormat,
            event.getFinalDamage());
    }

    private void newIndicator(@NotNull LivingEntity entity, Player damager, boolean showDamagerOnly,
                              @NotNull DecimalFormat damageFormat, double damage) {
        final ThreadLocalRandom random = ThreadLocalRandom.current();
        // Attempts to final a random positions until it finds one that is not inside a block
        Location spawnLocation;
        int attempts = 0;
        do {
            attempts++;
            spawnLocation = entity.getLocation().add(
                random.nextDouble(0, 2) - 1d, 1, random.nextDouble(0, 2) - 1d);
            if (attempts > 20) {
                spawnLocation = entity.getLocation();
                break;
            }
        } while (!spawnLocation.getBlock().isPassable());

        // figure out who should see the indicator
        List<Player> packetRecipients = new ArrayList<>();
        if (showDamagerOnly) {
            for (Entity nearbyEntity : damager.getNearbyEntities(16, 16, 16)) {
                if (nearbyEntity instanceof Player) packetRecipients.add((Player) nearbyEntity);
            }
        } else if (Objects.isNull(damager)) return;

        packetRecipients.add(damager);

        final Location finalSpawnLocation = spawnLocation;
        final DecimalFormat finalDamageFormat = damageFormat;

        // Queue packet sending.
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin,
            () -> summonAndQueueIndicatorDeletion(finalSpawnLocation, finalDamageFormat, damage,
                packetRecipients));
    }

    private void markEntityPoisonedAndQueueUnmark(@NotNull LivingEntity entity, @NotNull Player damager,
                                                  int effectDuration) {
        entity.getPersistentDataContainer().set(key, PersistentDataType.STRING,
            damager.getUniqueId().toString());
        this.plugin.getServer().getScheduler().runTaskLaterAsynchronously(this.plugin, () -> {
            if (Objects.equals(entity.getPersistentDataContainer().get(key, PersistentDataType.STRING),
                damager.getUniqueId().toString()))
                entity.getPersistentDataContainer().remove(key);
        }, effectDuration);
    }

    private void summonAndQueueIndicatorDeletion(@NotNull Location location,
                                                 @NotNull DecimalFormat nameFormat,
                                                 double damage,
                                                 @NotNull List<@NotNull Player> packetRecipients) {
        //Create the entity
        final Object indicatorEntity = packetManager.buildEntityArmorStand(location,
            String.valueOf(nameFormat.format(damage)));

        //Create the packets
        final Object entitySpawnPacket = packetManager.buildEntitySpawnPacket(indicatorEntity);
        final Object entityMetadataPacket = packetManager.buildEntityMetadataPacket(indicatorEntity, true);

        //Send the packets
        for (Player recipient : packetRecipients) {
            packetManager.sendPacket(entitySpawnPacket, recipient);
            packetManager.sendPacket(entityMetadataPacket, recipient);
        }

        this.plugin.getServer().getScheduler().runTaskLaterAsynchronously(this.plugin, () -> {
            //Create the destroy packet
            final Object entityDestroyPacket = packetManager.buildEntityDestroyPacket(indicatorEntity);

            //Send the destroy packet
            for (final Player recipient : packetRecipients)
                packetManager.sendPacket(entityDestroyPacket, recipient);
        }, (long) (this.plugin.getConfig().getDouble(Utility.INDICATOR_TIME, 1.5) * 20));
    }

    private int PoisonArrowEffectDuration(PotionData basePotionData) {
        if (basePotionData.isUpgraded()) return 54;
        if (basePotionData.isExtended()) return 225;
        return 112;
    }

    private int PoisonLingeringPotionEffectDuration(PotionData basePotionData) {
        if (basePotionData.isUpgraded()) return 108;
        if (basePotionData.isExtended()) return 450;
        return 225;
    }
}
