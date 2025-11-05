package net.jotred.firmanutrition;

import net.jotred.firmanutrition.config.FNServerConfig;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;

@Mod(FirmaNutrition.MOD_ID)
public class FirmaNutrition {

    public static final String MOD_ID = "firmanutrition";
    public static final String MOD_NAME = "FirmaNutrition";
    public static final Logger LOGGER = LogUtils.getLogger();

    public FirmaNutrition(IEventBus bus, ModContainer mod)
    {
        mod.registerConfig(ModConfig.Type.SERVER, FNServerConfig.SPEC);
        ForgeEventHandler.init();
    }
}
