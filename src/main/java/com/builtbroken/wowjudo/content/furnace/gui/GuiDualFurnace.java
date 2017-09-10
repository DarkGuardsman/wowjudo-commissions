package com.builtbroken.wowjudo.content.furnace.gui;

import com.builtbroken.mc.client.SharedAssets;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.mc.prefab.gui.GuiContainerBase;
import com.builtbroken.wowjudo.SurvivalMod;
import com.builtbroken.wowjudo.content.furnace.TileDualFurnace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 8/30/2017.
 */
public class GuiDualFurnace extends GuiContainerBase
{
    TileDualFurnace furnace;

    public GuiDualFurnace(EntityPlayer player, TileDualFurnace furnace)
    {
        super(new ContainerDualFurnace(player, furnace));
        this.furnace = furnace;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        drawStringCentered(LanguageUtility.getLocal("tile." + SurvivalMod.PREFX + "furnace.gui.grid"), xSize / 2, 5);
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

        //Render fire for fuel timer
        int x = 14;
        int y = 36;
        renderFurnaceCookFire(x, y, furnace.burnTimer1, furnace.burnTimerItem1);
        renderFurnaceCookFire(x + 24, y, furnace.burnTimer2, furnace.burnTimerItem2);

        //Render arrow for crafting timer
        x = 66;
        y = 38;
        renderFurnaceCookArrow(x, y, furnace.cookTime, TileDualFurnace.MAX_COOK_TIMER);
    }
}
