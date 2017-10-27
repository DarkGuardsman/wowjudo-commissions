package com.builtbroken.wowjudo.stats;

import com.builtbroken.wowjudo.SurvivalMod;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.FoodStats;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.HashMap;
import java.util.UUID;

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
    public static int FOOD_SCALE = 1;
    public static int ARMOR_SCALE = 1;
    public static int AIR_SCALE = 1;

    public static int SPEED_MAX = 10;
    public static int HEALTH_MAX = 10;
    public static int DAMAGE_MAX = 10;
    public static int FOOD_MAX = 10;
    public static int ARMOR_MAX = 10;
    public static int AIR_MAX = 10;

    public static StatHandler INSTANCE = new StatHandler();

    HashMap<UUID, XpCacheObject> xpCache = new HashMap();
    HashMap<UUID, StatEntityProperty> propCache = new HashMap();

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
        if (event.entity instanceof EntityPlayer)
        {
            StatEntityProperty property = StatHandler.getPropertyForEntity((EntityPlayer) event.entity);
            if (property != null)
            {
                property.update();
            }
        }
    }

    @SubscribeEvent
    public void livingDamageEvent(LivingHurtEvent event)
    {
        if (event.entity instanceof EntityPlayer
                && !event.entity.isEntityInvulnerable()
                && !event.source.isDamageAbsolute()
                && !event.source.isUnblockable())
        {
            EntityPlayer player = (EntityPlayer) event.entity;
            StatEntityProperty property = StatHandler.getPropertyForEntity(player);
            if (property != null)
            {
                int armor = property.getArmorIncrease() * ARMOR_SCALE;
                if (armor > 0)
                {
                    int reductionRatio = 25 - armor; //TODO add config to scale, TODO remove magic number 25
                    event.ammount = event.ammount * (float) reductionRatio;
                }
            }
        }
    }

    @SubscribeEvent
    public void livingDeathEvent(LivingDeathEvent event)
    {
        if (event.entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) event.entity;

            //Collect xp
            XpCacheObject object = new XpCacheObject();
            object.level = player.experienceLevel;
            object.totalXp = player.experienceTotal;
            object.xp = player.experience;
            xpCache.put(player.getGameProfile().getId(), object);

            //Clear xp
            player.experienceTotal = 0;
            player.experience = 0;
            player.experienceLevel = 0;

            //cache property
            propCache.put(player.getGameProfile().getId(), getPropertyForEntity(player));
        }
    }

    @SubscribeEvent
    public void playerRespawnEvent(PlayerEvent.PlayerRespawnEvent event)
    {
        EntityPlayer player = event.player;
        UUID uuid = player.getGameProfile().getId();
        if (xpCache.containsKey(uuid))
        {
            XpCacheObject object = xpCache.get(uuid);
            event.player.experience = object.xp;
            event.player.experienceLevel = object.level;
            event.player.experienceTotal = object.totalXp;
            xpCache.remove(uuid);
        }

        if (propCache.containsKey(uuid))
        {
            StatEntityProperty property = propCache.get(uuid);
            propCache.remove(uuid);

            StatEntityProperty newProperty = getPropertyForEntity(event.player);
            newProperty.copyData(property);
        }
    }

    @SubscribeEvent
    public void livingCreationEvent(EntityEvent.EntityConstructing event)
    {
        if (event.entity instanceof EntityPlayer)
        {
            event.entity.registerExtendedProperties(PROPERTY_ID, new StatEntityProperty());
            if (!overrideFoodStats((EntityPlayer) event.entity))
            {
                FoodStats old = ((EntityPlayer) event.entity).foodStats;
                SurvivalMod.logger.error("Failed to replace Player:'" + ((EntityPlayer) event.entity).getCommandSenderName() + "' FoodStats object, " +
                        "this will prevent the stat system from changing the player's max food." +
                        "Report this issue to the developer with the following information. " +
                        "FoodStat = " + old + " Class = " + old.getClass());
            }
        }
    }

    public static boolean overrideFoodStats(EntityPlayer player)
    {
        FoodStats old = player.foodStats;
        if (old == null || old.getClass() == FoodStats.class)
        {
            NBTTagCompound tag = new NBTTagCompound();
            if (old != null)
            {
                old.writeNBT(tag);
            }

            FoodStatOverride override = new FoodStatOverride();
            override.readNBT(tag);

            player.foodStats = override;
            return true;
        }
        return false;
    }



    protected static final class XpCacheObject
    {
        public float xp;
        public int totalXp;
        public int level;
    }
}
