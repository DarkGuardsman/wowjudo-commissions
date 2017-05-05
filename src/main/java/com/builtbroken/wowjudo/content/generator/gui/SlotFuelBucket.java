package com.builtbroken.wowjudo.content.generator.gui;

import com.builtbroken.wowjudo.content.generator.TilePowerGenerator;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Slot to restrict input slot to containers containing fuel
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 5/5/2017.
 */
public class SlotFuelBucket extends Slot
{
    public SlotFuelBucket(IInventory inventory, int slotID, int x, int y)
    {
        super(inventory, slotID, x, y);
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
        return TilePowerGenerator.isFuelBucket(stack);
    }
}