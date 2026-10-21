package com.cardejibka.mobnet;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue ENABLE_FEATURE_X = BUILDER
            .comment("Enable some important feature")
            .define("enableFeatureX", true);

    public static final ModConfigSpec SPEC = BUILDER.build();
}