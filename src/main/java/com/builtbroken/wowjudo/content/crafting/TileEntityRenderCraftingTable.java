package com.builtbroken.wowjudo.content.crafting;

import com.builtbroken.wowjudo.SurvivalMod;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/8/2017.
 */
public class TileEntityRenderCraftingTable extends TileEntitySpecialRenderer
{
    public static final IModelCustom MODEL = AdvancedModelLoader.loadModel(new ResourceLocation(SurvivalMod.DOMAIN, "models/crafting.obj"));
    public static final ResourceLocation TEXTURE_LOG = new ResourceLocation(SurvivalMod.DOMAIN, "textures/models/log_oak.png");
    public static final ResourceLocation TEXTURE_PLANK = new ResourceLocation(SurvivalMod.DOMAIN, "textures/models/planks_oak.png");
    public static final ResourceLocation TEXTURE_SHELF = new ResourceLocation(SurvivalMod.DOMAIN, "textures/models/planks_long.png");
    public static final ResourceLocation TEXTURE_TOP = new ResourceLocation(SurvivalMod.DOMAIN, "textures/models/planks_top.png");

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float frameDelta)
    {
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);


        ForgeDirection direction = ForgeDirection.UNKNOWN;
        switch (tile.getBlockMetadata())
        {
            //North
            case 2:
                direction = ForgeDirection.EAST;
                break;
            //South
            case 3:
                direction = ForgeDirection.WEST;
                break;
            //West
            case 4:
                direction = ForgeDirection.NORTH;
                break;
            //East
            case 5:
                direction = ForgeDirection.SOUTH;
                break;
        }
        //Render Fire pit
        GL11.glTranslated(0.5 + 0.5 * direction.offsetX, 0, 0.5 + 0.5 * direction.offsetZ);

        final float scale = 0.0625f / 2.45f;
        GL11.glScaled(scale, scale, scale);

        if (direction == ForgeDirection.SOUTH)
        {
            GL11.glRotatef(90, 0, 1, 0);
        }
        else if (direction == ForgeDirection.NORTH)
        {
            GL11.glRotatef(-90, 0, 1, 0);
        }
        else if(direction == ForgeDirection.EAST)
        {
            GL11.glRotatef(-180, 0, 1, 0);
        }

        //Render top
        bindTexture(TEXTURE_TOP);
        MODEL.renderOnly("group_1");

        //Render sides
        bindTexture(TEXTURE_PLANK);
        MODEL.renderOnly("ID33", "Component_18", "Component_15");

        //Render shelfs
        bindTexture(TEXTURE_SHELF);
        MODEL.renderOnly("Component_17");

        //Render legs
        bindTexture(TEXTURE_LOG);
        MODEL.renderOnly("Component_16");

        GL11.glPopMatrix();
    }
}
