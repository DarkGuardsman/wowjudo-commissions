package com.builtbroken.wowjudo.content.campfire;

import com.builtbroken.mc.prefab.gui.GuiContainerBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/8/2017.
 */
public class GuiCampFire extends GuiContainerBase
{
    public GuiCampFire(EntityPlayer player, TileEntityCampfire fire)
    {
        super(new ContainerCampFire(player, fire));
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
    }
}
