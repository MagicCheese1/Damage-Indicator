package io.github.magiccheese1.damageindicator;

import io.github.magiccheese1.damageindicator.config.Options;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.LingeringPotionSplashEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.Objects;
import java.util.UUID;

import static io.github.magiccheese1.damageindicator.Utility.poisonArrowEffectDuration;
import static io.github.magiccheese1.damageindicator.Utility.poisonLingeringPotionEffectDuration;
import static io.github.magiccheese1.damageindicator.config.ConfigUtility.getConfigurationDamageFormat;

public class BukkitEventListener implements Listener {

    private final DamageIndicatorImpl damageIndicator;
    private final NamespacedKey poisonedByKey;
    private final NamespacedKey burnedByKey;
    private final NamespacedKey harmedByKey;

    public BukkitEventListener(@NotNull final DamageIndicatorImpl damageIndicator) {
        this.damageIndicator = damageIndicator;
        this.poisonedByKey = new NamespacedKey(damageIndicator, "poisoned-by");
        this.burnedByKey = new NamespacedKey(damageIndicator, "burned-by");
        this.harmedByKey = new NamespacedKey(damageIndicator, "harmed-by");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void entityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof LivingEntity livingEntity)) return;

        PersistentDataContainer container = event.getEntity().getPersistentDataContainer();
        //POISON DAMAGE
        if (container.has(poisonedByKey, PersistentDataType.STRING) && event.getCause() == EntityDamageEvent.DamageCause.POISON) {

            final FileConfiguration configuration = this.damageIndicator.getConfig();
            DecimalFormat poisonFormat = getConfigurationDamageFormat(configuration, Options.POISON_FORMAT).orElseThrow(
                () -> new IllegalStateException("Plugin configuration did not provide indicator format")
            );

            damageIndicator.spawnIndicator(
                livingEntity,
                damageIndicator.getServer().getPlayer(UUID.fromString(container.get(poisonedByKey,
                    PersistentDataType.STRING))),
                poisonFormat,
                event.getFinalDamage()
            );
            //FIRE DAMAGE
        } else if (container.has(burnedByKey, PersistentDataType.STRING) && event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
            final FileConfiguration configuration = this.damageIndicator.getConfig();
            DecimalFormat burnFormat = getConfigurationDamageFormat(configuration, Options.BURN_FORMAT).orElseThrow(
                () -> new IllegalStateException("Plugin configuration did not provide indicator format")
            );

            damageIndicator.spawnIndicator(
                livingEntity,
                damageIndicator.getServer().getPlayer(UUID.fromString(container.get(burnedByKey,
                    PersistentDataType.STRING))),
                burnFormat,
                event.getFinalDamage()
            );
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void potionSplash(PotionSplashEvent event) {
        if (!(event.getPotion().getShooter() instanceof Player damager)) return;

        PotionEffect effect = event.getPotion().getEffects().stream()
            .filter(x -> x.getType().equals(PotionEffectType.POISON))
            .findAny()
            .orElse(null);

        if (Objects.isNull(effect)) return;
        for (LivingEntity entity : event.getAffectedEntities()) {
            markEntityAndQueueUnmark(entity, damager.getUniqueId(), effect.getDuration(), poisonedByKey);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void lingeringPotionSplash(LingeringPotionSplashEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player shooter)) return;
        if(event.getAreaEffectCloud().getBasePotionData() == null) return;
        if (event.getAreaEffectCloud().getBasePotionData().getType() == PotionType.POISON) {
            event.getAreaEffectCloud().getPersistentDataContainer().set(
                poisonedByKey, PersistentDataType.STRING, shooter.getUniqueId().toString()
            );
        } else if (event.getAreaEffectCloud().getBasePotionData().getType() == PotionType.INSTANT_DAMAGE) {
            event.getAreaEffectCloud().getPersistentDataContainer().set(
                harmedByKey, PersistentDataType.STRING, shooter.getUniqueId().toString()
            );
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void areaEffectCloudApply(AreaEffectCloudApplyEvent event) {
        if(event.getEntity().getBasePotionData() == null) return;
        if (event.getEntity().getBasePotionData().getType() != PotionType.POISON) return;

        PersistentDataContainer container = event.getEntity().getPersistentDataContainer();
        if (!container.has(poisonedByKey, PersistentDataType.STRING)) return;

        final UUID uuid = UUID.fromString(container.get(poisonedByKey, PersistentDataType.STRING));

        for (LivingEntity entity : event.getAffectedEntities()) {
            markEntityAndQueueUnmark(
                entity,
                uuid,
                poisonLingeringPotionEffectDuration(event.getEntity().getBasePotionData()),
                poisonedByKey
            );
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void entityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof final LivingEntity entity)) return;
        if (entity instanceof ArmorStand) return;

        final FileConfiguration configuration = this.damageIndicator.getConfig();
        Player damager = null;
        DecimalFormat damageFormat = getConfigurationDamageFormat(
            configuration,
            Options.FORMAT_INDICATOR
        ).orElseThrow(() -> new IllegalStateException("Plugin configuration did not provide indicator " +
            "format"));
        if (event.getDamager() instanceof final AreaEffectCloud areaEffectCloud) {
            if(areaEffectCloud.getBasePotionData() == null) return;
            if (areaEffectCloud.getBasePotionData().getType() == PotionType.INSTANT_DAMAGE) {
                if (areaEffectCloud.getPersistentDataContainer().has(harmedByKey, PersistentDataType.STRING)) {
                    damager =
                        damageIndicator.getServer().getPlayer(UUID.fromString(
                            areaEffectCloud.getPersistentDataContainer().get(harmedByKey, PersistentDataType.STRING)));
                    damageFormat =
                        getConfigurationDamageFormat(configuration, Options.INSTANT_DAMAGE_FORMAT).orElseThrow(
                            () -> new IllegalStateException(
                                "Plugin configuration did not provide instant damage indicator format"
                            )
                        );
                }
            }
        } else if (event.getDamager() instanceof final Projectile projectile) {
            if (!(projectile.getShooter() instanceof Player player)) return;

            damager = player;
            if (damager.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.ARROW_FIRE) > 0)
                markEntityAndQueueUnmark(entity, damager.getUniqueId(), 100, burnedByKey);

            if (projectile instanceof ThrownPotion potion) {
                if (potion.getEffects().stream().anyMatch(e -> e.getType().equals(PotionEffectType.HARM))) {
                    damageFormat =
                        getConfigurationDamageFormat(configuration, Options.INSTANT_DAMAGE_FORMAT).orElseThrow(
                            () -> new IllegalStateException(
                                "Plugin configuration did not provide instant damage indicator " +
                                    "format"));
                }
            } else if (projectile instanceof AbstractArrow abstractArrow) {

                if (abstractArrow.isCritical()) {
                    damageFormat = getConfigurationDamageFormat(configuration, Options.CRITICAL_FORMAT).orElseThrow(
                        () -> new IllegalStateException("Plugin configuration did not provide critical indicator " +
                            "format")
                    );
                }

                if (projectile instanceof Arrow arrow) {
                    if (arrow.getBasePotionData() != null) { // Can return null in newer versions
                        PotionData potionData = arrow.getBasePotionData();
                        if (potionData.getType() == PotionType.POISON) {
                            markEntityAndQueueUnmark(
                                entity,
                                damager.getUniqueId(),
                                poisonArrowEffectDuration(((Arrow) projectile).getBasePotionData()),
                                poisonedByKey
                            );
                        } else if (potionData.getType() == PotionType.INSTANT_DAMAGE) {
                            damageFormat =
                                getConfigurationDamageFormat(configuration, Options.INSTANT_DAMAGE_FORMAT).orElseThrow(
                                    () -> new IllegalStateException("Plugin configuration did not provide instant " +
                                        "damage " +
                                        "indicator " +
                                        "format"));
                        }
                    }
                }
            }
        } else if (event.getDamager() instanceof Player) {
            damager = (Player) event.getDamager();

            //mark burning
            if (damager.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.FIRE_ASPECT) > 0)
                markEntityAndQueueUnmark(entity, damager.getUniqueId(), 80, burnedByKey);

            if (Utility.isCritical(damager)) {
                damageFormat = getConfigurationDamageFormat(configuration, Options.CRITICAL_FORMAT).orElseThrow(
                    () -> new IllegalStateException("Plugin configuration did not provide critical indicator format")
                );
            }
            if(event.getCause() == EntityDamageEvent.DamageCause.THORNS) {
                damageFormat = getConfigurationDamageFormat(configuration, Options.THORNS_DAMAGE_FORMAT).orElseThrow(
                    () -> new IllegalStateException("Plugin configuration did not provide thorns indicator format")
                );
            }
        }

        if (damager == null) return; // Could not parse the damaging player from the event.

        damageIndicator.spawnIndicator(
            entity,
            damager,
            damageFormat,
            event.getFinalDamage()
        );
    }

    private void markEntityAndQueueUnmark(@NotNull LivingEntity entity,
                                          @NotNull UUID damagerUUID,
                                          int effectDuration, NamespacedKey markingKey) {
        entity.getPersistentDataContainer().set(markingKey, PersistentDataType.STRING, damagerUUID.toString());
        this.damageIndicator.getServer().getScheduler().runTaskLater(this.damageIndicator, () -> {
            if (Objects.equals(
                entity.getPersistentDataContainer().get(markingKey, PersistentDataType.STRING),
                damagerUUID.toString()
            )) {
                entity.getPersistentDataContainer().remove(markingKey);
            }
        }, effectDuration);
    }


}
