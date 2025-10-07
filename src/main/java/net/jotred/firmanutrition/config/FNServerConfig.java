package net.jotred.firmanutrition.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class FNServerConfig
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.DoubleValue maximumNutrition = BUILDER
        .comment("")
        .comment(" Maximum (hidden) nutrition that players can have going above 100%, making it easier to reach 100% average nutrition")
        .defineInRange("maximum_nutrition", 1.1, 1, Float.MAX_VALUE);

    public static final ModConfigSpec.BooleanValue proportionalNutritionDecay = BUILDER
        .comment("")
        .comment(" Whether the decay of nutrients is proportional to the amount of nutrients a player has. Otherwise, it will just be a constant decay rate")
        .define("proportional_nutrition_decay", true);

    public static final ModConfigSpec.DoubleValue proportionalDecayFloor = BUILDER
        .comment("")
        .comment(" The minimum proportion of the nutrition decay rate that is applied, if proportional nutrition decay is enabled.")
        .comment(" This is basically the amount of nutrients a meal must provide to maintain a stable level of player nutrition intake / outflow")
        .defineInRange("proportional_decay_floor", 0.75, 0, 1);

    public static final ModConfigSpec SPEC = BUILDER.build();
}
