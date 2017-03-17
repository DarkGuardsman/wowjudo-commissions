package com.builtbroken.wowjudo.content.campfire.gui;

import com.builtbroken.mc.client.SharedAssets;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.mc.prefab.gui.GuiContainerBase;
import com.builtbroken.wowjudo.SurvivalMod;
import com.builtbroken.wowjudo.content.campfire.TileEntityCampfire;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/8/2017.
 */
public class GuiCampFire extends GuiContainerBase
{
    private TileEntityCampfire campFire;

    public GuiCampFire(EntityPlayer player, TileEntityCampfire fire)
    {
        super(new ContainerCampFire(player, fire));
        this.campFire = fire;
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
        int x = 54;
        int y = 34;
        this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 18, 18 * 3, 18, 18);
        if(campFire.fuelTimer > 0)
        {
            int offsetY = (int)Math.floor(campFire.fuelTimer * 18f / (campFire.itemFuelTime + 0.0f));
            this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y + 17 - offsetY, 18, 18 * 3 - offsetY, 18, 1 + offsetY);
        }

        //Render arrow for crafting timer
        x = 84;
        y = 35;
        drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 18, 0, 22, 15);
        if(campFire.cookTimer > 0)
        {
            float p = campFire.cookTimer / (TileEntityCampfire.COOK_TIMER + 0.0f);
            drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 18, 15, (int)Math.floor(22 * p), 15);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        drawStringCentered(LanguageUtility.getLocal("tile." + SurvivalMod.PREFX + "campFire.gui.name"), 88, 6);
        drawString(LanguageUtility.getLocal("tile." + SurvivalMod.PREFX + "campFire.gui.inventory"), 8, 74);
    }
}
