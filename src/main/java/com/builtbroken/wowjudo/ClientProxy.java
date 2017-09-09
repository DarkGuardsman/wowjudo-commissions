package com.builtbroken.wowjudo;

import com.builtbroken.mc.client.json.ClientDataHandler;
import com.builtbroken.wowjudo.content.campfire.TileEntityCampfire;
import com.builtbroken.wowjudo.content.campfire.TileEntityRenderCampFire;
import com.builtbroken.wowjudo.content.crafting.TileEntityCraftingTable;
import com.builtbroken.wowjudo.content.crafting.TileEntityRenderCraftingTable;
import com.builtbroken.wowjudo.content.explosive.tile.ISBRExplosive;
import com.builtbroken.wowjudo.content.furnace.ISBRDualFurance;
import com.builtbroken.wowjudo.content.furnace.TESRDualFurnace;
import com.builtbroken.wowjudo.content.furnace.TileWrapperDualFurnace;
import com.builtbroken.wowjudo.content.wall.ISBRWall;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

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
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCraftingTable.class, new TileEntityRenderCraftingTable());
        ClientRegistry.bindTileEntitySpecialRenderer(TileWrapperDualFurnace.class, new TESRDualFurnace());
    }

    @Override
    public void loadJsonContentHandlers()
    {
        RenderingRegistry.registerBlockHandler(new ISBRDualFurance());
        ClientDataHandler.INSTANCE.addBlockRenderer(SurvivalMod.DOMAIN + ":wall", new ISBRWall());
    }
}
