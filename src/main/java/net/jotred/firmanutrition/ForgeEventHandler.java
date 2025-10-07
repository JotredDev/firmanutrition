package net.jotred.firmanutrition;

import net.jotred.firmanutrition.common.commands.FNPlayerCommand;
import net.jotred.firmanutrition.common.component.food.FNNutritionData;
import net.jotred.firmanutrition.common.component.food.IAdditionalNutritionData;
import net.minecraft.commands.Commands;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import net.dries007.tfc.common.component.food.Nutrient;
import net.dries007.tfc.common.player.IPlayerInfo;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.events.NutritionDataEvent;

public class ForgeEventHandler
{
    public static void init()
    {
        final IEventBus bus = NeoForge.EVENT_BUS;

        bus.addListener(EventPriority.HIGH, ForgeEventHandler::registerNutritionData); // We choose high priority, so any other mod listening to the NutritionDataEvent overwrites this by default, but doesn't have to do so
        bus.addListener(ForgeEventHandler::registerCommands);
        bus.addListener(ForgeEventHandler::onPlayerDeath);
    }

    public static void registerNutritionData(NutritionDataEvent event)
    {
        event.setSupplier(FNNutritionData::new);
    }

    public static void registerCommands(RegisterCommandsEvent event)
    {
        // Adding the command `/tfc player add/reset/set nutrition/decay_rate`, following the style of the `/tfc player add/reset/set hunger/saturation/water` commands from TFC
        event.getDispatcher().register(Commands.literal("tfc")
            .then(FNPlayerCommand.create())
        );
    }

    public static void onPlayerDeath(PlayerEvent.Clone event)
    {
        // This event fires before respawn event, and allows us to copy decay rates to the new player.
        // It is only necessary to do anything if the `keepNutritionAfterDeath` config is false, since otherwise decay rates are copied along with the rest of the nutrition data
        if (!TFCConfig.SERVER.keepNutritionAfterDeath.get() && event.isWasDeath())
        {
            final IPlayerInfo oldInfo = IPlayerInfo.get(event.getOriginal());
            final IPlayerInfo newInfo = IPlayerInfo.get(event.getEntity());

            if (newInfo.nutrition() instanceof IAdditionalNutritionData newNutrition && oldInfo.nutrition() instanceof IAdditionalNutritionData oldNutrition)
            {
                for (Nutrient nutrient : Nutrient.VALUES)
                {
                    newNutrition.setDecayRate(nutrient, oldNutrition.getDecayRate(nutrient));
                }
            }
        }
    }
}
