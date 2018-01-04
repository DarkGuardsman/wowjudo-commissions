package com.builtbroken.wowjudo.stats.network;

import com.builtbroken.mc.api.data.IPacket;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.wowjudo.SurvivalMod;
import com.builtbroken.wowjudo.stats.StatHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Handles syncing settings to the client in case they are different server side
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/4/2018.
 */
public class PacketStatSettings implements IPacket
{
    public float SPEED_SCALE;
    public int HEALTH_SCALE;
    public float DAMAGE_SCALE;
    public int FOOD_SCALE;
    public float ARMOR_DAMAGE_REDUCTION_SCALE;
    public int AIR_SCALE;

    public int SPEED_MAX;
    public int HEALTH_MAX;
    public int DAMAGE_MAX;
    public int FOOD_MAX;
    public int ARMOR_MAX;
    public int AIR_MAX;

    public boolean ENABLE_SPEED = true;
    public boolean ENABLE_HEALTH = true;
    public boolean ENABLE_DAMAGE = true;
    public boolean ENABLE_FOOD = true;
    public boolean ENABLE_DAMAGE_REDUCTION = true;
    public boolean ENABLE_AIR = true;

    public boolean KEEP_XP_ON_DEATH = true;

    public PacketStatSettings()
    {
        //Needed for forge to construct the packet
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
    {
        buffer.writeFloat(StatHandler.SPEED_SCALE);
        buffer.writeInt(StatHandler.HEALTH_SCALE);
        buffer.writeFloat(StatHandler.DAMAGE_SCALE);
        buffer.writeInt(StatHandler.FOOD_SCALE);
        buffer.writeFloat(StatHandler.ARMOR_DAMAGE_REDUCTION_SCALE);
        buffer.writeInt(StatHandler.AIR_SCALE);

        buffer.writeInt(StatHandler.SPEED_MAX);
        buffer.writeInt(StatHandler.HEALTH_MAX);
        buffer.writeInt(StatHandler.DAMAGE_MAX);
        buffer.writeInt(StatHandler.FOOD_MAX);
        buffer.writeInt(StatHandler.ARMOR_MAX);
        buffer.writeInt(StatHandler.AIR_MAX);

        buffer.writeBoolean(StatHandler.ENABLE_SPEED);
        buffer.writeBoolean(StatHandler.ENABLE_HEALTH);
        buffer.writeBoolean(StatHandler.ENABLE_DAMAGE);
        buffer.writeBoolean(StatHandler.ENABLE_FOOD);
        buffer.writeBoolean(StatHandler.ENABLE_DAMAGE_REDUCTION);
        buffer.writeBoolean(StatHandler.ENABLE_AIR);

        buffer.writeBoolean(StatHandler.KEEP_XP_ON_DEATH);
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
    {
        SPEED_SCALE = buffer.readFloat();
        HEALTH_SCALE = buffer.readInt();
        DAMAGE_SCALE = buffer.readFloat();
        FOOD_SCALE = buffer.readInt();
        ARMOR_DAMAGE_REDUCTION_SCALE = buffer.readFloat();
        AIR_SCALE = buffer.readInt();

        SPEED_MAX = buffer.readInt();
        HEALTH_MAX = buffer.readInt();
        DAMAGE_MAX = buffer.readInt();
        FOOD_MAX = buffer.readInt();
        ARMOR_MAX = buffer.readInt();
        AIR_MAX = buffer.readInt();

        ENABLE_SPEED  = buffer.readBoolean();
        ENABLE_HEALTH = buffer.readBoolean();
        ENABLE_DAMAGE = buffer.readBoolean();
        ENABLE_FOOD = buffer.readBoolean();
        ENABLE_DAMAGE_REDUCTION = buffer.readBoolean();
        ENABLE_AIR = buffer.readBoolean();

        KEEP_XP_ON_DEATH = buffer.readBoolean();
    }

    @Override
    public void handleClientSide(EntityPlayer player)
    {
        if (Engine.runningAsDev)
        {
            SurvivalMod.logger.info("Received packet for stat settings");
        }
        StatHandler.SPEED_SCALE = SPEED_SCALE;
        StatHandler.HEALTH_SCALE = HEALTH_SCALE ;
        StatHandler.DAMAGE_SCALE = DAMAGE_SCALE;
        StatHandler.FOOD_SCALE = FOOD_SCALE;
        StatHandler.ARMOR_DAMAGE_REDUCTION_SCALE = ARMOR_DAMAGE_REDUCTION_SCALE;
        StatHandler.AIR_SCALE = AIR_SCALE;

        StatHandler.SPEED_MAX = SPEED_MAX;
        StatHandler.HEALTH_MAX = HEALTH_MAX;
        StatHandler.DAMAGE_MAX = DAMAGE_MAX;
        StatHandler.FOOD_MAX = FOOD_MAX;
        StatHandler.ARMOR_MAX = ARMOR_MAX;
        StatHandler.AIR_MAX = AIR_MAX;

        StatHandler.ENABLE_SPEED  = ENABLE_SPEED;
        StatHandler.ENABLE_HEALTH = ENABLE_HEALTH;
        StatHandler.ENABLE_DAMAGE = ENABLE_DAMAGE;
        StatHandler.ENABLE_FOOD = ENABLE_FOOD;
        StatHandler.ENABLE_DAMAGE_REDUCTION = ENABLE_DAMAGE_REDUCTION;
        StatHandler.ENABLE_AIR = ENABLE_AIR;

        StatHandler.KEEP_XP_ON_DEATH = KEEP_XP_ON_DEATH;
    }

    @Override
    public void handleServerSide(EntityPlayer player)
    {
        //Client side only
    }
}
