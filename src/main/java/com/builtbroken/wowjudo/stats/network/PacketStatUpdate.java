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
public class PacketStatUpdate implements IPacket
{
    protected int entityId;
    private int hpIncrease = 0;
    private int speedIncrease = 0;
    private int meleeDamage = 0;
    private int foodAmount = 0;
    private int armorIncrease = 0;
    private int airIncrease = 0;

    public PacketStatUpdate()
    {
        //Needed for forge to construct the packet
    }

    public PacketStatUpdate(EntityPlayer player)
    {
        this.entityId = player.getEntityId();
        StatEntityProperty property = StatHandler.getPropertyForEntity(player);
        if (property != null)
        {
            hpIncrease = property.getHpIncrease();
            speedIncrease = property.getSpeedIncrease();
            meleeDamage = property.getMeleeDamageIncrease();
            foodAmount = property.getFoodAmountIncrease();
            armorIncrease = property.getArmorIncrease();
            armorIncrease = property.getAirIncrease();
        }
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
    {
        buffer.writeInt(entityId);

        buffer.writeInt(hpIncrease);
        buffer.writeInt(speedIncrease);
        buffer.writeInt(meleeDamage);
        buffer.writeInt(foodAmount);
        buffer.writeInt(armorIncrease);
        buffer.writeInt(airIncrease);
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
    {
        entityId = buffer.readInt();

        hpIncrease = buffer.readInt();
        speedIncrease = buffer.readInt();
        meleeDamage = buffer.readInt();
        foodAmount = buffer.readInt();
        armorIncrease = buffer.readInt();
        airIncrease = buffer.readInt();
    }

    @Override
    public void handleClientSide(EntityPlayer player)
    {
        Entity entity = player.getEntityWorld().getEntityByID(this.entityId);

        if (Engine.runningAsDev)
        {
            SurvivalMod.logger.info("PacketStatRequest: " + entity);
        }

        if (entity instanceof EntityPlayer)
        {
            StatEntityProperty property = StatHandler.getPropertyForEntity((EntityPlayer) entity);
            if (property != null)
            {
                property.setHpIncrease(hpIncrease);
                property.setSpeedIncrease(speedIncrease);
                property.setMeleeDamageIncrease(meleeDamage);
                property.setFoodAmountIncrease(foodAmount);
                property.setArmorIncrease(armorIncrease);
                property.setAirIncrease(airIncrease);
            }
            else if (Engine.runningAsDev)
            {
                SurvivalMod.logger.error((player.worldObj.isRemote ? "Client" : "Server") + ": Received a packet from player:'" + player.getCommandSenderName() + "' to update stats for entity:'" + entity.getCommandSenderName() + "' but entity doesn't have a stat provider!");
            }
        }
        else if (Engine.runningAsDev)
        {
            SurvivalMod.logger.error((player.worldObj.isRemote ? "Client" : "Server") + ": Received a packet from player:'" + player.getCommandSenderName() + "' to update stats for entity with id:'" + entityId + "' but entity doesn't exist!");
        }
    }

    @Override
    public void handleServerSide(EntityPlayer player)
    {
        //Do nothing server side, this should always be server -> client only to prevent cheating
    }
}
