package com.builtbroken.wowjudo.network;

import com.builtbroken.wowjudo.JudoMod;
import com.builtbroken.wowjudo.network.packets.Packet;
import cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author tgame14
 * @since 31/05/14
 */
public class ResonantChannelHandler extends FMLIndexedMessageToMessageCodec<Packet>
{
    public boolean silenceStackTrace = false; //TODO add command and config

    public ResonantChannelHandler()
    {
        //this.addDiscriminator(0, PacketMouseClick.class);
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, Packet packet, ByteBuf target) throws Exception
    {
        try
        {
            packet.write(target);
        }
        catch (Exception e)
        {
            if (!silenceStackTrace)
                JudoMod.logger.error("Failed to encode packet " + packet, e);
            else
                JudoMod.logger.error("Failed to encode packet " + packet + " E: " + e.getMessage());
        }
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf source, Packet packet)
    {
        try
        {
            packet.read(source);
        }
        catch (Exception e)
        {
            if (!silenceStackTrace)
                JudoMod.logger.error("Failed to decode packet " + packet, e);
            else
                JudoMod.logger.error("Failed to decode packet " + packet + " E: " + e.getMessage());
        }
    }
}
