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

    public static final ModConfigSpec.BooleanValue forceCustomDecayRates = BUILDER
        .comment("")
        .comment(" Force player decay rates to be equal to the custom decay rates set in the config, recommended if you want to change decay rates on a public server.")
        .comment(" This will go into effect once a player joins the world / server, and will override decay rates set via command!")
        .define("force_custom_decay_rates", false);

    public static final ModConfigSpec.DoubleValue defaultGrainDecayRate = BUILDER
        .comment("")
        .defineInRange("default_grain_decay_rate", 1.0, 0, Float.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue defaultFruitDecayRate = BUILDER
        .defineInRange("default_fruit_decay_rate", 1.0, 0, Float.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue defaultVegetablesDecayRate = BUILDER
        .defineInRange("default_vegetables_decay_rate", 1.0, 0, Float.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue defaultProteinDecayRate = BUILDER
        .defineInRange("default_protein_decay_rate", 1.0, 0, Float.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue defaultDairyDecayRate = BUILDER
        .defineInRange("default_dairy_decay_rate", 1.0, 0, Float.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue nutritionModifierOnDeath = BUILDER
        .comment("")
        .comment(" The remaining percentage of nutrition a player keeps after dying, if the TFC config `keepNutritionAfterDeath` is enabled.")
        .comment(" At `0` this will remove all nutrients from a player, while at `1` it won't change the nutrients at all")
        .defineInRange("nutrition_modifier_on_death", 1.0, 0, 1);



    public static final ModConfigSpec SPEC = BUILDER.build();
}
