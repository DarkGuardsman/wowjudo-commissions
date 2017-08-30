package com.builtbroken.wowjudo.content.furnace.gui;

import com.builtbroken.mc.prefab.gui.ContainerBase;
import com.builtbroken.mc.prefab.gui.slot.SlotOutput;
import com.builtbroken.wowjudo.content.furnace.TileDualFurnace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 8/30/2017.
 */
public class ContainerDualFurnace extends ContainerBase<TileDualFurnace>
{
    public ContainerDualFurnace(EntityPlayer player, TileDualFurnace node)
    {
        super(player, node);

        int x = 16;
        //Input slots
        this.addSlotToContainer(new Slot(node.getInventory(), TileDualFurnace.INPUT_SLOT_1, x, 17));
        this.addSlotToContainer(new Slot(node.getInventory(), TileDualFurnace.INPUT_SLOT_2, x + 24, 17));

        //Fuel slots
        this.addSlotToContainer(new Slot(node.getInventory(), TileDualFurnace.FUEL_SLOT_1, x, 57));
        this.addSlotToContainer(new Slot(node.getInventory(), TileDualFurnace.FUEL_SLOT_2, x + 24, 57));

        x = 100;
        this.addSlotToContainer(new SlotOutput(node.getInventory(), TileDualFurnace.OUTPUT_SLOT_1, x, 38));
        this.addSlotToContainer(new SlotOutput(node.getInventory(), TileDualFurnace.OUTPUT_SLOT_2, x + 30, 38));

        //Inventory
        addPlayerInventory(player);
    }
}
