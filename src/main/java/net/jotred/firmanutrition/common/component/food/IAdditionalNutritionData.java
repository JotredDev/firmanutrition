package net.jotred.firmanutrition.common.component.food;

import net.dries007.tfc.common.component.food.INutritionData;
import net.dries007.tfc.common.component.food.Nutrient;

public interface IAdditionalNutritionData extends INutritionData
{
    /**
     * @return The decay rate value of the given nutrient, non-negative
     */
    float getDecayRate(Nutrient nutrient);

    /**
     * @return The array of the decay rate value of all nutrients, each non-negative
     */
    float[] getDecayRates();

    /**
     * Sets the decay rate value of the given nutrient, non-negative
     */
    void setDecayRate(Nutrient nutrient, float rate);

    /**
     * Sets the decay rate values of all nutrients, each non-negative
     */
    default void setAllDecayRates(float rate)
    {
        for (Nutrient nutrient : Nutrient.VALUES)
        {
            setDecayRate(nutrient, rate);
        }
    }

    /**
     * Adds to the decay rate value of the given nutrient
     */
    default void addDecayRate(Nutrient nutrient, float rate)
    {
        setDecayRate(nutrient, getDecayRate(nutrient) + rate);
    }

    /**
     * Adds to all decay rate values of all nutrients
     */
    default void addAllDecayRates(float rate)
    {
        for (Nutrient nutrient : Nutrient.VALUES)
        {
            setDecayRate(nutrient, getDecayRate(nutrient) + rate);
        }
    }

    /**
     * Sets the current {@code nutrient} value of the player
     */
    void setNutrient(Nutrient nutrient, float amount);

    /**
     * Sets the current {@code nutrient} values of all nutrients of the player
     */
    default void setAllNutrients(float amount)
    {
        for (Nutrient nutrient : Nutrient.VALUES)
        {
            setNutrient(nutrient, amount);
        }
    }

    /**
     * Adds to the current {@code nutrient} value of the player
     */
    void addNutrient(Nutrient nutrient, float amount);

    /**
     * Adds to all current {@code nutrient} values of the player
     */
    default void addAllNutrients(float amount)
    {
        for (Nutrient nutrient : Nutrient.VALUES)
        {
            addNutrient(nutrient, amount);
        }
    }
}
