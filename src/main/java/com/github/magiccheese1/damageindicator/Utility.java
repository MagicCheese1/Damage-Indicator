package com.github.magiccheese1.damageindicator;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class Utility {

    /**
     * Determine if the direct hit was a critical hit
     *
     * @param damager - The damaging player
     */
    public static boolean isCritical(Player damager) {
        return damager.getAttackCooldown() > 0.9 && damager.getFallDistance() > 0.0F
                && !damager.getLocation().getBlock().isLiquid() && damager.getActivePotionEffects().stream()
                .noneMatch(o -> o.getType().equals(PotionEffectType.BLINDNESS))
                && damager.getVehicle() == null && !damager.isSprinting();
    }
}
