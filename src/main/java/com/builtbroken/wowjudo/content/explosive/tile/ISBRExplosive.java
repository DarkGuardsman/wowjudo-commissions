package com.builtbroken.wowjudo.content.explosive.tile;

import com.builtbroken.mc.lib.render.RenderUtility;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/7/2017.
 */
public class ISBRExplosive implements ISimpleBlockRenderingHandler
{
    public static int ID = RenderingRegistry.getNextAvailableRenderId();

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer)
    {
        RenderUtility.renderCube(0, 0, 0, 1, 1, 1, block, null, metadata);
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
    {
        renderer.renderStandardBlock(block, x, y, z);
        return true;
    }

    public void renderBlock(RenderBlocks renderer, Block block, int x, int y, int z, IIcon icon)
    {
        renderer.renderFaceYNeg(block, (double) x, (double) y, (double) z, icon);
        renderer.renderFaceYPos(block, (double) x, (double) y, (double) z, icon);
        renderer.renderFaceZNeg(block, (double) x, (double) y, (double) z, icon);
        renderer.renderFaceZPos(block, (double) x, (double) y, (double) z, icon);
        renderer.renderFaceXNeg(block, (double) x, (double) y, (double) z, icon);
        renderer.renderFaceXPos(block, (double) x, (double) y, (double) z, icon);
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId)
    {
        return true;
    }

    @Override
    public int getRenderId()
    {
        return ID;
    }
}
