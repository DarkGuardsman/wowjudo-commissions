package com.builtbroken.wowjudo.content.furnace;

import com.builtbroken.mc.api.tile.node.ITileNodeHost;
import com.builtbroken.mc.lib.render.RenderUtility;
import com.builtbroken.wowjudo.content.campfire.TileEntityRenderCampFire;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 9/9/2017.
 */
public class TESRDualFurnace extends TileEntitySpecialRenderer
{
    EntityItem entityItem = new EntityItem(null);

    int crashCount = 0;
    boolean errorLock = false;

    @Override
    public void renderTileEntityAt(TileEntity tile, double xx, double yy, double zz, float delta)
    {
        if (tile instanceof ITileNodeHost && ((ITileNodeHost) tile).getTileNode() instanceof TileDualFurnace)
        {
            TileDualFurnace furnace = (TileDualFurnace) ((ITileNodeHost) tile).getTileNode();

            boolean hasFuel = furnace.hasFuel; //has fuel time
            boolean isCooking = furnace.burnTimer1 > 0 || furnace.burnTimer2 > 0; //has cook time

            GL11.glPushMatrix();
            GL11.glTranslated(xx, yy, zz);

            if (hasFuel || isCooking)
            {
                //Charcoal
                GL11.glPushMatrix();
                GL11.glTranslated(0, 0.6, 0);
                RenderUtility.renderCube(0.2, 0, 0.2, 0.8, 0.1, 0.8, Blocks.coal_block);
                GL11.glPopMatrix();
            }
            else
            {
                GL11.glPushMatrix();
                GL11.glTranslated(0, 0.6, 0);
                RenderUtility.renderCube(0.2, 0, 0.2, 0.8, 0.01, 0.8, Blocks.coal_block);
                GL11.glPopMatrix();
            }

            if (isCooking)
            {
                //Flames
                GL11.glPushMatrix();
                GL11.glScaled(0.6, 0.5, 0.6);
                GL11.glTranslated(0.3, 0.6, 0.3);
                RenderUtility.setTerrainTexture();
                TileEntityRenderCampFire.renderBlockFire();
                GL11.glPopMatrix();
            }
            final float itemScale = 0.5f;
            final float itemSpacing = 0.15f;
            float itemYLevel = hasFuel || isCooking ? 0.7f : 0.61f;
            if (!errorLock)
            {
                try
                {
                    GL11.glPushMatrix();
                    if (furnace.getDirection() == ForgeDirection.WEST)
                    {
                        GL11.glTranslated(0.5, itemYLevel, 0.4);
                    }
                    else if (furnace.getDirection() == ForgeDirection.EAST)
                    {
                        GL11.glTranslated(0.4, itemYLevel, 0.4);
                    }
                    else if (furnace.getDirection() == ForgeDirection.NORTH)
                    {
                        GL11.glTranslated(0.5, itemYLevel, 0.5);
                    }
                    else if (furnace.getDirection() == ForgeDirection.SOUTH)
                    {
                        GL11.glTranslated(0.5, itemYLevel, 0.3);
                    }
                    if (furnace.renderStack1 != null)
                    {
                        GL11.glPushMatrix();
                        GL11.glTranslated(itemSpacing * furnace.getDirection().offsetZ, 0, itemSpacing * furnace.getDirection().offsetX);
                        GL11.glScaled(itemScale, itemScale, itemScale);
                        GL11.glRotatef(90, 1, 0, 0);
                        renderItem(furnace.world().unwrap(), furnace.x(), furnace.y(), furnace.z(), furnace.renderStack1);
                        GL11.glPopMatrix();
                    }
                    if (furnace.renderStack2 != null)
                    {
                        GL11.glPushMatrix();
                        GL11.glTranslated(-itemSpacing * furnace.getDirection().offsetZ, 0, -itemSpacing * furnace.getDirection().offsetX);
                        GL11.glScaled(itemScale, itemScale, itemScale);
                        GL11.glRotatef(90, 1, 0, 0);
                        renderItem(furnace.world().unwrap(), furnace.x(), furnace.y(), furnace.z(), furnace.renderStack2);
                        GL11.glPopMatrix();
                    }
                    GL11.glPopMatrix();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    crashCount++;
                    if (crashCount > 3)
                    {
                        errorLock = true;
                    }
                }
            }

            GL11.glPopMatrix();
        }
    }

    public void renderItem(World world, double x, double y, double z, ItemStack stack)
    {
        entityItem.worldObj = world;
        entityItem.posX = x;
        entityItem.posY = y;
        entityItem.posZ = z;

        entityItem.setEntityItemStack(stack.copy());
        entityItem.getEntityItem().stackSize = 1;
        entityItem.hoverStart = 0.0F;
        GL11.glPushMatrix();

        RenderItem renderItem = ((RenderItem) RenderManager.instance.getEntityClassRenderObject(EntityItem.class));

        renderItem.doRender(entityItem, 0, 0, 0, 0, 0);

        GL11.glPopMatrix();
    }
}
