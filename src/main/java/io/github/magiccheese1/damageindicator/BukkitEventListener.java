package io.github.magiccheese1.damageindicator;

import io.github.magiccheese1.damageindicator.config.Options;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.Objects;
import java.util.UUID;

import static io.github.magiccheese1.damageindicator.Utility.poisonArrowEffectDuration;
import static io.github.magiccheese1.damageindicator.Utility.poisonLingeringPotionEffectDuration;
import static io.github.magiccheese1.damageindicator.config.configUtility.getConfigurationDamageFormat;

public class BukkitEventListener implements Listener {

    private final DamageIndicator damageIndicator;
    private final NamespacedKey key;

    public BukkitEventListener(@NotNull final DamageIndicator damageIndicator) {
        this.damageIndicator = damageIndicator;
        this.key = new NamespacedKey(damageIndicator, "poisoned-by");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void entityDamage(EntityDamageEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.POISON) return;
        if (!(event.getEntity() instanceof LivingEntity)) return;

        PersistentDataContainer container = event.getEntity().getPersistentDataContainer();

        if (container.has(key, PersistentDataType.STRING)) {
            final FileConfiguration configuration = this.damageIndicator.getConfig();
            DecimalFormat poisonFormat =
                getConfigurationDamageFormat(configuration, Options.POISON_FORMAT).orElseThrow(
                    () -> new IllegalStateException("Plugin configuration did not provide indicator format"));

            damageIndicator.spawnIndicator((LivingEntity) event.getEntity(),
                damageIndicator.getServer().getPlayer(UUID.fromString(container.get(key, PersistentDataType.STRING))),
                poisonFormat,
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
    public void LingeringPotionSplash(LingeringPotionSplashEvent event) {
        if (event.getAreaEffectCloud().getBasePotionData().getType() != PotionType.POISON) return;
        if (!(event.getEntity().getShooter() instanceof Player shooter)) return;

        event.getAreaEffectCloud().getPersistentDataContainer().set(key, PersistentDataType.STRING,
            shooter.getUniqueId().toString());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void AreaEffectCloudApply(AreaEffectCloudApplyEvent event) {
        PersistentDataContainer container = event.getEntity().getPersistentDataContainer();

        if (event.getEntity().getBasePotionData().getType() != PotionType.POISON) return;
        if (!container.has(key, PersistentDataType.STRING)) return;

        Player damager = event.getEntity().getServer().getPlayer(UUID.fromString(container.get(key,
            PersistentDataType.STRING)));

        for (LivingEntity entity : event.getAffectedEntities()) {
            markEntityPoisonedAndQueueUnmark(entity, damager,
                poisonLingeringPotionEffectDuration(event.getEntity().getBasePotionData()));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void entityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof final LivingEntity entity)) return;
        if (entity instanceof ArmorStand) return;

        final FileConfiguration configuration = this.damageIndicator.getConfig();
        Player damager = null;
        DecimalFormat damageFormat = getConfigurationDamageFormat(configuration,
            Options.FORMAT_INDICATOR)
            .orElseThrow(() -> new IllegalStateException("Plugin configuration did not provide indicator " +
                "format"));

        if (event.getDamager() instanceof final Projectile projectile) {
            if (!(projectile.getShooter() instanceof Player)) return;
            damager = (Player) projectile.getShooter();

            if (!(projectile instanceof AbstractArrow))
                return;
            if (((AbstractArrow) projectile).isCritical()) {
                damageFormat
                    =
                    getConfigurationDamageFormat(configuration, Options.CRITICAL_FORMAT).orElseThrow(
                        () -> new IllegalStateException(
                            "Plugin configuration did not provide critical indicator format"));
            }
            if (projectile instanceof Arrow) {
                if (((Arrow) projectile).getBasePotionData().getType() == PotionType.POISON) {
                    markEntityPoisonedAndQueueUnmark(entity, damager,
                        poisonArrowEffectDuration(((Arrow) projectile).getBasePotionData()));
                }
            }

        } else if (event.getDamager() instanceof Player) {
            damager = (Player) event.getDamager();

            if (Utility.isCritical(damager)) {
                damageFormat =
                    getConfigurationDamageFormat(configuration, Options.CRITICAL_FORMAT).orElseThrow(
                        () -> new IllegalStateException(
                            "Plugin configuration did not provide critical indicator format"));
            }
        }

        if (damager == null) return; // Could not parse the damaging player from the event.

        damageIndicator.spawnIndicator(entity,
            damager,
            damageFormat,
            event.getFinalDamage());
    }

    private void markEntityPoisonedAndQueueUnmark(@NotNull LivingEntity entity, @NotNull Player damager,
                                                  int effectDuration) {
        entity.getPersistentDataContainer().set(key, PersistentDataType.STRING,
            damager.getUniqueId().toString());
        this.damageIndicator.getServer().getScheduler().runTaskLaterAsynchronously(this.damageIndicator, () -> {
            if (Objects.equals(entity.getPersistentDataContainer().get(key, PersistentDataType.STRING),
                damager.getUniqueId().toString()))
                entity.getPersistentDataContainer().remove(key);
        }, effectDuration);
    }

}
