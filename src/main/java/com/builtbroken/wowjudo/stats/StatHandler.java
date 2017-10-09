package com.builtbroken.wowjudo.stats;

import com.builtbroken.wowjudo.SurvivalMod;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
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

    public static StatHandler INSTANCE = new StatHandler();

    public static StatEntityProperty getPropertyForEntity(Entity entity)
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
        StatEntityProperty property = StatHandler.getPropertyForEntity(event.entity);
        if (property != null)
        {
            property.update();
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
