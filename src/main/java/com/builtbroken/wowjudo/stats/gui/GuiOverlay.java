package com.builtbroken.wowjudo.stats.gui;

import com.builtbroken.jlib.data.Colors;
import com.builtbroken.mc.client.helpers.Render2DHelper;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.lib.helper.MathUtility;
import com.builtbroken.wowjudo.stats.FoodStatOverride;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;
import net.minecraft.util.FoodStats;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.config.Configuration;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/30/2017.
 */
public class GuiOverlay
{
    public static final int FOOD_TEXT_COLOR = new Color(110, 9, 15).getRGB();
    public static final GuiOverlay INSTANCE = new GuiOverlay();

    public boolean enableAirRender = true;
    public boolean enableFoodRender = true;
    public boolean enableHealthRender = true;

    private int airBarMaxSize = 300;

    private int foodRenderHeight = 0;

    @SubscribeEvent
    public void onRenderOverlayPre(RenderGameOverlayEvent.Pre event)
    {
        int width = event.resolution.getScaledWidth();
        int height = event.resolution.getScaledHeight();
        Minecraft mc = Minecraft.getMinecraft();

        if (event.type == RenderGameOverlayEvent.ElementType.AIR)
        {
            renderAir(event, width, height, mc);
            event.setCanceled(true);
        }
        else if (event.type == RenderGameOverlayEvent.ElementType.FOOD)
        {
            foodRenderHeight = GuiIngameForge.right_height;
        }
    }

    @SubscribeEvent
    public void onRenderOverlayPost(RenderGameOverlayEvent.Post event)
    {
        int width = event.resolution.getScaledWidth();
        int height = event.resolution.getScaledHeight();
        Minecraft mc = Minecraft.getMinecraft();

        if (event.type == RenderGameOverlayEvent.ElementType.AIR)
        {
            if (Minecraft.getMinecraft().thePlayer.isInsideOfMaterial(Material.water))
            {
                GuiIngameForge.right_height += 10;
            }
        }
        else if (event.type == RenderGameOverlayEvent.ElementType.FOOD)
        {
            renderFood(event, width, height, mc);
        }
    }

    protected void renderAir(RenderGameOverlayEvent.Pre event, int width, int height, Minecraft mc)
    {
        GL11.glEnable(GL11.GL_BLEND);

        int left = width / 2 + 91;
        int top = height - GuiIngameForge.right_height;

        if (Minecraft.getMinecraft().thePlayer.isInsideOfMaterial(Material.water))
        {
            int maxBarSize = 300;
            int airTicks = mc.thePlayer.getAir();

            //Track largest air size TODO use to auto adjust bar
            if (airBarMaxSize < airTicks)
            {
                airBarMaxSize = airTicks;
            }

            //Temp fix for render issues with air bar being too large
            if (airTicks > maxBarSize)
            {
                airTicks = maxBarSize;
            }

            //Get full air bubbles
            int full = MathHelper.ceiling_double_int((double) (airTicks - 2) * 10.0D / maxBarSize);

            //Get partial air bubbles
            int partial = MathHelper.ceiling_double_int((double) airTicks * 10.0D / maxBarSize) - full;

            //Generate bubbles
            for (int i = 0; i < full + partial; ++i)
            {
                Render2DHelper.drawTexturedModalRect(
                        //Position
                        left - i * 8 - 9, top,
                        //UV
                        (i < full ? 16 : 25), 18, //16 is full bubble, 25 partial bubble
                        //Size
                        9, 9);
            }

            if(Engine.runningAsDev)
            {
                Render2DHelper.renderTextWithShadow("Air: " + mc.thePlayer.getAir(), left, top, 8453920);
            }
        }

        GL11.glDisable(GL11.GL_BLEND);
    }

    protected void renderFood(RenderGameOverlayEvent.Post event, int width, int height, Minecraft mc)
    {
        //Setup values
        java.awt.Color color = Colors.RED.color;
        GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 1f);
        GL11.glEnable(GL11.GL_BLEND);

        int left = width / 2 + 91;
        int top = height - foodRenderHeight;

        FoodStats stats = mc.thePlayer.getFoodStats();
        int foodLevel = stats.getFoodLevel();
        int extra = foodLevel - FoodStatOverride.MAX_FOOD_DEFAULT;
        int bars = extra / FoodStatOverride.MAX_FOOD_DEFAULT;

        //Render extra food bar
        if (foodLevel > FoodStatOverride.MAX_FOOD_DEFAULT)
        {
            for (int foodIconIndex = 0; foodIconIndex < 10; ++foodIconIndex)
            {
                int index = foodIconIndex * 2 + 1 + FoodStatOverride.MAX_FOOD_DEFAULT;

                int x = left - foodIconIndex * 8 - 9;
                int y = top;

                int icon = 16;

                if (mc.thePlayer.isPotionActive(Potion.hunger))
                {
                    icon += 36;
                }

                if (mc.thePlayer.getFoodStats().getSaturationLevel() <= 0.0F && event.partialTicks % (foodLevel * 3 + 1) == 0)
                {
                    y = top + (MathUtility.rand.nextInt(3) - 1);
                }

                if (index < foodLevel)
                {
                    Render2DHelper.drawTexturedModalRect(x, y, icon + 36, 27, 9, 9);
                }
                else if (index == foodLevel)
                {
                    Render2DHelper.drawTexturedModalRect(x, y, icon + 45, 27, 9, 9);
                }
            }
        }

        //Reset values
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_BLEND);

        //Render multiplier to note number of food bars in use
        if(bars > 0)
        {
           Render2DHelper.renderTextWithShadow("x" + bars, left, top, FOOD_TEXT_COLOR);
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    protected void renderHealth(RenderGameOverlayEvent.Pre event)
    {

    }

    public void loadConfig(Configuration configuration)
    {
        enableAirRender = configuration.getBoolean("air", "overlay_renders", enableAirRender,
                "Enables air overlay render, disable to allow other mods to handle or solve bugs.");
        enableFoodRender = configuration.getBoolean("food", "overlay_renders", enableFoodRender,
                "Enables food overlay render, disable to allow other mods to handle or solve bugs.");
        enableHealthRender = configuration.getBoolean("health", "overlay_renders", enableHealthRender,
                "Enables health overlay render, disable to allow other mods to handle or solve bugs.");

    }
}
