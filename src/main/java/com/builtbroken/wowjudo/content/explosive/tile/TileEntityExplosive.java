package com.builtbroken.wowjudo.content.explosive.tile;

import com.builtbroken.mc.api.event.TriggerCause;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.lib.world.explosive.ExplosiveRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/7/2017.
 */
public class TileEntityExplosive extends TileEntity
{
    public static float BLAST_SIZE = 8;
    public static int BLAST_DELAY = 20;

    public EntityPlayer triggerEntity;

    public int timer = 0;
    public boolean isExploding = false;

    @Override
    public void updateEntity()
    {
        if (isExploding)
        {
            timer++;
            if (timer > BLAST_DELAY)
            {
                explode();
            }
        }
    }

    public void explode()
    {
        IExplosiveHandler handler = ExplosiveRegistry.get("wowjudo.damage");
        if (handler != null)
        {
            worldObj.setBlockToAir(xCoord, yCoord, zCoord);
            ExplosiveRegistry.triggerExplosive(worldObj, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, handler, new TriggerCause.TriggerCauseEntity(triggerEntity), BLAST_SIZE, new NBTTagCompound());
        }
    }

    public void trigger(EntityPlayer player)
    {
        if (!isExploding)
        {
            triggerEntity = player;
            isExploding = true;
        }
        else
        {
            player.addChatComponentMessage(new ChatComponentText("Explosive is already triggered!"));
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        isExploding = nbt.getBoolean("isExploding");
        timer = nbt.getInteger("timer");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setBoolean("isExploding", isExploding);
        nbt.setInteger("timer", timer);
    }
}
