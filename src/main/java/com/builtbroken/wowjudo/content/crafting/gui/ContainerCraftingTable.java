package com.builtbroken.wowjudo.content.crafting.gui;

import com.builtbroken.mc.prefab.gui.ContainerBase;
import com.builtbroken.wowjudo.content.crafting.TileEntityCraftingTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/8/2017.
 */
public class ContainerCraftingTable extends ContainerBase
{
    TileEntityCraftingTable craftingTable;

    public InventoryCraftingMatrix4x4 craftMatrix4x4;

    /** Work around for supporting smaller recipes */
    public InventoryCraftingMatrix3x3[] craftingMatrix3x3;

    public IInventory craftResult = new InventoryCraftResult();

    public ContainerCraftingTable(EntityPlayer player, TileEntityCraftingTable table)
    {
        super(player, table);
        this.craftingTable = table;

        //Crafting grid
        craftMatrix4x4 = new InventoryCraftingMatrix4x4(this, table);
        //Crafting grid slots
        // 0  1  2  3
        // 4  5  6  7
        // 8  9 10 11
        //12 13 14 15

        //Great the 4x4 up into 4 3x3 grids to map slots correctly
        craftingMatrix3x3 = new InventoryCraftingMatrix3x3[4]; //TODO automate process to allow for larger grids in the future
        craftingMatrix3x3[0] = new InventoryCraftingMatrix3x3(this, table,
                new int[]
                        {
                                0, 1, 2,
                                4, 5, 6,
                                8, 9, 10
                        }
        );
        craftingMatrix3x3[1] = new InventoryCraftingMatrix3x3(this, table,
                new int[]
                        {
                                1, 2, 3,
                                5, 6, 7,
                                9, 10, 11
                        }
        );
        craftingMatrix3x3[2] = new InventoryCraftingMatrix3x3(this, table,
                new int[]
                        {
                                4, 5, 6,
                                8, 9, 10,
                                12, 13, 14
                        }
        );
        craftingMatrix3x3[3] = new InventoryCraftingMatrix3x3(this, table,
                new int[]
                        {
                                5, 6, 7,
                                9, 10, 11,
                                13, 14, 15
                        }
        );

        for (int row = 0; row < 4; ++row)
        {
            for (int col = 0; col < 4; ++col)
            {
                this.addSlotToContainer(new Slot(this.craftMatrix4x4, col + row * 4, 20 + col * 18, 13 + row * 18));
            }
        }

        //Output slot
        this.addSlotToContainer(new SlotCraftingTable(player, table, this.craftMatrix4x4, this.craftResult, 0, 124, 39));

        //Secondary inventory
        for (int row = 0; row < 2; ++row)
        {
            for (int col = 0; col < 9; ++col)
            {
                this.addSlotToContainer(new Slot(table, col + row * 9 + 16, 8 + col * 18, 94 + row * 18));
            }
        }

        //Player inventory
        addPlayerInventory(player, 8, 135);

        //Init crafting grid
        this.onCraftMatrixChanged(this.craftMatrix4x4);
    }

    @Override
    public void onCraftMatrixChanged(IInventory _notUsed)
    {
        this.craftResult.setInventorySlotContents(0, null);
        //If our recipe is a 3x3 we need to fake the results
        if (!is4x4Recipe())
        {
            for (int i = 0; i < 4; i++)
            {
                ItemStack result = CraftingManager.getInstance().findMatchingRecipe(this.craftingMatrix3x3[i], craftingTable.getWorldObj());
                if (result != null && result.getItem() != null)
                {
                    this.craftResult.setInventorySlotContents(0, result);
                }
            }
        }
        else
        {
            this.craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix4x4, craftingTable.getWorldObj()));
        }
    }

    protected boolean is4x4Recipe()
    {
        int width = 0;
        int height = 0;
        int items = 0;

        for (int row = 0; row < 4; ++row)
        {
            int cWidth = 0;
            boolean hasItem = false;
            for (int col = 0; col < 4; ++col)
            {
                int slot = col + row * 4;

                //Count up items
                ItemStack stack = craftingTable.getInventory().getStackInSlot(slot);
                if (stack != null)
                {
                    items++;
                    cWidth++;
                    hasItem = true;
                }
            }

            if (cWidth > width)
            {
                width = cWidth;
            }
            if (hasItem)
            {
                height++;
            }
        }

        return items > 9 || width == 4 || height == 4;
    }

    @Override
    public boolean canInteractWith(EntityPlayer p_75145_1_)
    {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex)
    {
        //Constant to mark player inventory, based on crafting table
        final int playerInventoryStart = craftingTable.getSizeInventory();
        final int playerInventoryEnd = craftingTable.getSizeInventory() + 36;
        //final int playerToolbarStart = craftingTable.getSizeInventory() + 27;

        ItemStack itemstack = null;
        final Slot slot = (Slot) this.inventorySlots.get(slotIndex);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            //From output to player inventory
            if (slotIndex == 0)
            {
                if (!this.mergeItemStack(itemstack1, playerInventoryStart, playerInventoryEnd, true))
                {
                    return null;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            //From player inventory to secondary inventory
            else if (slotIndex >= TileEntityCraftingTable.SLOT_INVENTORY_START && slotIndex <= TileEntityCraftingTable.SLOT_INVENTORY_END)
            {
                if (!this.mergeItemStack(itemstack1, playerInventoryStart, playerInventoryEnd, false))
                {
                    return null;
                }
            }
            //From secondary to player inventory
            else if (slotIndex >= playerInventoryStart && slotIndex <= playerInventoryEnd)
            {
                if (!this.mergeItemStack(itemstack1, TileEntityCraftingTable.SLOT_INVENTORY_START, TileEntityCraftingTable.SLOT_INVENTORY_END, false))
                {
                    return null;
                }
            }
            //Default for anything else, merge to player inventory
            else if (!this.mergeItemStack(itemstack1, playerInventoryStart, playerInventoryEnd, false))
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

    @Override
    public boolean func_94530_a(ItemStack stack, Slot slot)
    {
        return slot.inventory != this.craftResult && super.func_94530_a(stack, slot);
    }
}
