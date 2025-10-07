package net.jotred.firmanutrition.common.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.jotred.firmanutrition.common.component.food.IAdditionalNutritionData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import net.dries007.tfc.common.component.food.Nutrient;
import net.dries007.tfc.common.player.IPlayerInfo;
import net.dries007.tfc.util.Helpers;

public class FNPlayerCommand
{
    private static final Component MISSING_DATA = Component.translatable("firmanutrition.commands.missing_data");
    private static final Component QUERY_DECAY_RATES = Component.translatable("firmanutrition.commands.query_decay_rates");

    public static LiteralArgumentBuilder<CommandSourceStack> create()
    {
        return Commands.literal("player")
            .requires(source -> source.hasPermission(2))
            .then(Commands.argument("target", EntityArgument.player())
                .then(Commands.literal("query")
                    .then(Commands.literal("decay_rate")
                        .executes(context -> queryDecayRates(context, EntityArgument.getPlayer(context, "target")))
                    )
                )
                .then(Commands.literal("set")
                    .then(Commands.literal("nutrition")
                        .then(Commands.literal("all")
                            .then(Commands.argument("value", IntegerArgumentType.integer(0))
                                .executes(context -> setAllNutrients(context, EntityArgument.getPlayer(context, "target"), IntegerArgumentType.getInteger(context, "value"), false))
                            )
                        )
                        .then(Commands.literal("grain")
                            .then(Commands.argument("value", IntegerArgumentType.integer(0))
                                .executes(context -> setNutrient(context, EntityArgument.getPlayer(context, "target"), Nutrient.GRAIN, IntegerArgumentType.getInteger(context, "value"), false))
                            )
                        )
                        .then(Commands.literal("fruit")
                            .then(Commands.argument("value", IntegerArgumentType.integer(0))
                                .executes(context -> setNutrient(context, EntityArgument.getPlayer(context, "target"), Nutrient.FRUIT, IntegerArgumentType.getInteger(context, "value"), false))
                            )
                        )
                        .then(Commands.literal("vegetables")
                            .then(Commands.argument("value", IntegerArgumentType.integer(0))
                                .executes(context -> setNutrient(context, EntityArgument.getPlayer(context, "target"), Nutrient.VEGETABLES, IntegerArgumentType.getInteger(context, "value"), false))
                            )
                        )
                        .then(Commands.literal("protein")
                            .then(Commands.argument("value", IntegerArgumentType.integer(0))
                                .executes(context -> setNutrient(context, EntityArgument.getPlayer(context, "target"), Nutrient.PROTEIN, IntegerArgumentType.getInteger(context, "value"), false))
                            )
                        )
                        .then(Commands.literal("dairy")
                            .then(Commands.argument("value", IntegerArgumentType.integer(0))
                                .executes(context -> setNutrient(context, EntityArgument.getPlayer(context, "target"), Nutrient.DAIRY, IntegerArgumentType.getInteger(context, "value"), false))
                            )
                        )
                    )
                    .then(Commands.literal("decay_rate")
                        .then(Commands.literal("all")
                            .then(Commands.argument("value", IntegerArgumentType.integer(0))
                                .executes(context -> setAllDecayRates(context, EntityArgument.getPlayer(context, "target"), IntegerArgumentType.getInteger(context, "value"), false))
                            )
                        )
                        .then(Commands.literal("grain")
                            .then(Commands.argument("value", IntegerArgumentType.integer(0))
                                .executes(context -> setDecayRate(context, EntityArgument.getPlayer(context, "target"), Nutrient.GRAIN, IntegerArgumentType.getInteger(context, "value"), false))
                            )
                        )
                        .then(Commands.literal("fruit")
                            .then(Commands.argument("value", IntegerArgumentType.integer(0))
                                .executes(context -> setDecayRate(context, EntityArgument.getPlayer(context, "target"), Nutrient.FRUIT, IntegerArgumentType.getInteger(context, "value"), false))
                            )
                        )
                        .then(Commands.literal("vegetables")
                            .then(Commands.argument("value", IntegerArgumentType.integer(0))
                                .executes(context -> setDecayRate(context, EntityArgument.getPlayer(context, "target"), Nutrient.VEGETABLES, IntegerArgumentType.getInteger(context, "value"), false))
                            )
                        )
                        .then(Commands.literal("protein")
                            .then(Commands.argument("value", IntegerArgumentType.integer(0))
                                .executes(context -> setDecayRate(context, EntityArgument.getPlayer(context, "target"), Nutrient.PROTEIN, IntegerArgumentType.getInteger(context, "value"), false))
                            )
                        )
                        .then(Commands.literal("dairy")
                            .then(Commands.argument("value", IntegerArgumentType.integer(0))
                                .executes(context -> setDecayRate(context, EntityArgument.getPlayer(context, "target"), Nutrient.DAIRY, IntegerArgumentType.getInteger(context, "value"), false))
                            )
                        )
                    )
                )
                .then(Commands.literal("reset")
                    .then(Commands.literal("nutrition")
                        .then(Commands.literal("all")
                            .executes(context ->
                                setNutrient(context, EntityArgument.getPlayer(context, "target"), Nutrient.GRAIN, 50, false)
                                    & setNutrient(context, EntityArgument.getPlayer(context, "target"), Nutrient.FRUIT, 50, false)
                                    & setNutrient(context, EntityArgument.getPlayer(context, "target"), Nutrient.VEGETABLES, 50, false)
                                    & setNutrient(context, EntityArgument.getPlayer(context, "target"), Nutrient.PROTEIN, 50, false)
                                    & setNutrient(context, EntityArgument.getPlayer(context, "target"), Nutrient.DAIRY, 0, false)
                            )
                        )
                        .then(Commands.literal("grain")
                            .executes(context -> setNutrient(context, EntityArgument.getPlayer(context, "target"), Nutrient.GRAIN, 50, false))
                        )
                        .then(Commands.literal("fruit")
                            .executes(context -> setNutrient(context, EntityArgument.getPlayer(context, "target"), Nutrient.FRUIT, 50, false))
                        )
                        .then(Commands.literal("vegetables")
                            .executes(context -> setNutrient(context, EntityArgument.getPlayer(context, "target"), Nutrient.VEGETABLES, 50, false))
                        )
                        .then(Commands.literal("protein")
                            .executes(context -> setNutrient(context, EntityArgument.getPlayer(context, "target"), Nutrient.PROTEIN, 50, false))
                        )
                        .then(Commands.literal("dairy")
                            .executes(context -> setNutrient(context, EntityArgument.getPlayer(context, "target"), Nutrient.DAIRY, 0, false))
                        )
                    )
                    .then(Commands.literal("decay_rate")
                        .then(Commands.literal("all")
                            .executes(context -> setAllDecayRates(context, EntityArgument.getPlayer(context, "target"), 100, false))
                        )
                        .then(Commands.literal("grain")
                            .executes(context -> setDecayRate(context, EntityArgument.getPlayer(context, "target"), Nutrient.GRAIN, 100, false))
                        )
                        .then(Commands.literal("fruit")
                            .executes(context -> setDecayRate(context, EntityArgument.getPlayer(context, "target"), Nutrient.FRUIT, 100, false))
                        )
                        .then(Commands.literal("vegetables")
                            .executes(context -> setDecayRate(context, EntityArgument.getPlayer(context, "target"), Nutrient.VEGETABLES, 100, false))
                        )
                        .then(Commands.literal("protein")
                            .executes(context -> setDecayRate(context, EntityArgument.getPlayer(context, "target"), Nutrient.PROTEIN, 100, false))
                        )
                        .then(Commands.literal("dairy")
                            .executes(context -> setDecayRate(context, EntityArgument.getPlayer(context, "target"), Nutrient.DAIRY, 100, false))
                        )
                    )
                )
                .then(Commands.literal("add")
                    .then(Commands.literal("nutrition")
                        .then(Commands.literal("all")
                            .then(Commands.argument("value", IntegerArgumentType.integer(0))
                                .executes(context -> setAllNutrients(context, EntityArgument.getPlayer(context, "target"), IntegerArgumentType.getInteger(context, "value"), true))
                            )
                        )
                        .then(Commands.literal("grain")
                            .then(Commands.argument("value", IntegerArgumentType.integer(0))
                                .executes(context -> setNutrient(context, EntityArgument.getPlayer(context, "target"), Nutrient.GRAIN, IntegerArgumentType.getInteger(context, "value"), true))
                            )
                        )
                        .then(Commands.literal("fruit")
                            .then(Commands.argument("value", IntegerArgumentType.integer(0))
                                .executes(context -> setNutrient(context, EntityArgument.getPlayer(context, "target"), Nutrient.FRUIT, IntegerArgumentType.getInteger(context, "value"), true))
                            )
                        )
                        .then(Commands.literal("vegetables")
                            .then(Commands.argument("value", IntegerArgumentType.integer(0))
                                .executes(context -> setNutrient(context, EntityArgument.getPlayer(context, "target"), Nutrient.VEGETABLES, IntegerArgumentType.getInteger(context, "value"), true))
                            )
                        )
                        .then(Commands.literal("protein")
                            .then(Commands.argument("value", IntegerArgumentType.integer(0))
                                .executes(context -> setNutrient(context, EntityArgument.getPlayer(context, "target"), Nutrient.PROTEIN, IntegerArgumentType.getInteger(context, "value"), true))
                            )
                        )
                        .then(Commands.literal("dairy")
                            .then(Commands.argument("value", IntegerArgumentType.integer(0))
                                .executes(context -> setNutrient(context, EntityArgument.getPlayer(context, "target"), Nutrient.DAIRY, IntegerArgumentType.getInteger(context, "value"), true))
                            )
                        )
                    )
                    .then(Commands.literal("decay_rate")
                        .then(Commands.literal("all")
                            .then(Commands.argument("value", IntegerArgumentType.integer(0))
                                .executes(context -> setAllDecayRates(context, EntityArgument.getPlayer(context, "target"), IntegerArgumentType.getInteger(context, "value"), true))
                            )
                        )
                        .then(Commands.literal("grain")
                            .then(Commands.argument("value", IntegerArgumentType.integer(0))
                                .executes(context -> setDecayRate(context, EntityArgument.getPlayer(context, "target"), Nutrient.GRAIN, IntegerArgumentType.getInteger(context, "value"), true))
                            )
                        )
                        .then(Commands.literal("fruit")
                            .then(Commands.argument("value", IntegerArgumentType.integer(0))
                                .executes(context -> setDecayRate(context, EntityArgument.getPlayer(context, "target"), Nutrient.FRUIT, IntegerArgumentType.getInteger(context, "value"), true))
                            )
                        )
                        .then(Commands.literal("vegetables")
                            .then(Commands.argument("value", IntegerArgumentType.integer(0))
                                .executes(context -> setDecayRate(context, EntityArgument.getPlayer(context, "target"), Nutrient.VEGETABLES, IntegerArgumentType.getInteger(context, "value"), true))
                            )
                        )
                        .then(Commands.literal("protein")
                            .then(Commands.argument("value", IntegerArgumentType.integer(0))
                                .executes(context -> setDecayRate(context, EntityArgument.getPlayer(context, "target"), Nutrient.PROTEIN, IntegerArgumentType.getInteger(context, "value"), true))
                            )
                        )
                        .then(Commands.literal("dairy")
                            .then(Commands.argument("value", IntegerArgumentType.integer(0))
                                .executes(context -> setDecayRate(context, EntityArgument.getPlayer(context, "target"), Nutrient.DAIRY, IntegerArgumentType.getInteger(context, "value"), true))
                            )
                        )
                    )
                )
            );
    }

    private static int setNutrient(CommandContext<CommandSourceStack> context, Player player, Nutrient nutrientType, int nutrientAmount, boolean add)
    {
        IPlayerInfo playerInfo = IPlayerInfo.get(player);

        if (playerInfo.nutrition() instanceof IAdditionalNutritionData nutrition)
        {
            if (add)
            {
                nutrition.addNutrient(nutrientType, (float) nutrientAmount / 100);
            }
            else
            {
                nutrition.setNutrient(nutrientType, (float) nutrientAmount / 100);
            }

            playerInfo.forceUpdate();
        }
        else
        {
            context.getSource().sendFailure(MISSING_DATA);
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int setAllNutrients(CommandContext<CommandSourceStack> context, Player player, int nutrientAmount, boolean add)
    {
        IPlayerInfo playerInfo = IPlayerInfo.get(player);

        if (playerInfo.nutrition() instanceof IAdditionalNutritionData nutrition)
        {
            if (add)
            {
                nutrition.addAllNutrients((float) nutrientAmount / 100);
            }
            else
            {
                nutrition.setAllNutrients((float) nutrientAmount / 100);
            }

            playerInfo.forceUpdate();
        }
        else
        {
            context.getSource().sendFailure(MISSING_DATA);
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int setDecayRate(CommandContext<CommandSourceStack> context, Player player, Nutrient nutrientType, int rate, boolean add)
    {
        if (IPlayerInfo.get(player).nutrition() instanceof IAdditionalNutritionData nutrition)
        {
            if (add)
            {
                nutrition.addDecayRate(nutrientType, (float) rate / 100);
            }
            else
            {
                nutrition.setDecayRate(nutrientType, (float) rate / 100);
            }
        }
        else
        {
            context.getSource().sendFailure(MISSING_DATA);
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int setAllDecayRates(CommandContext<CommandSourceStack> context, Player player, int rate, boolean add)
    {
        if (IPlayerInfo.get(player).nutrition() instanceof IAdditionalNutritionData nutrition)
        {
            if (add)
            {
                nutrition.addAllDecayRates((float) rate / 100);
            }
            else
            {
                nutrition.setAllDecayRates((float) rate / 100);
            }
        }
        else
        {
            context.getSource().sendFailure(MISSING_DATA);
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int queryDecayRates(CommandContext<CommandSourceStack> context, Player player)
    {
        if (IPlayerInfo.get(player).nutrition() instanceof IAdditionalNutritionData nutrition)
        {
            final float[] decayRates = nutrition.getDecayRates();
            context.getSource().sendSuccess(() -> QUERY_DECAY_RATES, true);

            for (Nutrient nutrient : Nutrient.VALUES)
            {
                int percent = (int) (100 * decayRates[nutrient.ordinal()]);
                context.getSource().sendSuccess(() -> Component.literal(" - ")
                    .append(Helpers.translateEnum(nutrient).withStyle(nutrient.getColor()))
                    .append(": " + percent + "%"), true);
            }
        }
        else
        {
            context.getSource().sendFailure(MISSING_DATA);
        }
        return Command.SINGLE_SUCCESS;
    }
}
