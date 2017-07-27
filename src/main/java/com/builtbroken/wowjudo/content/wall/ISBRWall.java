package com.builtbroken.wowjudo.content.wall;

import com.builtbroken.mc.api.tile.node.ITileNodeHost;
import com.builtbroken.mc.lib.helper.ReflectionUtility;
import com.builtbroken.mc.lib.render.RenderUtility;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;

import java.lang.reflect.Field;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/27/2017.
 */
public class ISBRWall implements ISimpleBlockRenderingHandler
{
    public int ID;

    public IIcon[] damageIcons;

    public ISBRWall()
    {
        ID = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(this);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onTextureCreated(TextureStitchEvent.Post event)
    {
        try
        {
            damageIcons = null;
            Field field = ReflectionUtility.getMCField(RenderGlobal.class, "field_94141_F", "destroyBlockIcons");
            field.setAccessible(true);
            damageIcons = (IIcon[]) field.get(Minecraft.getMinecraft().renderGlobal);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer)
    {
        RenderUtility.renderCube(0, -0.1, 0, 1, 0.9, 1, block, null, metadata);
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess access, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
    {
        //base block render
        renderer.renderStandardBlock(block, x, y, z);

        TileEntity tileEntity = access.getTileEntity(x, y, z);
        if (tileEntity instanceof ITileNodeHost && ((ITileNodeHost) tileEntity).getTileNode() instanceof TileNodeWall)
        {
            TileNodeWall wall = (TileNodeWall) ((ITileNodeHost) tileEntity).getTileNode();
            float hp = wall.getHp();
            float max_hp = wall.getMaxHp();
            float percent = hp / max_hp;

            if (percent < 0.98)
            {
                //Extend bounds to avoid z fighting
                renderer.setRenderBounds(-0.01, -0.01, -0.01, 1.01, 1.01, 1.01);

                //Damage
                IIcon icon = null; //TODO get breaking icon
                if (damageIcons != null)
                {
                    int i = Math.min(damageIcons.length - 1, Math.max(0, (int) (damageIcons.length - damageIcons.length * percent)));
                    icon = damageIcons[i];
                }

                if (icon == null)
                {
                    icon = Blocks.vine.getIcon(0, 0);
                }

                //Render sides
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
            }
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
        return ID;
    }
}
