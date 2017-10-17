package com.builtbroken.wowjudo.stats;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.FoodStats;
import net.minecraft.world.EnumDifficulty;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/17/2017.
 */
public class FoodStatOverride extends FoodStats
{
    public static final int MAX_FOOD_DEFAULT = 20;
    public static final int FOOD_TIMER_TRIGGER = 80;
    public static final float MAX_EXHAUSTION = 40f;

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

    public int getFoodNeededToHeal()
    {
        return foodNeededToHeal;
    }

    @Override
    public void onUpdate(EntityPlayer player)
    {
        EnumDifficulty enumdifficulty = player.worldObj.difficultySetting;
        this.prevFoodLevel = this.foodLevel;

        if (this.foodExhaustionLevel > 4.0F)
        {
            this.foodExhaustionLevel -= 4.0F;

            if (this.foodSaturationLevel > 0.0F)
            {
                this.foodSaturationLevel = Math.max(this.foodSaturationLevel - 1.0F, 0.0F);
            }
            else if (enumdifficulty != EnumDifficulty.PEACEFUL)
            {
                this.foodLevel = Math.max(this.foodLevel - 1, 0);
            }
        }

        if (player.worldObj.getGameRules().getGameRuleBooleanValue("naturalRegeneration") && this.foodLevel >= getFoodNeededToHeal() && player.shouldHeal())
        {
            ++this.foodTimer;

            if (this.foodTimer >= 80)
            {
                player.heal(1.0F);
                this.addExhaustion(3.0F);
                this.foodTimer = 0;
            }
        }
        else if (this.foodLevel <= 0)
        {
            ++this.foodTimer;

            if (this.foodTimer >= FOOD_TIMER_TRIGGER)
            {
                if (player.getHealth() > 10.0F || enumdifficulty == EnumDifficulty.HARD || player.getHealth() > 1.0F && enumdifficulty == EnumDifficulty.NORMAL)
                {
                    player.attackEntityFrom(DamageSource.starve, 1.0F);
                }

                this.foodTimer = 0;
            }
        }
        else
        {
            this.foodTimer = 0;
        }
    }

    @Override
    public boolean needFood()
    {
        return this.foodLevel < getMaxFoodLevel();
    }


    @Override
    public void addExhaustion(float ex)
    {
        this.foodExhaustionLevel = Math.min(this.foodExhaustionLevel + ex, MAX_EXHAUSTION);
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
