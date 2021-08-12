package com.github.magiccheese1.damageindicator;

import com.github.magiccheese1.damageindicator.versions.PacketManager;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class BukkitEventListener implements Listener {

    private final Plugin plugin;
    private final PacketManager packetManager;

    public BukkitEventListener(@NotNull final Plugin plugin, @NotNull final PacketManager packetManager) {
        this.plugin = plugin;
        this.packetManager = packetManager;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void entityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) return;

        final LivingEntity entity = (LivingEntity) event.getEntity();
        // Don't show indicator if the damagee is an armor stand
        if (entity instanceof ArmorStand) return;

        final ThreadLocalRandom random = ThreadLocalRandom.current();
        final FileConfiguration configuration = this.plugin.getConfig();
        Player damager = null;
        DecimalFormat damageFormat = Utility.getConfigurationDamageFormat(configuration, Utility.FORMAT_INDICATOR)
            .orElseThrow(() -> new IllegalStateException("Plugin configuration did not provide indicator format"));

        // Check if the damager is an arrow. If it is use arrow.isCritical().
        // If it isn't use the custom isCritical() for direct damage.
        if (event.getDamager() instanceof Projectile) {
            final Projectile projectile = (Projectile) event.getDamager();

            // Don't show indicator if the arrow doesn't belong to a player
            if (!(projectile.getShooter() instanceof Player)) return;
            damager = (Player) projectile.getShooter();

            if (projectile instanceof AbstractArrow && ((AbstractArrow) projectile).isCritical()) {
                damageFormat = Utility.getConfigurationDamageFormat(configuration, Utility.CRITICAL_FORMAT).orElseThrow(
                    () -> new IllegalStateException("Plugin configuration did not provide critical indicator format")
                );
            }
        } else if (event.getDamager() instanceof Player) {
            damager = (Player) event.getDamager();

            if (Utility.isCritical(damager)) {
                damageFormat = Utility.getConfigurationDamageFormat(configuration, Utility.CRITICAL_FORMAT).orElseThrow(
                    () -> new IllegalStateException("Plugin configuration did not provide critical indicator format")
                );
            }
        }

        if (damager == null) return; // Could not parse the damaging player from the event.

        // Attempts to final a random positions until it finds one that is not inside a block
        Location spawnLocation;
        int attempts = 0;
        do {
            attempts++;
            spawnLocation = entity.getLocation().add(random.nextDouble(0, 2) - 1d, 1, random.nextDouble(0, 2) - 1d);
            if (attempts > 20) {
                spawnLocation = entity.getLocation();
                break;
            }
        } while (!spawnLocation.getBlock().isPassable());

        // figure out who should see the indicator
        List<Player> packetRecipients = new ArrayList<>();
        packetRecipients.add(damager);
        if (!configuration.getBoolean(Utility.SHOW_DAMAGE_ONLY)) {
            for (Entity nearbyEntity : damager.getNearbyEntities(16, 16, 16)) {
                if (nearbyEntity instanceof Player) packetRecipients.add((Player) nearbyEntity);
            }
        }

        final Location finalSpawnLocation = spawnLocation;
        final DecimalFormat finalDamageFormat = damageFormat;

        // Queue packet sending.
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin,
            () -> summonAndQueueIndicatorDeletion(
                finalSpawnLocation,
                finalDamageFormat,
                event.getFinalDamage(),
                packetRecipients
            )
        );
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
            for (final Player recipient : packetRecipients) packetManager.sendPacket(entityDestroyPacket, recipient);
        }, (long) (this.plugin.getConfig().getDouble(Utility.INDICATOR_TIME, 1.5) * 20));
    }

}
