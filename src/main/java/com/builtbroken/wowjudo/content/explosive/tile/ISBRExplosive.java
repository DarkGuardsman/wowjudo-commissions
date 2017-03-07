package com.builtbroken.wowjudo.content.explosive.tile;

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

    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
    {
        final float pixel = 1.0f / 16.0f;
        final float c4Size = pixel * 3;
        final float buttonSize = pixel * 6;
        int meta = world.getBlockMetadata(x, y, z);

        //Keep track of previous values for reset
        int uvRotationEast = renderer.uvRotateEast;
        int uvRotationWest = renderer.uvRotateWest;
        int uvRotationNorth = renderer.uvRotateNorth;
        int uvRotationSouth = renderer.uvRotateSouth;
        int uvRotationBottom = renderer.uvRotateBottom;
        int uvRotationTop = renderer.uvRotateTop;

        switch (meta)
        {
            //Bottom
            case 0:
                renderer.uvRotateEast = 3;
                renderer.uvRotateWest = 3;
                renderer.uvRotateNorth = 3;
                renderer.uvRotateSouth = 3;

                renderer.setRenderBounds(c4Size, 1.01 - 6 * pixel, c4Size, 1 - c4Size, 1, 1 - c4Size);
                renderer.renderStandardBlock(block, x, y, z);

                //Reset
                renderer.uvRotateEast = uvRotationEast;
                renderer.uvRotateWest = uvRotationWest;
                renderer.uvRotateNorth = uvRotationNorth;
                renderer.uvRotateSouth = uvRotationSouth;

                renderer.setRenderBounds(buttonSize, .5, buttonSize, 1 - buttonSize, .6, 1 - buttonSize);
                renderBlock(renderer, block, x, y, z, block.getIcon(0, 7));
                break;
            //TOP
            case 1:
                renderer.setRenderBounds(c4Size, 0, c4Size, 1 - c4Size,  6 * pixel, 1 - c4Size);
                renderer.renderStandardBlock(block, x, y, z);

                renderer.setRenderBounds(buttonSize, .4, buttonSize, 1 - buttonSize, .5, 1 - buttonSize);
                renderBlock(renderer, block, x, y, z, block.getIcon(0, 7));
                break;
            //North
            case 2:
                renderer.uvRotateNorth = 2;
                renderer.uvRotateSouth = 1;

                renderer.setRenderBounds(c4Size, c4Size,  1.01 - 6 * pixel, 1 - c4Size, 1 - c4Size, 1);
                renderer.renderStandardBlock(block, x, y, z);

                //Reset
                renderer.uvRotateNorth = uvRotationNorth;
                renderer.uvRotateSouth = uvRotationSouth;

                renderer.setRenderBounds(buttonSize, buttonSize, .5, 1 - buttonSize, 1 - buttonSize, .6);
                renderBlock(renderer, block, x, y, z, block.getIcon(0, 7));
                break;
            //South
            case 3:
                renderer.uvRotateNorth = 1;
                renderer.uvRotateSouth = 2;
                renderer.uvRotateBottom = 3;
                renderer.uvRotateTop = 3;

                renderer.setRenderBounds(c4Size, c4Size, 0, 1 - c4Size, 1 - c4Size,  6 * pixel);
                renderer.renderStandardBlock(block, x, y, z);

                //Reset
                renderer.uvRotateBottom = uvRotationBottom;
                renderer.uvRotateTop = uvRotationTop;
                renderer.uvRotateNorth = uvRotationNorth;
                renderer.uvRotateSouth = uvRotationSouth;

                renderer.setRenderBounds(buttonSize, buttonSize, .4, 1 - buttonSize, 1 - buttonSize, .5);
                renderBlock(renderer, block, x, y, z, block.getIcon(0, 7));
                break;
            //West
            case 4:
                renderer.uvRotateBottom = 1;
                renderer.uvRotateTop = 2;
                renderer.uvRotateEast = 1;
                renderer.uvRotateWest = 2;

                renderer.setRenderBounds(1.01 - 6 * pixel, c4Size, c4Size, 1, 1 - c4Size, 1 - c4Size);
                renderer.renderStandardBlock(block, x, y, z);

                //Reset
                renderer.uvRotateBottom = uvRotationBottom;
                renderer.uvRotateTop = uvRotationTop;
                renderer.uvRotateEast = uvRotationEast;
                renderer.uvRotateWest = uvRotationWest;

                renderer.setRenderBounds(.5, buttonSize, buttonSize,  1.01 - 6 * pixel, 1 - buttonSize, 1 - buttonSize);
                renderBlock(renderer, block, x, y, z, block.getIcon(0, 7));
                break;
            //East
            case 5:
                renderer.uvRotateBottom = 2;
                renderer.uvRotateTop = 1;
                renderer.uvRotateEast = 2;
                renderer.uvRotateWest = 1;

                renderer.setRenderBounds(0, c4Size, c4Size, 6 * pixel, 1 - c4Size, 1 - c4Size);
                renderer.renderStandardBlock(block, x, y, z);

                //Reset
                renderer.uvRotateBottom = uvRotationBottom;
                renderer.uvRotateTop = uvRotationTop;
                renderer.uvRotateEast = uvRotationEast;
                renderer.uvRotateWest = uvRotationWest;

                renderer.setRenderBounds(.4, buttonSize, buttonSize, .5, 1 - buttonSize, 1 - buttonSize);
                renderBlock(renderer, block, x, y, z, block.getIcon(0, 7));
                break;
        }
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
        return false;
    }

    @Override
    public int getRenderId()
    {
        return ID;
    }
}
