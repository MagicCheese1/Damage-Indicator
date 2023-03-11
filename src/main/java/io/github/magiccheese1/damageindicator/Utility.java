package io.github.magiccheese1.damageindicator;

import io.github.magiccheese1.damageindicator.config.Options;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides basic utility functionality.
 */
public class Utility {
    private static final Pattern EASY_HEX_PATTERN = Pattern.compile("[§&]#[a-fA-F0-9]{6}");

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
        final String formatLocale = Optional.ofNullable(configuration.getString(Options.FORMAT_LOCALE))
            .orElse("en-US");
        if (stringFormat == null) return Optional.empty();
        DecimalFormat format = ((DecimalFormat) NumberFormat.getNumberInstance(Locale.forLanguageTag(formatLocale)));
        format.applyPattern(
            ChatColor.translateAlternateColorCodes('&', convertEasyHexToLegacy(stringFormat)));
        return Optional.of(format);
    }

    /**
     * Replaces any occurrence of the easy hex format in the passed string with the legacy minecraft hex colour format.
     * More specifically, the easy hex format is defined by the colour escape character (§) followed by a hashtag and
     * six characters defining the hex colour. An example of this would be `§#FFFFFF` for white.
     * <p>
     * As a note, the legacy hex format would contain the six hex characters all escaped by a § and prefixed with §x.
     * An example would be `§x§F§F§F§F§F§F` for white.
     *
     * @param easyHexString the source string containing the easy hex colour.
     *
     * @return the now fully legacy conform string containing the legacy hex representation.
     */
    @NotNull
    public static String convertEasyHexToLegacy(@NotNull final String easyHexString) {
        final Matcher matcher = EASY_HEX_PATTERN.matcher(easyHexString);
        return matcher.replaceAll(r -> {
            final String group = r.group();
            final StringBuilder builder = new StringBuilder(14); // 7 chars in total, all escaped.
            builder.append("§x");

            for (int i = 2; i < 8; i++) {
                builder.append("§").append(group.charAt(i));
            }
            return builder.toString();
        });
    }


    public static int poisonArrowEffectDuration(PotionData basePotionData) {
        if (basePotionData.isUpgraded()) return 54;
        if (basePotionData.isExtended()) return 225;
        return 112;
    }

    public static int poisonLingeringPotionEffectDuration(PotionData basePotionData) {
        if (basePotionData.isUpgraded()) return 108;
        if (basePotionData.isExtended()) return 450;
        return 225;
    }
}
