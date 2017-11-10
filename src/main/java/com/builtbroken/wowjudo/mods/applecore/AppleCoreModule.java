package com.builtbroken.wowjudo.mods.applecore;

import com.builtbroken.mc.framework.mod.ModProxy;
import com.builtbroken.mc.framework.mod.Mods;
import com.builtbroken.wowjudo.stats.StatHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.FoodStats;

import java.lang.reflect.Field;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/10/2017.
 */
public class AppleCoreModule extends ModProxy
{
    public static Field applCore_playerField;

    public AppleCoreModule()
    {
        super(Mods.APPLE_CORE);
    }

    @Override
    public void preInit()
    {
        StatHandler.foodStatsFactory = player ->
        {
            AppleCoreFoodStatsOverride override = new AppleCoreFoodStatsOverride();
            setPlayer(override, player);
            return override;
        };
    }

    public static void setPlayer(FoodStats override, EntityPlayer player)
    {
        //Fix for apple core modifying food stat TODO write code that auto reflects and moves values
        try
        {
            checkField();
            applCore_playerField.set(override, player);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static EntityPlayer getPlayer(FoodStats override)
    {
        try
        {
            checkField();
            return (EntityPlayer) applCore_playerField.get(override);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static void checkField() throws NoSuchFieldException
    {
        if (applCore_playerField == null)
        {
            applCore_playerField = FoodStats.class.getDeclaredField("entityplayer");
            applCore_playerField.setAccessible(true);
        }
    }
}
