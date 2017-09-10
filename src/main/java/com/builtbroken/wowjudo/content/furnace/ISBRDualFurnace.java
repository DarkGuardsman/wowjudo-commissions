package com.builtbroken.wowjudo.content.furnace;

import com.builtbroken.mc.client.json.ClientDataHandler;
import com.builtbroken.mc.data.Direction;
import com.builtbroken.mc.lib.render.RenderUtility;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 8/31/2017.
 */
public class ISBRDualFurnace implements ISimpleBlockRenderingHandler
{
    final float wallWidth = 0.2f;

    final float wallStart = 0.6f;
    final float wallHeight = 1 - wallStart;
    final float wallEnd = wallStart + wallHeight;

    final float northStart = 0f;
    final float northEnd = wallWidth;

    final float southStart = 0.8f;
    final float southEnd = 1f;

    final float eastStart = 0.8f;
    final float eastEnd = 1f;

    final float westStart = 0f;
    final float westEnd = wallWidth;

    final float stackStart = 1.2f;
    final float stackEdge = 0.25f;
    final float stackEnd = 2f;
    final float stackThickness = 0.1f;

    public final static int ID = RenderingRegistry.getNextAvailableRenderId();

    public ISBRDualFurnace()
    {
        ClientDataHandler.INSTANCE.addBlockRenderer("dualFurnaceRender", this);
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer)
    {
        GL11.glPushMatrix();
        GL11.glTranslated(0, -0.2, 0);
        GL11.glScaled(0.85, 0.85, 0.85);
        renderWorldBlock(null, 0, -1, 0, block, modelId, renderer);
        GL11.glPopMatrix();
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
    {
        renderer.setRenderAllFaces(true);

        Direction direction = world == null ? Direction.EAST : Direction.getOrientation(world.getBlockMetadata(x, y, z)).getOpposite();

        //Base
        renderer.setRenderBounds(
                0, 0, 0,
                1, wallStart, 1);
        renderBlock(renderer, block, x, y, z, Blocks.stone_slab.getIcon(2, 0));

        if (direction != Direction.NORTH)
        {
            //North wall
            renderer.setRenderBounds(
                    westEnd, wallStart, northStart,
                    eastStart, wallEnd, northEnd);
            renderBlock(renderer, block, x, y, z, Blocks.cobblestone.getIcon(2, 0));
        }

        if (direction != Direction.SOUTH)
        {
            //South wall
            renderer.setRenderBounds(
                    westEnd, wallStart, southStart,
                    eastStart, wallEnd, southEnd);
            renderBlock(renderer, block, x, y, z, Blocks.cobblestone.getIcon(2, 0));
        }

        if (direction != Direction.EAST)
        {
            //East
            renderer.setRenderBounds(
                    eastStart, wallStart, northEnd,
                    eastEnd, wallEnd, southStart);
            renderBlock(renderer, block, x, y, z, Blocks.cobblestone.getIcon(2, 0));
        }

        if (direction != Direction.WEST)
        {
            //West
            renderer.setRenderBounds(
                    westStart, wallStart, northEnd,
                    westEnd, wallEnd, southStart);
            renderBlock(renderer, block, x, y, z, Blocks.cobblestone.getIcon(2, 0));
        }

        //Cap
        renderer.setRenderBounds(0, wallEnd, 0,
                1, stackStart, 1);
        renderBlock(renderer, block, x, y, z, Blocks.stone_slab.getIcon(2, 0));

        //===================================
        //smoke Stack
        //===================================
        if (y >= 0)
        {
            //north
            renderer.setRenderBounds(stackEdge, stackStart, stackEdge,
                    1 - stackEdge, stackEnd, stackEdge + stackThickness);
            renderBlock(renderer, block, x, y, z, Blocks.cobblestone.getIcon(2, 0));

            //South
            renderer.setRenderBounds(stackEdge, stackStart, 1 - stackEdge - stackThickness,
                    1 - stackEdge, stackEnd, 1 - stackEdge);
            renderBlock(renderer, block, x, y, z, Blocks.cobblestone.getIcon(2, 0));

            //East
            renderer.setRenderBounds(stackEdge, stackStart, stackEdge + stackThickness,
                    stackEdge + stackThickness, stackEnd, 1 - stackEdge - stackThickness);
            renderBlock(renderer, block, x, y, z, Blocks.cobblestone.getIcon(2, 0));

            //West
            renderer.setRenderBounds(1 - stackEdge - stackThickness, stackStart, stackEdge + stackThickness,
                    1 - stackEdge, stackEnd, 1 - stackEdge - stackThickness);
            renderBlock(renderer, block, x, y, z, Blocks.cobblestone.getIcon(2, 0));
        }
        //Item rendering
        else
        {
            //north
            renderer.setRenderBounds(stackEdge, stackStart, stackEdge,
                    1 - stackEdge, stackEnd - 0.5, stackEdge + stackThickness);
            renderBlock(renderer, block, x, y, z, Blocks.cobblestone.getIcon(2, 0));

            //South
            renderer.setRenderBounds(stackEdge, stackStart, 1 - stackEdge - stackThickness,
                    1 - stackEdge, stackEnd - 0.5, 1 - stackEdge);
            renderBlock(renderer, block, x, y, z, Blocks.cobblestone.getIcon(2, 0));

            //East
            renderer.setRenderBounds(stackEdge, stackStart, stackEdge + stackThickness,
                    stackEdge + stackThickness, stackEnd - 0.5, 1 - stackEdge - stackThickness);
            renderBlock(renderer, block, x, y, z, Blocks.cobblestone.getIcon(2, 0));

            //West
            renderer.setRenderBounds(1 - stackEdge - stackThickness, stackStart, stackEdge + stackThickness,
                    1 - stackEdge, stackEnd - 0.5, 1 - stackEdge - stackThickness);
            renderBlock(renderer, block, x, y, z, Blocks.cobblestone.getIcon(2, 0));

            //Charcoal
            renderer.setRenderBounds(westEnd, wallStart, northEnd,
                    eastStart, wallStart + 0.1, southStart);
            renderBlock(renderer, block, x, y, z, Blocks.coal_block.getIcon(2, 0));
        }

        renderer.setRenderAllFaces(false);
        return true;
    }

    public void renderBlock(RenderBlocks renderer, Block block, int x, int y, int z, IIcon icon)
    {
        if (y == -1)
        {
            RenderUtility.renderCube(renderer.renderMinX, renderer.renderMinY, renderer.renderMinZ, renderer.renderMaxX, renderer.renderMaxY, renderer.renderMaxZ, block, icon, 0);
        }
        else
        {
            renderer.setOverrideBlockTexture(icon);
            renderer.renderStandardBlock(block, x, y, z);
            renderer.setOverrideBlockTexture(null);
        }
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
