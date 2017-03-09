package com.builtbroken.wowjudo.content.crafting.gui;

import com.builtbroken.mc.client.SharedAssets;
import com.builtbroken.mc.prefab.gui.GuiContainerBase;
import com.builtbroken.wowjudo.content.crafting.TileEntityCraftingTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/8/2017.
 */
public class GuiCraftingTable extends GuiContainerBase
{
    public GuiCraftingTable(EntityPlayer player, TileEntityCraftingTable fire)
    {
        super(new ContainerCraftingTable(player, fire));
        this.baseTexture = SharedAssets.GUI_EMPTY_FILE;
        this.ySize = 166 + 18 * 3;
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
