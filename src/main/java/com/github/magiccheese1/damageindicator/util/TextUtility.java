package com.github.magiccheese1.damageindicator.util;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The text utility provides basic utility functionality to modify text that is displayed to players.
 */
public final class TextUtility {

    private static final Pattern EASY_HEX_PATTERN = Pattern.compile("[§&]#[a-fA-F0-9]{6}");

    private TextUtility() {
        throw new UnsupportedOperationException("Cannot create instance of stateless legacy text converter!");
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
}
