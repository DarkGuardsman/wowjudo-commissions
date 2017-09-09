package com.builtbroken.wowjudo.content.campfire;

import com.builtbroken.mc.lib.render.RenderUtility;
import com.builtbroken.wowjudo.SurvivalMod;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/8/2017.
 */
public class TileEntityRenderCampFire extends TileEntitySpecialRenderer
{
    public static final IModelCustom MODEL = AdvancedModelLoader.loadModel(new ResourceLocation(SurvivalMod.DOMAIN, "models/firepit.obj"));
    public static final ResourceLocation TEXTURE_WOOD = new ResourceLocation("textures/blocks/log_oak.png");
    public static final ResourceLocation TEXTURE_STONE = new ResourceLocation("textures/blocks/stone.png");
    public static final ResourceLocation TEXTURE_COAL = new ResourceLocation("textures/blocks/coal_block.png");

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float frameDelta)
    {
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);

        //Render Fire pit
        GL11.glPushMatrix();
        GL11.glTranslated(0.5, 0.01, 0.5);

        final float scale = 0.0625f / 4f;
        GL11.glScaled(scale, scale, scale);

        if (tile instanceof TileEntityCampfire)
        {
            if (((TileEntityCampfire) tile).hasFuel)
            {
                //Render logs
                bindTexture(TEXTURE_WOOD);
                MODEL.renderOnly("Component_13");
            }
        }

        //Render ring of rocks
        bindTexture(TEXTURE_STONE);
        MODEL.renderOnly("Component_12");

        //Render bottom
        bindTexture(TEXTURE_COAL);
        MODEL.renderOnly("group_1");
        GL11.glPopMatrix();

        //Render fire
        if (tile instanceof TileEntityCampfire)
        {
            if (((TileEntityCampfire) tile).cookTimer > 0)
            {
                GL11.glPushMatrix();
                RenderUtility.setTerrainTexture();
                renderBlockFire();
                GL11.glPopMatrix();
            }
        }

        GL11.glPopMatrix();
    }

    public static boolean renderBlockFire()
    {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawing(GL11.GL_QUADS);
        IIcon iicon = Blocks.fire.getFireIcon(0);
        IIcon iicon1 = Blocks.fire.getFireIcon(1);
        IIcon iicon2 = iicon;

        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        tessellator.setBrightness(200);
        double d0 = (double) iicon2.getMinU();
        double d1 = (double) iicon2.getMinV();
        double d2 = (double) iicon2.getMaxU();
        double d3 = (double) iicon2.getMaxV();
        float f = 1.4F;
        double d5;
        double d6;
        double d7;
        double d8;
        double d9;
        double d10;
        double d11;
        double d4 = (double) 0 + 0.5D + 0.2D;
        d5 = (double) 0 + 0.5D - 0.2D;
        d6 = (double) 0 + 0.5D + 0.2D;
        d7 = (double) 0 + 0.5D - 0.2D;
        d8 = (double) 0 + 0.5D - 0.3D;
        d9 = (double) 0 + 0.5D + 0.3D;
        d10 = (double) 0 + 0.5D - 0.3D;
        d11 = (double) 0 + 0.5D + 0.3D;
        tessellator.addVertexWithUV(d8, (double) ((float) 0 + f), (double) (0 + 1), d2, d1);
        tessellator.addVertexWithUV(d4, (double) (0 + 0), (double) (0 + 1), d2, d3);
        tessellator.addVertexWithUV(d4, (double) (0 + 0), (double) (0 + 0), d0, d3);
        tessellator.addVertexWithUV(d8, (double) ((float) 0 + f), (double) (0 + 0), d0, d1);
        tessellator.addVertexWithUV(d9, (double) ((float) 0 + f), (double) (0 + 0), d2, d1);
        tessellator.addVertexWithUV(d5, (double) (0 + 0), (double) (0 + 0), d2, d3);
        tessellator.addVertexWithUV(d5, (double) (0 + 0), (double) (0 + 1), d0, d3);
        tessellator.addVertexWithUV(d9, (double) ((float) 0 + f), (double) (0 + 1), d0, d1);
        d0 = (double) iicon1.getMinU();
        d1 = (double) iicon1.getMinV();
        d2 = (double) iicon1.getMaxU();
        d3 = (double) iicon1.getMaxV();
        tessellator.addVertexWithUV((double) (0 + 1), (double) ((float) 0 + f), d11, d2, d1);
        tessellator.addVertexWithUV((double) (0 + 1), (double) (0 + 0), d7, d2, d3);
        tessellator.addVertexWithUV((double) (0 + 0), (double) (0 + 0), d7, d0, d3);
        tessellator.addVertexWithUV((double) (0 + 0), (double) ((float) 0 + f), d11, d0, d1);
        tessellator.addVertexWithUV((double) (0 + 0), (double) ((float) 0 + f), d10, d2, d1);
        tessellator.addVertexWithUV((double) (0 + 0), (double) (0 + 0), d6, d2, d3);
        tessellator.addVertexWithUV((double) (0 + 1), (double) (0 + 0), d6, d0, d3);
        tessellator.addVertexWithUV((double) (0 + 1), (double) ((float) 0 + f), d10, d0, d1);
        d4 = (double) 0 + 0.5D - 0.5D;
        d5 = (double) 0 + 0.5D + 0.5D;
        d6 = (double) 0 + 0.5D - 0.5D;
        d7 = (double) 0 + 0.5D + 0.5D;
        d8 = (double) 0 + 0.5D - 0.4D;
        d9 = (double) 0 + 0.5D + 0.4D;
        d10 = (double) 0 + 0.5D - 0.4D;
        d11 = (double) 0 + 0.5D + 0.4D;
        tessellator.addVertexWithUV(d8, (double) ((float) 0 + f), (double) (0 + 0), d0, d1);
        tessellator.addVertexWithUV(d4, (double) (0 + 0), (double) (0 + 0), d0, d3);
        tessellator.addVertexWithUV(d4, (double) (0 + 0), (double) (0 + 1), d2, d3);
        tessellator.addVertexWithUV(d8, (double) ((float) 0 + f), (double) (0 + 1), d2, d1);
        tessellator.addVertexWithUV(d9, (double) ((float) 0 + f), (double) (0 + 1), d0, d1);
        tessellator.addVertexWithUV(d5, (double) (0 + 0), (double) (0 + 1), d0, d3);
        tessellator.addVertexWithUV(d5, (double) (0 + 0), (double) (0 + 0), d2, d3);
        tessellator.addVertexWithUV(d9, (double) ((float) 0 + f), (double) (0 + 0), d2, d1);
        d0 = (double) iicon.getMinU();
        d1 = (double) iicon.getMinV();
        d2 = (double) iicon.getMaxU();
        d3 = (double) iicon.getMaxV();
        tessellator.addVertexWithUV((double) (0 + 0), (double) ((float) 0 + f), d11, d0, d1);
        tessellator.addVertexWithUV((double) (0 + 0), (double) (0 + 0), d7, d0, d3);
        tessellator.addVertexWithUV((double) (0 + 1), (double) (0 + 0), d7, d2, d3);
        tessellator.addVertexWithUV((double) (0 + 1), (double) ((float) 0 + f), d11, d2, d1);
        tessellator.addVertexWithUV((double) (0 + 1), (double) ((float) 0 + f), d10, d0, d1);
        tessellator.addVertexWithUV((double) (0 + 1), (double) (0 + 0), d6, d0, d3);
        tessellator.addVertexWithUV((double) (0 + 0), (double) (0 + 0), d6, d2, d3);
        tessellator.addVertexWithUV((double) (0 + 0), (double) ((float) 0 + f), d10, d2, d1);


        tessellator.draw();
        return true;
    }
}
