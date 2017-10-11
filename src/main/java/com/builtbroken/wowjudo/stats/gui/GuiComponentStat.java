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
        super(id, x, y, 300, 50, "");
        this.barColor = barColor;
        this.packetID = packetID;

        increaseButton = add(GuiButton9px.newPlusButton(0, 0, 0));
        increaseButton.setRelativePosition(new HugXSide(this, 0, true));
        decreaseButton = add(GuiButton9px.newPlusButton(0, 0, 0));
        decreaseButton.setRelativePosition(new HugXSide(this, -GuiButton9px.SIZE, false));
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
            value = Math.min(max, value + 1);
            syncToServer();
        }
        else if (button == decreaseButton)
        {
            value = Math.max(min, value - 1);
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
        int bars = (max - min);
        int widthPerBar = getWidth() / bars;
        mc.getTextureManager().bindTexture(GuiStats.texture);
        for (int i = 0; i < widthPerBar; i++)
        {
            Render2DHelper.renderWithRepeatHorizontal(x() + i * widthPerBar, y(), 182, 0, widthPerBar, 9, 2, 2, 3);
        }
    }
}
