package com.builtbroken.wowjudo.stats.gui;

import com.builtbroken.mc.client.helpers.Render2DHelper;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.prefab.gui.buttons.GuiButton9px;
import com.builtbroken.mc.prefab.gui.components.GuiComponentContainer;
import com.builtbroken.mc.prefab.gui.pos.HugXSide;
import com.builtbroken.wowjudo.stats.network.PacketStatSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/10/2017.
 */
public class GuiComponentStat extends GuiComponentContainer<GuiComponentStat>
{
    public Color barColor;

    private GuiButton9px increaseButton;
    private GuiButton9px decreaseButton;

    private int min = 0;
    private int max = 10;
    private int value = 5;

    private int packetID = -1;

    public GuiComponentStat(int id, int x, int y, Color barColor, int packetID)
    {
        super(id, x, y, 0, 0, "");
        this.barColor = barColor;
        this.packetID = packetID;

        increaseButton = add(GuiButton9px.newPlusButton(0, 0, 0));
        increaseButton.setRelativePosition(new HugXSide(this, -GuiButton9px.SIZE, false));
        decreaseButton = add(GuiButton9px.newMinusButton(1, 0, 0));
        decreaseButton.setRelativePosition(new HugXSide(this, 0, true));
    }

    @Override
    protected void update(Minecraft mc, int mouseX, int mouseY)
    {
        super.update(mc, mouseX, mouseY);
    }

    public EntityPlayer player()
    {
        if (getHost() instanceof GuiStats)
        {
            return ((GuiStats) getHost()).player;
        }
        return null;
    }

    @Override
    public void actionPerformed(GuiButton button)
    {
        if (button == increaseButton)
        {
            //Fail safe
            if(!(getParentComponent() instanceof GuiFrameStats) || !((GuiFrameStats) getParentComponent()).canIncreaseStat(id))
            {
                increaseButton.disable();
                return;
            }
            //Enable decrease
            decreaseButton.enable();

            //Update value
            value = Math.min(max, value + 1);

            //Disable button if limit hit
            if(value == max)
            {
                increaseButton.disable();
            }

            //Update server
            syncToServer();
        }
        else if (button == decreaseButton)
        {
            //Enable increase button
            increaseButton.enable();

            //Update value
            value = Math.max(min, value - 1);

            //Disable button if limit hit
            if(value == min)
            {
                decreaseButton.disable();
            }

            //Update server
            syncToServer();
        }
    }

    protected void syncToServer()
    {
        if (packetID != -1 && player() != null)
        {
            Engine.packetHandler.sendToServer(new PacketStatSet(player(), packetID, value));
        }
    }

    public GuiComponentStat setLimits(int min, int max)
    {
        this.min = min;
        this.max = max;
        return this;
    }

    public GuiComponentStat setValue(int value)
    {
        this.value = value;
        return this;
    }

    @Override
    protected void doRender(Minecraft mc, int mouseX, int mouseY)
    {
        super.doRender(mc, mouseX, mouseY);

        //Calculate box size
        int bars = (max - min);
        int width = (getWidth() - GuiButton9px.SIZE * 2);
        int widthPerBar = (int) Math.floor(width / (float) bars);
        int extra = width - (widthPerBar * bars);

        //Set properties
        mc.getTextureManager().bindTexture(GuiStats.texture);

        //Render boxes
        int nextX = x() + GuiButton9px.SIZE + (extra / 2);
        for (int i = 0; i < bars; i++)
        {
            if(i <= value)
            {
                GL11.glColor3f(barColor.getRed() / 255f, barColor.getGreen() / 255f, barColor.getBlue() / 255f);
            }
            else
            {
                GL11.glColor4f(1f, 1f, 1f, 1f);
            }
            Render2DHelper.renderWithRepeatHorizontal(nextX, y() + 2, 182, 0, widthPerBar, 5, 2, 2, 3);
            nextX += widthPerBar;
        }
    }
}
