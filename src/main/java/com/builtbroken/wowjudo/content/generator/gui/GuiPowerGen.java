package com.builtbroken.wowjudo.content.generator.gui;

import com.builtbroken.mc.client.SharedAssets;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.mc.prefab.gui.GuiContainerBase;
import com.builtbroken.wowjudo.SurvivalMod;
import com.builtbroken.wowjudo.content.generator.TilePowerGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/8/2017.
 */
public class GuiPowerGen extends GuiContainerBase
{
    private TilePowerGenerator generator;

    public GuiPowerGen(EntityPlayer player, TilePowerGenerator generator)
    {
        super(new ContainerPowerGen(player, generator));
        this.generator = generator;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(f, mouseX, mouseY);
        for (Object object : inventorySlots.inventorySlots)
        {
            if (object instanceof Slot)
            {
                drawSlot((Slot) object);
            }
        }

        this.mc.renderEngine.bindTexture(SharedAssets.GUI_COMPONENTS);

        //TODO add estimated fuel time
        //TODO add fuel tank render
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        drawStringCentered(LanguageUtility.getLocal("tile." + SurvivalMod.PREFX + "powerGen.gui.name"), 88, 6);
        drawString(LanguageUtility.getLocal("tile." + SurvivalMod.PREFX + "powerGen.gui.inventory"), 8, 74);
    }
}
