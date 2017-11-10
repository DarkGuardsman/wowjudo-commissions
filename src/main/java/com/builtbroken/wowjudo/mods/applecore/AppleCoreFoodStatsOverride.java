package com.builtbroken.wowjudo.mods.applecore;

import com.builtbroken.wowjudo.stats.FoodStatOverride;
import net.minecraft.entity.player.EntityPlayer;
import squeek.applecore.api.food.FoodValues;
import squeek.applecore.asm.Hooks;

/**
 * Fixes apple core modifying the food stat to add extra events. Ensures those events are still fired while also
 * ensuring that we still have the ability to modify max food value.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/10/2017.
 */
public class AppleCoreFoodStatsOverride extends FoodStatOverride
{
    @Override
    public void addStats(int food, float sat)
    {
        if (!Hooks.fireFoodStatsAdditionEvent(getPlayer(), new FoodValues(food, sat)))
        {
            super.addStats(food, sat);
        }
    }

    public EntityPlayer getPlayer()
    {
        return AppleCoreModule.getPlayer(this);
    }
}
