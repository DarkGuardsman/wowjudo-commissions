package com.builtbroken.wowjudo.content.explosive.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import java.util.UUID;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/7/2017.
 */
public class TileEntityExplosive extends TileEntity
{
    public UUID ownerUUID;

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
        worldObj.createExplosion(null, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, 8, true);
    }

    public void trigger(EntityPlayer player)
    {
        isExploding = true;
    }
}
