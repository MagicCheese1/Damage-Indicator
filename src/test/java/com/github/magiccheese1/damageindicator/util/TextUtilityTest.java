package com.github.magiccheese1.damageindicator.util;

import net.md_5.bungee.api.ChatColor;
import org.junit.Assert;
import org.junit.Test;

import java.text.DecimalFormat;

public class TextUtilityTest {

    @Test
    public void testBasic() {
        Assert.assertEquals(
            "§x§F§F§F§F§F§F This is white",
            TextUtility.convertEasyHexToLegacy("§#FFFFFF This is white")
        );
    }

    @Test
    public void testMultiple() {
        Assert.assertEquals(
            "§x§F§F§F§F§F§F White and §x§3§3§F§F§5§7 green",
            TextUtility.convertEasyHexToLegacy("§#FFFFFF White and §#33FF57 green")
        );
    }

    @Test
    public void testIgnored() {
        Assert.assertEquals(
            "§#FFFFF White with 5 and #33FF57 green without",
            TextUtility.convertEasyHexToLegacy("§#FFFFF White with 5 and #33FF57 green without")
        );
    }

    @Test
    public void testRealConfigValue() {
        Assert.assertEquals(
            "§x§3§3§F§F§5§7-0.#&4❤",
            TextUtility.convertEasyHexToLegacy("&#33FF57-0.#&4❤")
        );
    }

    @Test
    public void testInDecimalFormat() {
        final String postHexConversion = TextUtility.convertEasyHexToLegacy("&#33FF57-0.#&4❤");
        final String postChatColourConversion = ChatColor.translateAlternateColorCodes('&', postHexConversion);

        final DecimalFormat decimalFormat = new DecimalFormat(postChatColourConversion);
        Assert.assertEquals(
            "§x§3§3§F§F§5§7-1§4❤",
            decimalFormat.format(1d)
        );
    }
}
