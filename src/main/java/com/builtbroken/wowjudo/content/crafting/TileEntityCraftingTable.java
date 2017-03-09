package com.builtbroken.wowjudo.content.crafting;

import com.builtbroken.mc.api.tile.IGuiTile;
import com.builtbroken.mc.prefab.inventory.ExternalInventory;
import com.builtbroken.mc.prefab.tile.entity.TileEntityInv;
import com.builtbroken.wowjudo.content.crafting.gui.ContainerCraftingTable;
import com.builtbroken.wowjudo.content.crafting.gui.GuiCraftingTable;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/9/2017.
 */
public class TileEntityCraftingTable extends TileEntityInv<ExternalInventory> implements IGuiTile
{
    public static final int SLOT_CRAFTING_START = 0;
    public static final int SLOT_CRAFTING_END = 15;
    public static final int SLOT_INVENTORY_START = SLOT_CRAFTING_END + 1;
    public static final int SLOT_INVENTORY_END = SLOT_INVENTORY_START + 18;

    @Override
    protected ExternalInventory createInventory()
    {
        return new ExternalInventory(this, SLOT_INVENTORY_END + 1);
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player)
    {
        return new ContainerCraftingTable(player, this);
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player)
    {
        return new GuiCraftingTable(player, this);
    }
}
