package com.builtbroken.wowjudo.stats;

import net.minecraft.util.FoodStats;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/17/2017.
 */
public class FoodStatOverride extends FoodStats
{
    public static final int MAX_FOOD_DEFAULT = 20;

    public int maxFoodLevel = MAX_FOOD_DEFAULT;
    public int foodNeededToHeal = 18;

    @Override
    public void addStats(int food, float sat)
    {
        this.foodLevel = Math.min(food + this.foodLevel, getMaxFoodLevel());
        this.foodSaturationLevel = Math.min(this.foodSaturationLevel + (float) food * sat * 2.0F, (float) this.foodLevel);
    }

    public void setMaxFoodLevel(int level)
    {
        this.maxFoodLevel = level;
        this.foodNeededToHeal = (int) Math.floor(maxFoodLevel * 0.8);
    }

    public int getMaxFoodLevel()
    {
        return maxFoodLevel;
    }

    @Override
    public boolean needFood()
    {
        return this.foodLevel < getMaxFoodLevel();
    }

    @Override
    public int getFoodLevel()
    {
        return this.foodLevel;
    }

    @Override
    public void setFoodLevel(int level)
    {
        this.foodLevel = level;
    }

    @Override
    public float getSaturationLevel()
    {
        return this.foodSaturationLevel;
    }

    @Override
    public void setFoodSaturationLevel(float level)
    {
        this.foodSaturationLevel = level;
    }
}
