package com.builtbroken.wowjudo.stats;

import com.builtbroken.mc.core.Engine;
import com.builtbroken.wowjudo.SurvivalMod;
import com.builtbroken.wowjudo.stats.network.PacketStatSettings;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.FoodStats;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Function;

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
    public static float ARMOR_DAMAGE_REDUCTION_SCALE = .01f;
    public static int AIR_SCALE = 20;

    public static int SPEED_MAX = 10;
    public static int HEALTH_MAX = 10;
    public static int DAMAGE_MAX = 10;
    public static int FOOD_MAX = 10;
    public static int ARMOR_MAX = 10;
    public static int AIR_MAX = 10;

    public static boolean ENABLE_SPEED = true;
    public static boolean ENABLE_HEALTH = true;
    public static boolean ENABLE_DAMAGE = true;
    public static boolean ENABLE_FOOD = true;
    public static boolean ENABLE_DAMAGE_REDUCTION = true;
    public static boolean ENABLE_AIR = true;

    public static boolean KEEP_XP_ON_DEATH = true;

    public static StatHandler INSTANCE = new StatHandler();

    /** Cache of XP from the player, used to store XP between respawns */
    HashMap<UUID, XpCacheObject> xpCache = new HashMap();
    /** Cache of stats from the player, used to store values between respawns */
    HashMap<UUID, StatEntityProperty> propCache = new HashMap();

    public static Function<EntityPlayer, FoodStats> foodStatsFactory = e -> new FoodStatOverride();

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
    public void onConnect(PlayerEvent.PlayerLoggedInEvent event)
    {
        if (!event.player.worldObj.isRemote && event.player instanceof EntityPlayerMP)
        {
            SurvivalMod.instance.logger().info("Sending stat settings packet to " + event.player.getCommandSenderName());
            Engine.packetHandler.sendToPlayer(new PacketStatSettings(), (EntityPlayerMP) event.player);
        }
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
        if (ENABLE_DAMAGE_REDUCTION && event.entity instanceof EntityPlayer
                && !event.entity.isEntityInvulnerable()
                && !event.source.isDamageAbsolute()
                && !event.source.isUnblockable())
        {
            EntityPlayer player = (EntityPlayer) event.entity;
            StatEntityProperty property = StatHandler.getPropertyForEntity(player);
            if (property != null)
            {
                int armor = property.getArmorIncrease();
                if (armor > 0)
                {
                    float damageReduction = armor * ARMOR_DAMAGE_REDUCTION_SCALE;
                    event.ammount -= event.ammount * damageReduction;
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

            if (KEEP_XP_ON_DEATH)
            {
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
            }

            //cache property
            propCache.put(player.getGameProfile().getId(), getPropertyForEntity(player));
        }
    }

    @SubscribeEvent
    public void playerRespawnEvent(PlayerEvent.PlayerRespawnEvent event)
    {
        EntityPlayer player = event.player;
        UUID uuid = player.getGameProfile().getId();

        //Restore XP
        if (KEEP_XP_ON_DEATH && xpCache.containsKey(uuid))
        {
            XpCacheObject object = xpCache.get(uuid);
            event.player.experience = object.xp;
            event.player.experienceLevel = object.level;
            event.player.experienceTotal = object.totalXp;
            xpCache.remove(uuid);
        }

        //Restore stats
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
        if (ENABLE_FOOD && event.entity instanceof EntityPlayer)
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

    //@SubscribeEvent
    public void interactionEvent(PlayerInteractEvent event)
    {
        //Could use this to handle food increase by adding extra food to StatEntityProperty
    }

    public static boolean overrideFoodStats(EntityPlayer player)
    {
        if (ENABLE_FOOD)
        {
            FoodStats old = player.foodStats;
            if (old == null || old.getClass() == FoodStats.class)
            {
                //Save data
                NBTTagCompound tag = new NBTTagCompound();
                if (old != null)
                {
                    old.writeNBT(tag);
                }

                //Create
                FoodStats override = foodStatsFactory.apply(player);

                //Load data
                override.readNBT(tag);

                //Assign
                player.foodStats = override;
                return true;
            }
        }
        return false;
    }

    public static void loadConfig(Configuration config)
    {
        SPEED_SCALE = config.getFloat("speed", "stat_scale",
                SPEED_SCALE, 0, 1, "Multiplier for movement speed bonus");
        HEALTH_SCALE = config.getInt("health", "stat_scale",
                HEALTH_SCALE, 0, Short.MAX_VALUE, "Multiplier for health bonus");
        DAMAGE_SCALE = config.getFloat("damage", "stat_scale",
                DAMAGE_SCALE, 0, Short.MAX_VALUE, "Multiplier for damage bonus");
        FOOD_SCALE = config.getInt("food", "stat_scale",
                FOOD_SCALE, 0, Integer.MAX_VALUE / 2, "Multiplier for food bonus");
        ARMOR_DAMAGE_REDUCTION_SCALE = config.getFloat("damage_reduction", "stat_scale",
                ARMOR_DAMAGE_REDUCTION_SCALE, 0, 1, "Multiplier for damage reduction, do not set too high as a (multiplier * level == 1 will result in zero damage)");
        AIR_SCALE = config.getInt("air", "stat_scale",
                AIR_SCALE, 0, Integer.MAX_VALUE / 2, "Multiplier for air bonus in ticks (20 ticks a second)");

        SPEED_MAX = config.getInt("speed", "stat_max_level",
                SPEED_MAX, 0, Integer.MAX_VALUE / 2, "Max level");
        HEALTH_MAX = config.getInt("health", "stat_max_level",
                SPEED_MAX, 0, Integer.MAX_VALUE / 2, "Max level");
        DAMAGE_MAX = config.getInt("damage", "stat_max_level",
                DAMAGE_MAX, 0, Integer.MAX_VALUE / 2, "Max level");
        FOOD_MAX = config.getInt("food", "stat_max_level",
                FOOD_MAX, 0, Integer.MAX_VALUE / 2, "Max level");
        ARMOR_MAX = config.getInt("damage_reduction", "stat_max_level",
                ARMOR_MAX, 0, Integer.MAX_VALUE / 2, "Max level");
        AIR_MAX = config.getInt("air", "stat_max_level",
                AIR_MAX, 0, Integer.MAX_VALUE / 2, "Max level");

        ENABLE_SPEED = config.getBoolean("speed", "stat_enable",
                ENABLE_SPEED, "Enables the component of the stat system, set to false to disable.");
        ENABLE_HEALTH = config.getBoolean("health", "stat_enable",
                ENABLE_HEALTH, "Enables the component of the stat system, set to false to disable.");
        ENABLE_DAMAGE = config.getBoolean("damage", "stat_enable",
                ENABLE_DAMAGE, "Enables the component of the stat system, set to false to disable.");
        ENABLE_FOOD = config.getBoolean("food", "stat_enable",
                ENABLE_FOOD, "Enables the component of the stat system, set to false to disable.");
        ENABLE_DAMAGE_REDUCTION = config.getBoolean("damage_reduction", "stat_enable",
                ENABLE_DAMAGE_REDUCTION, "Enables the component of the stat system, set to false to disable.");
        ENABLE_AIR = config.getBoolean("air", "stat_enable",
                ENABLE_AIR, "Enables the component of the stat system, set to false to disable.");

        KEEP_XP_ON_DEATH = !config.getBoolean("xp_drop", "stat_enable",
                !KEEP_XP_ON_DEATH, "Allow XP to be dropped on death, disabled by default to keep levels.");
    }


    public static void loadSettings(ByteBuf buf)
    {

    }


    protected static final class XpCacheObject
    {
        public float xp;
        public int totalXp;
        public int level;
    }
}
