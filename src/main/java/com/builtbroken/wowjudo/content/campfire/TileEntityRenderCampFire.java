package com.builtbroken.wowjudo.content.campfire;

import com.builtbroken.wowjudo.SurvivalMod;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
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
        GL11.glTranslated(x + 0.5, y + 0.01, z + 0.5);
        final float scale = 0.0625f / 4f;
        GL11.glScaled(scale, scale, scale);

        //Render logs
        bindTexture(TEXTURE_WOOD);
        MODEL.renderOnly("Component_13");

        //Render ring of rocks
        bindTexture(TEXTURE_STONE);
        MODEL.renderOnly("Component_12");

        //Render bottom
        bindTexture(TEXTURE_COAL);
        MODEL.renderOnly("group_1");
        GL11.glPopMatrix();
    }
}
