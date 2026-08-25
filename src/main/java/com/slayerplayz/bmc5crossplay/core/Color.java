package com.slayerplayz.bmc5crossplay.core;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public final class Color {
    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.legacyAmpersand();
    private Color() {}
    public static String text(String value) { return value == null ? "" : value.replace('&', '§'); }
    public static Component component(String value) { return SERIALIZER.deserialize(value == null ? "" : value); }
}