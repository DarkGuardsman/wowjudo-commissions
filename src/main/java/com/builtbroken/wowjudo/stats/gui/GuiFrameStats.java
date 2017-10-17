package com.builtbroken.wowjudo.stats.gui;

import com.builtbroken.jlib.data.Colors;
import com.builtbroken.mc.prefab.gui.components.GuiLabel;
import com.builtbroken.mc.prefab.gui.components.frame.GuiFrame;
import com.builtbroken.mc.prefab.gui.pos.HugXSide;
import com.builtbroken.mc.prefab.gui.pos.size.GuiRelativeSize;
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
    GuiComponentStat speedStat;
    GuiComponentStat damageStat;
    GuiComponentStat foodStat;
    GuiComponentStat armorStat;
    GuiComponentStat airStat;

    public GuiFrameStats(int id, int x, int y)
    {
        super(id, x, y);
    }

    @Deprecated
    protected Color getBackgroundColor()
    {
        return Colors.GREY.color;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        int index = 0;
        int x = 47;
        int y_spacer = 20;

        healthStat = addStat(0, x, (index++) * y_spacer + 10, PacketStatSet.HEALTH, Color.RED);
        armorStat = addStat(1, x, (index++) * y_spacer + 10, PacketStatSet.ARMOR, Color.BLUE);
        damageStat = addStat(2, x, (index++) * y_spacer + 10, PacketStatSet.SPEED, Color.MAGENTA);
        speedStat = addStat(3, x, (index++) * y_spacer + 10, PacketStatSet.SPEED, Color.YELLOW);
        foodStat = addStat(4, x, (index++) * y_spacer + 10, PacketStatSet.SPEED, Color.GREEN);
        airStat = addStat(5, x, (index++) * y_spacer + 10, PacketStatSet.SPEED, Color.CYAN);

        index = 0;
        x = 3;
        addLabel("Health:", x, (index++) * y_spacer + 10);
        addLabel("Armor:", x, (index++) * y_spacer + 10);
        addLabel("Damage:", x, (index++) * y_spacer + 10);
        addLabel("Speed:", x, (index++) * y_spacer + 10);
        addLabel("Food:", x, (index++) * y_spacer + 10);
        addLabel("Air:", x, (index++) * y_spacer + 10);

        updateValues();
    }

    protected void addLabel(String name, int x, int y)
    {
        GuiLabel label = add(new GuiLabel(x, y, name));
        label.setRelativePosition(new HugXSide(this, x, true).setYOffset(y));
    }

    protected GuiComponentStat addStat(int id, int x, int y, int packetID, Color color)
    {
        GuiComponentStat component = add(new GuiComponentStat(id, 0, y, color, packetID));
        component.setRelativePosition(new HugXSide(this, x, true).setYOffset(y));
        component.setRelativeSize(new GuiRelativeSize(this, -50, 9).setUseHostHeight(false));
        return component;
    }

    public void updateValues()
    {
        StatEntityProperty property = StatHandler.getPropertyForEntity(player());
        if (property != null)
        {
            healthStat.setLimits(0, StatHandler.HEALTH_MAX);
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
