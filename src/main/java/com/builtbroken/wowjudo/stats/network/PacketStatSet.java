package com.builtbroken.wowjudo.stats.network;

import com.builtbroken.mc.api.data.IPacket;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.wowjudo.SurvivalMod;
import com.builtbroken.wowjudo.stats.StatEntityProperty;
import com.builtbroken.wowjudo.stats.StatHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Handles syncing stat changes for an entity
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/10/2017.
 */
public class PacketStatSet implements IPacket
{
    public static final int CLEAR = -1;
    public static final int HEALTH = 0;
    public static final int SPEED = 1;
    public static final int DAMAGE = 2;
    public static final int FOOD = 3;
    public static final int ARMOR = 4;
    public static final int AIR = 5;

    protected int entityId;
    protected int id;
    protected int value;

    public PacketStatSet()
    {
        //Needed for forge to construct the packet
    }

    public PacketStatSet(Entity entity, int id, int value)
    {
        this.entityId = entity.getEntityId();
        this.id = id;
        this.value = value;
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
    {
        buffer.writeInt(this.entityId);
        buffer.writeInt(this.id);
        buffer.writeInt(this.value);
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
    {
        this.entityId = buffer.readInt();
        this.id = buffer.readInt();
        this.value = buffer.readInt();
    }

    @Override
    public void handleClientSide(EntityPlayer player)
    {
        handleServerSide(player);
    }

    @Override
    public void handleServerSide(EntityPlayer player)
    {
        Entity entity = player.getEntityWorld().getEntityByID(this.entityId);
        if (entity instanceof EntityPlayer)
        {
            StatEntityProperty property = StatHandler.getPropertyForEntity((EntityPlayer) entity);
            if (property != null)
            {
                switch (id)
                {
                    case CLEAR:
                        property.reset();
                        break;
                    case HEALTH:
                        property.setHpIncrease(value);
                        break;
                    case SPEED:
                        property.setSpeedIncrease(value);
                        break;
                    case DAMAGE:
                        property.setMeleeDamageIncrease(value);
                        break;
                    case FOOD:
                        property.setFoodAmountIncrease(value);
                        break;
                    case ARMOR:
                        property.setArmorIncrease(value);
                        break;
                    case AIR:
                        property.setAirIncrease(value);
                        break;
                    default:
                        if (Engine.runningAsDev)
                        {
                            SurvivalMod.logger.error((player.worldObj.isRemote ? "Client" : "Server") + ": Received a packet from " + player.getCommandSenderName() + " to update " + entity.getCommandSenderName() + " for stat update with id[" + id
                                    + "] and value of " + value + " that doesn't match a stat!");
                        }
                }
            }
            else if (Engine.runningAsDev)
            {
                SurvivalMod.logger.error((player.worldObj.isRemote ? "Client" : "Server") + ": Received a packet from " + player.getCommandSenderName() + " to update " + entity.getCommandSenderName() + " for stat update with id[" + id
                        + "] and value of " + value + " but entity doesn't have a stat provider!");
            }
        }
        else if (Engine.runningAsDev)
        {
            SurvivalMod.logger.error((player.worldObj.isRemote ? "Client" : "Server") + ": Received a packet from " + player.getCommandSenderName() + " for stat update with id[" + id
                    + "] and value of " + value + " but entity with id[" + entityId + "] doesn't exist!");
        }
    }
}
