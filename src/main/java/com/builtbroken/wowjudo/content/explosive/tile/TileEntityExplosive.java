package com.builtbroken.wowjudo.content.explosive.tile;

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
    public EntityPlayer triggerEntity;

    public int timer = 0;
    public boolean isExploding = false;

    @Override
    public void updateEntity()
    {
        if (isExploding)
        {
            timer++;
            if (timer > 20)
            {
                explode();
            }
        }
    }

    public void explode()
    {
        worldObj.createExplosion(triggerEntity, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, 8, true);
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
