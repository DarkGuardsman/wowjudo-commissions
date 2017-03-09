package com.builtbroken.wowjudo.content.crafting.gui;

import com.builtbroken.wowjudo.content.crafting.TileEntityCraftingTable;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/9/2017.
 */
public class InventoryCraftingMatrix extends InventoryCrafting
{
    public final TileEntityCraftingTable table;

    public InventoryCraftingMatrix(Container container, TileEntityCraftingTable table)
    {
        super(container, 4, 4);
        this.table = table;
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return slot >= this.getSizeInventory() ? null : table.getStackInSlot(slot);
    }

    @Override
    public ItemStack getStackInRowAndColumn(int row, int col)
    {
        if (row >= 0 && row < 4)
        {
            int k = row + col * 4;
            return this.getStackInSlot(k);
        }
        else
        {
            return null;
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot)
    {
        return table.getStackInSlotOnClosing(slot);
    }

    @Override
    public ItemStack decrStackSize(int slot, int a)
    {
        return table.decrStackSize(slot, a);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
        table.setInventorySlotContents(slot, stack);
    }
}
