package com.builtbroken.wowjudo.stats.gui;

import com.builtbroken.mc.prefab.gui.screen.GuiScreenBase;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/10/2017.
 */
public class GuiStats extends GuiScreenBase
{
    protected final EntityPlayer player;

    protected GuiFrameStats frameStats;

    public GuiStats(EntityPlayer player)
    {
        this.player = player;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        int guiWidth = 200;
        frameStats = add(new GuiFrameStats(0, (width / 2) - (guiWidth / 2), 100));
        frameStats.setWidth(200);
        frameStats.setHeight(height - 200);
    }
}
