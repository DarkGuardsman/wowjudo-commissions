package com.builtbroken.wowjudo.content.furnace;

import com.builtbroken.mc.api.tile.node.ITileNodeHost;
import com.builtbroken.mc.lib.render.RenderUtility;
import com.builtbroken.wowjudo.content.campfire.TileEntityRenderCampFire;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 9/9/2017.
 */
public class TESRDualFurnace extends TileEntitySpecialRenderer
{
    @Override
    public void renderTileEntityAt(TileEntity tile, double xx, double yy, double zz, float delta)
    {
        if (tile instanceof ITileNodeHost && ((ITileNodeHost) tile).getTileNode() instanceof TileDualFurnace)
        {
            TileDualFurnace furnace = (TileDualFurnace) ((ITileNodeHost) tile).getTileNode();

            boolean hasFuel = true; //has fuel time
            boolean isCooking = true; //has cook time

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

            GL11.glPopMatrix();
        }
    }
}
