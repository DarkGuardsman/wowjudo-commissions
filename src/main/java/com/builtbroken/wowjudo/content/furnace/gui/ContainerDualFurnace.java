package com.builtbroken.wowjudo.content.furnace.gui;

import com.builtbroken.mc.prefab.gui.ContainerBase;
import com.builtbroken.mc.prefab.gui.slot.SlotOutput;
import com.builtbroken.wowjudo.content.furnace.TileDualFurnace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;

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

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex)
    {
        ItemStack itemstack = null;
        final Slot slot = (Slot)this.inventorySlots.get(slotIndex);

        final int inventoryEnd = 6; //start point for player inventory, end + 1 for tile inventory
        final int playerInvEnd = inventoryEnd + 27;
        final int playerHotbarEnd = playerInvEnd + 9;

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (slotIndex == TileDualFurnace.OUTPUT_SLOT_1 || slotIndex == TileDualFurnace.OUTPUT_SLOT_2)
            {
                if (!this.mergeItemStack(itemstack1, inventoryEnd, playerHotbarEnd, true))
                {
                    return null;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            //Move from player inventory to furnace
            else if (slotIndex >= inventoryEnd)
            {
                //Input slots
                if (FurnaceRecipes.smelting().getSmeltingResult(itemstack1) != null)
                {
                    if (!this.mergeItemStack(itemstack1, TileDualFurnace.INPUT_SLOT_1, TileDualFurnace.INPUT_SLOT_2 + 1, false))
                    {
                        return null;
                    }
                }
                //Fuel slots
                else if (TileEntityFurnace.isItemFuel(itemstack1))
                {
                    if (!this.mergeItemStack(itemstack1, TileDualFurnace.FUEL_SLOT_1, TileDualFurnace.FUEL_SLOT_2 + 1, false))
                    {
                        return null;
                    }
                }
                //Hotbar
                else if (slotIndex >= inventoryEnd && slotIndex < playerInvEnd)
                {
                    if (!this.mergeItemStack(itemstack1, playerInvEnd, playerHotbarEnd, false))
                    {
                        return null;
                    }
                }
                //Main inventory
                else if (slotIndex >= playerInvEnd && slotIndex < playerHotbarEnd && !this.mergeItemStack(itemstack1, inventoryEnd, playerHotbarEnd, false))
                {
                    return null;
                }
            }
            //Fail state
            else if (!this.mergeItemStack(itemstack1, inventoryEnd, playerHotbarEnd, false))
            {
                return null;
            }

            if (itemstack1.stackSize <= 0)
            {
                slot.putStack((ItemStack)null);
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
