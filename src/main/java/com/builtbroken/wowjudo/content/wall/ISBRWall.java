package com.builtbroken.wowjudo.content.wall;

import com.builtbroken.mc.lib.render.RenderUtility;
import com.builtbroken.mc.lib.render.block.BlockRenderHandler;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/27/2017.
 */
public class ISBRWall implements ISimpleBlockRenderingHandler
{
    public int ID = RenderingRegistry.getNextAvailableRenderId();

    public ISBRWall()
    {
        ID = RenderingRegistry.getNextAvailableRenderId();
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer)
    {
        RenderUtility.renderCube(0, 0, 0, 1, 1, 1, block, null, metadata);
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess access, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
    {
        //base block render
        renderer.renderStandardBlock(block, x, y, z);
        //Damage
        IIcon icon = Blocks.vine.getIcon(0, 0); //TODO get breaking icon

        if (block.shouldSideBeRendered(access, x, y, z, 0))
        {
            renderer.renderFaceYNeg(block, (double) x, (double) y, (double) z, icon);
        }
        if (block.shouldSideBeRendered(access, x, y, z, 1))
        {
            renderer.renderFaceYPos(block, (double) x, (double) y, (double) z, icon);
        }
        if (block.shouldSideBeRendered(access, x, y, z, 2))
        {
            renderer.renderFaceZNeg(block, (double) x, (double) y, (double) z, icon);
        }
        if (block.shouldSideBeRendered(access, x, y, z, 3))
        {
            renderer.renderFaceZPos(block, (double) x, (double) y, (double) z, icon);
        }
        if (block.shouldSideBeRendered(access, x, y, z, 4))
        {
            renderer.renderFaceXNeg(block, (double) x, (double) y, (double) z, icon);
        }
        if (block.shouldSideBeRendered(access, x, y, z, 5))
        {
            renderer.renderFaceXPos(block, (double) x, (double) y, (double) z, icon);
        }

        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId)
    {
        return true;
    }

    @Override
    public int getRenderId()
    {
        return BlockRenderHandler.ID;
    }
}
