package com.builtbroken.wowjudo;

import com.builtbroken.wowjudo.content.campfire.gui.GuiCampFire;
import com.builtbroken.wowjudo.content.campfire.TileEntityCampfire;
import com.builtbroken.wowjudo.content.campfire.TileEntityRenderCampFire;
import com.builtbroken.wowjudo.content.explosive.tile.ISBRExplosive;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/7/2017.
 */
public class ClientProxy extends CommonProxy
{
    @Override
    public void preInit()
    {
        RenderingRegistry.registerBlockHandler(new ISBRExplosive());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCampfire.class, new TileEntityRenderCampFire());
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        if(tile instanceof TileEntityCampfire)
        {
            return new GuiCampFire(player, (TileEntityCampfire)tile);
        }
        return null;
    }
}
