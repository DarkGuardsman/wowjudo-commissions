package com.builtbroken.wowjudo.content.crafting.gui;

import com.builtbroken.mc.prefab.gui.ContainerBase;
import com.builtbroken.wowjudo.content.campfire.gui.SlotCampFire;
import com.builtbroken.wowjudo.content.crafting.TileEntityCraftingTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/8/2017.
 */
public class ContainerCraftingTable extends ContainerBase
{
    TileEntityCraftingTable craftingTable;

    public ContainerCraftingTable(EntityPlayer player, TileEntityCraftingTable campFire)
    {
        super(player, campFire);
        this.craftingTable = campFire;
        int slot = 0;
        for (int i = 0; i < 4; i++)
        {
            for (int j = 0; j < 4; j++)
            {
                addSlotToContainer(new Slot(campFire, slot++, i * 18 + 30, j * 18 + 17));
            }
        }
        this.addSlotToContainer(new SlotCampFire(player, campFire, slot++, 116, 35));
        for (int i = 0; i < 2; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                addSlotToContainer(new Slot(campFire, slot++, 8 + j * 18, 90 + i * 18));
            }
        }
        addPlayerInventory(player, 8, 90 + 3 * 18);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot) this.inventorySlots.get(slotIndex);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (slotIndex >= craftingTable.getSizeInventory())
            {
                if (!this.mergeItemStack(itemstack1, TileEntityCraftingTable.SLOT_INVENTORY_START, craftingTable.getSizeInventory(), false))
                {
                    return null;
                }
            }
            //From inventory
            else if (!this.mergeItemStack(itemstack1, craftingTable.getSizeInventory(), craftingTable.getSizeInventory() + 27 + 9, false))
            {
                return null;
            }

            if (itemstack1.stackSize == 0)
            {
                slot.putStack((ItemStack) null);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize)
            {
                return null;
            }

            slot.onPickupFromSlot(player, itemstack1);
        }

        return itemstack;
    }
}
