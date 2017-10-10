package com.builtbroken.wowjudo.stats.gui;

import com.builtbroken.mc.prefab.gui.components.frame.GuiFrame;
import com.builtbroken.mc.prefab.gui.pos.HugXSide;
import com.builtbroken.wowjudo.stats.StatEntityProperty;
import com.builtbroken.wowjudo.stats.StatHandler;
import com.builtbroken.wowjudo.stats.network.PacketStatSet;
import net.minecraft.entity.player.EntityPlayer;

import java.awt.*;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/10/2017.
 */
public class GuiFrameStats extends GuiFrame<GuiFrameStats>
{
    GuiComponentStat healthStat;

    public GuiFrameStats(int id, int x, int y)
    {
        super(id, x, y);
    }

    @Override
    public void initGui()
    {
        super.initGui();

        healthStat = add(new GuiComponentStat(0, 0, 10, Color.RED, PacketStatSet.HEALTH));
        healthStat.setRelativePosition(new HugXSide(this, 10, true));
        healthStat.setLimits(0, StatHandler.HEALTH_MAX);

        updateValues();
    }

    public void updateValues()
    {
        StatEntityProperty property = StatHandler.getPropertyForEntity(player());
        if (property != null)
        {
            healthStat.setValue(property.getHpIncrease());
        }
    }

    public EntityPlayer player()
    {
        if (getHost() instanceof GuiStats)
        {
            return ((GuiStats) getHost()).player;
        }
        return null;
    }
}
