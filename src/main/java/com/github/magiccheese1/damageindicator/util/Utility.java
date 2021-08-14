package com.github.magiccheese1.damageindicator.util;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.Optional;

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
    @SuppressWarnings("deprecation")
    public static boolean isCritical(Player damager) {
        return damager.getAttackCooldown() > 0.9F && damager.getFallDistance() > 0.0F
            && !damager.isOnGround() && !damager.isInWater() && damager.getActivePotionEffects().stream()
            .noneMatch(o -> o.getType().equals(PotionEffectType.BLINDNESS))
            && damager.getVehicle() == null && !damager.isSprinting();
    }

    /**
     * Parses a decimal format from the provided configuration file.
     * The decimal format will previously be translated using {@link ChatColor#translateAlternateColorCodes(char,
     * String)}.
     *
     * @param configuration the configuration instance to pull the string format from.
     * @param path          the path that correlates to the damage format inside the file configuration.
     *
     * @return the parsed decimal format. If the configuration did not contain the path, the returned optional will be
     *     empty.
     */
    @NotNull
    public static Optional<DecimalFormat> getConfigurationDamageFormat(@NotNull final FileConfiguration configuration,
                                                                       @NotNull String path) {
        final String stringFormat = configuration.getString(path);
        if (stringFormat == null) return Optional.empty();

        return Optional.of(new DecimalFormat(
            ChatColor.translateAlternateColorCodes('&', TextUtility.convertEasyHexToLegacy(stringFormat))
        ));
    }
}
