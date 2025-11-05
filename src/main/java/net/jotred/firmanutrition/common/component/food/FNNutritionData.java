package net.jotred.firmanutrition.common.component.food;

import net.jotred.firmanutrition.config.FNServerConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.component.food.FoodData;
import net.dries007.tfc.common.component.food.Nutrient;
import net.dries007.tfc.common.player.IPlayerInfo;
import net.dries007.tfc.common.player.PlayerInfo;
import net.dries007.tfc.config.TFCConfig;

/**
 * A wrapper class for nutrition stats for a player
 * <p>
 * This only executes logic on server side, on client side it simply sets the lastAverageNutrients
 */
public class FNNutritionData implements IAdditionalNutritionData
{
    protected final float[] nutrients;
    protected final float[] decayRates;
    protected float averageNutrients;
    protected int hungerWindow;
    protected boolean doProportionalDecay;
    protected float proportionalDecayFloor;
    protected float maxNutrition;
    protected int hunger;
    protected boolean nonEmptyLastMeal;

    public FNNutritionData(float defaultNutritionValue, float defaultDairyNutritionValue)
    {
        this.nutrients = new float[5];
        this.decayRates = new float[5];
        this.averageNutrients = IPlayerInfo.DEFAULT_AVERAGE_NUTRITION;
        this.hungerWindow = 0;
        this.doProportionalDecay = true;
        this.proportionalDecayFloor = 0;
        this.maxNutrition = 0;
        this.hunger = PlayerInfo.MAX_HUNGER;
        this.nonEmptyLastMeal = true;

        for (Nutrient nutrient : Nutrient.VALUES)
        {
            nutrients[nutrient.ordinal()] = (nutrient == Nutrient.DAIRY) ? defaultDairyNutritionValue : defaultNutritionValue;
        }

        decayRates[Nutrient.GRAIN.ordinal()] = (float) FNServerConfig.defaultGrainDecayRate.getAsDouble();
        decayRates[Nutrient.FRUIT.ordinal()] = (float) FNServerConfig.defaultFruitDecayRate.getAsDouble();
        decayRates[Nutrient.VEGETABLES.ordinal()] = (float) FNServerConfig.defaultVegetablesDecayRate.getAsDouble();
        decayRates[Nutrient.PROTEIN.ordinal()] = (float) FNServerConfig.defaultProteinDecayRate.getAsDouble();
        decayRates[Nutrient.DAIRY.ordinal()] = (float) FNServerConfig.defaultDairyDecayRate.getAsDouble();
    }

    /**
     * @return The average of the nutrition values of the player
     */
    @Override
    public float getAverageNutrition()
    {
        return averageNutrients;
    }

    /**
     * @return The nutrient value, in [0, 1]
     */
    @Override
    public float getNutrient(Nutrient nutrient)
    {
        // We have to manually clamp the value to [0, 1], since it might be larger than 1 in this implementation
        // However, it cannot be negative, so we only have to use Math.min
        return Math.min(nutrients[nutrient.ordinal()], 1);
    }

    /**
     * @return An array of all nutrient values, each in [0, 1]
     */
    @Override
    public float[] getNutrients()
    {
        float[] cappedNutrients = new float[5];

        for (Nutrient nutrient : Nutrient.VALUES)
        {
            cappedNutrients[nutrient.ordinal()] = getNutrient(nutrient);
        }

        return cappedNutrients;
    }

    /**
     * Sets the current {@code hunger} value of the player, in {@code [0, PlayerInfo.MAX_HUNGER]}.
     * This may update the nutrition of the player.
     */
    @Override
    public void setHungerAndUpdate(int hunger)
    {
        // If the new hunger value is lower than the old hunger value, then the nutrients of the player need to be reduced according to the amount of hunger ticks that passed
        if (hunger < this.hunger)
        {
            reduceAllNutrients(this.hunger - hunger);
        }
        this.hunger = hunger;
    }

    /**
     * Sets the current {@code hunger} value of the player, in {@code [0, PlayerInfo.MAX_HUNGER]}.
     * This <strong>must not</strong> update the nutrition of the player.
     */
    @Override
    public void setHunger(int hunger)
    {
        this.hunger = hunger;
    }

    /**
     * Sets data from a packet, received on client side. Only contains the array of nutrient values of the player, since only those are needed for the client
     */
    @Override
    public void onClientUpdate(float[] nutrients)
    {
        System.arraycopy(nutrients, 0, this.nutrients, 0, this.nutrients.length);
        updateAverageNutrients();
    }

    /**
     * Applies nutrients of the food data to the player, and incorporates the current hunger level of the player
     * If the last meal you ate had hunger, and this one didn't have hunger, we will apply the meal
     * Use case: Milk drinking. We add milk as a meal if and only if you just ate something
     *
     * @param data The {@link FoodData} of the eaten food
     * @param currentHunger The food level of the player at time of eating
     */
    @Override
    public void addNutrients(FoodData data, int currentHunger)
    {
        if (data.hunger() > 0 || nonEmptyLastMeal)
        {
            // Reload from config
            this.hungerWindow = TFCConfig.SERVER.nutritionRotationHungerWindow.get();
            this.maxNutrition = (float) FNServerConfig.maximumNutrition.getAsDouble();
            int filledHunger = data.hunger() == 0 ? 4 : Math.min(PlayerInfo.MAX_HUNGER - currentHunger, data.hunger());

            // Add nutrients from the food to the player, scaled proportionally to the amount of hunger it actually filled, and capped to maxNutrition
            // We don't just cap the value to 1, since that would make it impossible to stay at 100% average nutrition
            for (Nutrient nutrient : Nutrient.VALUES)
            {
                setNutrient(nutrient, Math.min(nutrients[nutrient.ordinal()] + filledHunger * data.nutrient(nutrient) / hungerWindow, maxNutrition));
            }

            nonEmptyLastMeal = data.hunger() > 0;
            updateAverageNutrients();
        }
    }

    /**
     * @return The relevant data for computing nutrition values written to an NBT Tag
     */
    @Override
    public Tag writeToNbt()
    {
        CompoundTag nbt = new CompoundTag();

        nbt.putFloat("grain", nutrients[Nutrient.GRAIN.ordinal()]);
        nbt.putFloat("fruit", nutrients[Nutrient.FRUIT.ordinal()]);
        nbt.putFloat("vegetables", nutrients[Nutrient.VEGETABLES.ordinal()]);
        nbt.putFloat("protein", nutrients[Nutrient.PROTEIN.ordinal()]);
        nbt.putFloat("dairy", nutrients[Nutrient.DAIRY.ordinal()]);

        nbt.putBoolean("nonEmptyLastMeal", nonEmptyLastMeal);

        nbt.putFloat("grainDecay", decayRates[Nutrient.GRAIN.ordinal()]);
        nbt.putFloat("fruitDecay", decayRates[Nutrient.FRUIT.ordinal()]);
        nbt.putFloat("vegetablesDecay", decayRates[Nutrient.VEGETABLES.ordinal()]);
        nbt.putFloat("proteinDecay", decayRates[Nutrient.PROTEIN.ordinal()]);
        nbt.putFloat("dairyDecay", decayRates[Nutrient.DAIRY.ordinal()]);

        return nbt;
    }

    /**
     * Reads relevant data for computing nutrition values from an NBT Tag
     */
    @Override
    public void readFromNbt(@Nullable Tag tag)
    {
        if (tag == null)
        {
            return;
        }

        // Note: This condition will always return true, unless the player data is corrupted or if the player data was saved without this mod installed
        // If it is false, we do not overwrite the default values from the constructor!
        if (tag instanceof CompoundTag nbt)
        {
            nutrients[Nutrient.GRAIN.ordinal()] = nbt.getFloat("grain");
            nutrients[Nutrient.FRUIT.ordinal()] = nbt.getFloat("fruit");
            nutrients[Nutrient.VEGETABLES.ordinal()] = nbt.getFloat("vegetables");
            nutrients[Nutrient.PROTEIN.ordinal()] = nbt.getFloat("protein");
            nutrients[Nutrient.DAIRY.ordinal()] = nbt.getFloat("dairy");

            nonEmptyLastMeal = nbt.getBoolean("nonEmptyLastMeal");

            if (FNServerConfig.forceCustomDecayRates.getAsBoolean())
            {
                decayRates[Nutrient.GRAIN.ordinal()] = (float) FNServerConfig.defaultGrainDecayRate.getAsDouble();
                decayRates[Nutrient.FRUIT.ordinal()] = (float) FNServerConfig.defaultFruitDecayRate.getAsDouble();
                decayRates[Nutrient.VEGETABLES.ordinal()] = (float) FNServerConfig.defaultVegetablesDecayRate.getAsDouble();
                decayRates[Nutrient.PROTEIN.ordinal()] = (float) FNServerConfig.defaultProteinDecayRate.getAsDouble();
                decayRates[Nutrient.DAIRY.ordinal()] = (float) FNServerConfig.defaultDairyDecayRate.getAsDouble();
            }
            else
            {
                decayRates[Nutrient.GRAIN.ordinal()] = nbt.getFloat("grainDecay");
                decayRates[Nutrient.FRUIT.ordinal()] = nbt.getFloat("fruitDecay");
                decayRates[Nutrient.VEGETABLES.ordinal()] = nbt.getFloat("vegetablesDecay");
                decayRates[Nutrient.PROTEIN.ordinal()] = nbt.getFloat("proteinDecay");
                decayRates[Nutrient.DAIRY.ordinal()] = nbt.getFloat("dairyDecay");
            }
        }

        updateAverageNutrients();
    }

    protected void updateAverageNutrients()
    {
        averageNutrients = 0;

        // We take the average of the clamped nutrient values, not the actually stored values, to ensure the result is in [0, 1]
        for (Nutrient nutrient : Nutrient.VALUES)
        {
            averageNutrients += getNutrient(nutrient);
        }

        averageNutrients /= Nutrient.TOTAL;
    }

    /**
     * Reduce the amount of stored nutrients of the player by the amount of hunger ticks the player experienced
     * Each hunger tick is equivalent to 1/4 of the hunger filled by any food item
     *
     * @param hungerTicks amount of hunger ticks since the last call of this function
     */
    protected void reduceAllNutrients(int hungerTicks)
    {
        if (hungerTicks <= 0) return;

        // Reload from config
        this.hungerWindow = TFCConfig.SERVER.nutritionRotationHungerWindow.get();
        this.doProportionalDecay = FNServerConfig.proportionalNutritionDecay.getAsBoolean();

        if (doProportionalDecay)
        {
            this.proportionalDecayFloor = (float) FNServerConfig.proportionalDecayFloor.getAsDouble();

            for (Nutrient nutrient : Nutrient.VALUES)
            {
                // We need this inner loop to ensure that setting hunger via command correctly applies the decay as if it happened naturally
                // Furthermore, we use the capped nutrients for this calculation, so `proportionalDecayFloor` has effectively a "full" range
                for (int i = 0; i < hungerTicks; i++)
                {
                    nutrients[nutrient.ordinal()] = Math.max(nutrients[nutrient.ordinal()] - Math.max(getNutrient(nutrient), proportionalDecayFloor) * decayRates[nutrient.ordinal()] / hungerWindow, 0);
                }
            }
        }
        else
        {
            for (Nutrient nutrient : Nutrient.VALUES)
            {
                // With non-proportional decay we can just multiply the decay rate with the amount of hunger ticks, instead of having to use a loop
                nutrients[nutrient.ordinal()] = Math.max(nutrients[nutrient.ordinal()] - hungerTicks * decayRates[nutrient.ordinal()] / hungerWindow, 0);
            }
        }
        updateAverageNutrients();
    }

    /**
     * @return The decay rate value of the given nutrient, non-negative
     */
    @Override
    public float getDecayRate(Nutrient nutrient)
    {
        return decayRates[nutrient.ordinal()];
    }

    /**
     * @return The array of the decay rate value of all nutrients, each non-negative
     */
    @Override
    public float[] getDecayRates()
    {
        return decayRates.clone();
    }

    /**
     * Sets the decay rate value of the given nutrient to any non-negative value
     */
    @Override
    public void setDecayRate(Nutrient nutrient, float rate)
    {
        decayRates[nutrient.ordinal()] = Math.max(rate, 0);
    }

    /**
     * Sets the current {@code nutrient} value of the player, up to the maximum nutrition value
     */
    @Override
    public void setNutrient(Nutrient nutrient, float amount)
    {
        this.maxNutrition = (float) FNServerConfig.maximumNutrition.getAsDouble();
        nutrients[nutrient.ordinal()] = Math.clamp(amount, 0, maxNutrition);
        updateAverageNutrients();
    }

    /**
     * Adds to the current {@code nutrient} value of the player, up to the maximum nutrition value
     */
    @Override
    public void addNutrient(Nutrient nutrient, float amount)
    {
        setNutrient(nutrient, nutrients[nutrient.ordinal()] + amount);
    }
}