package com.builtbroken.wowjudo.stats;

import com.builtbroken.wowjudo.SurvivalMod;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingEvent;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/9/2017.
 */
public class StatHandler
{
    public static final String PROPERTY_ID = SurvivalMod.PREFX + "stats";

    //configs
    public static float SPEED_SCALE = 0.05f;
    public static int HEALTH_SCALE = 1;
    public static float DAMAGE_SCALE = 1f;
    public static float FOOD_SCALE = 1f;
    public static int ARMOR_SCALE = 1;
    public static int AIR_SCALE = 1;

    public static int SPEED_MAX = 10;
    public static int HEALTH_MAX = 10;
    public static int DAMAGE_MAX = 10;
    public static int FOOD_MAX = 10;
    public static int ARMOR_MAX = 10;
    public static int AIR_MAX = 10;

    public static StatHandler INSTANCE = new StatHandler();

    public static StatEntityProperty getPropertyForEntity(EntityPlayer entity)
    {
        IExtendedEntityProperties prop = entity.getExtendedProperties(PROPERTY_ID);
        if (prop instanceof StatEntityProperty)
        {
            return (StatEntityProperty) prop;
        }
        return null;
    }

    @SubscribeEvent
    public void livingUpdateEvent(LivingEvent.LivingUpdateEvent event)
    {
        if(event.entity instanceof EntityPlayer)
        {
            StatEntityProperty property = StatHandler.getPropertyForEntity((EntityPlayer) event.entity);
            if (property != null)
            {
                property.update();
            }
        }
    }

    @SubscribeEvent
    public void livingCreationEvent(EntityEvent.EntityConstructing event)
    {
        if (event.entity instanceof EntityPlayer)
        {
            event.entity.registerExtendedProperties(PROPERTY_ID, new StatEntityProperty());
        }
    }
}
