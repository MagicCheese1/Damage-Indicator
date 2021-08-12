package com.github.magiccheese1.damageindicator;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class Utility {


    public static final String SHOW_DAMAGE_ONLY = "ShowToDamagerOnly";
    public static final String FORMAT_INDICATOR = "IndicatorFormat";
    public static final String CRITICAL_FORMAT = "CriticalIndicatorFormat";
    public static final String INDICATOR_TIME = "IndicatorTime";


    /**
     * Determine if the direct hit was a critical hit
     *
     * @param damager - The damaging player
     */
    public static boolean isCritical(Player damager) {
        return damager.getCooledAttackStrength(0.5F) > 0.9F && damager.getFallDistance() > 0.0F
                && !damager.isOnGround() && !damager.isInWater() && damager.getActivePotionEffects().stream()
                .noneMatch(o -> o.getType().equals(PotionEffectType.BLINDNESS))
                && damager.getVehicle() == null && !damager.isSprinting();
    }
}
