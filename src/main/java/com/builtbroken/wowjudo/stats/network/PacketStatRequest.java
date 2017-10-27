package com.builtbroken.wowjudo.stats.network;

import com.builtbroken.mc.api.data.IPacket;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.wowjudo.SurvivalMod;
import com.builtbroken.wowjudo.stats.StatEntityProperty;
import com.builtbroken.wowjudo.stats.StatHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Handles asking for stat updates from server
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/262017.
 */
public class PacketStatRequest implements IPacket
{
    public PacketStatRequest()
    {
        //Needed for forge to construct the packet
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
    {
        //No data to send
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
    {
        //No data to send
    }

    @Override
    public void handleClientSide(EntityPlayer player)
    {
        //Server side only packet
    }

    @Override
    public void handleServerSide(EntityPlayer player)
    {
        if (Engine.runningAsDev)
        {
            SurvivalMod.logger.info("PacketStatRequest: " + player);
        }
        if (player instanceof EntityPlayerMP)
        {
            StatEntityProperty property = StatHandler.getPropertyForEntity(player);
            if (property != null)
            {
                Engine.packetHandler.sendToPlayer(new PacketStatUpdate(player), (EntityPlayerMP) player);
            }
            else if (Engine.runningAsDev)
            {
                SurvivalMod.logger.error((player.worldObj.isRemote ? "Client" : "Server") + ": Received a packet from player:'" + player.getCommandSenderName() + "' to request state update on client but no stat provider exists!");
            }
        }
        else if (Engine.runningAsDev)
        {
            SurvivalMod.logger.error((player.worldObj.isRemote ? "Client" : "Server") + ": Received a packet from player:'" + player.getCommandSenderName() + "' to request state update on client but player is not a multiplayer entity.");
        }
    }
}
