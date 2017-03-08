package com.builtbroken.wowjudo.content.campfire;

import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.prefab.inventory.ExternalInventory;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import com.builtbroken.mc.prefab.items.ItemStackWrapper;
import com.builtbroken.mc.prefab.tile.entity.TileEntityInv;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.HashMap;

/**
 * Simple camp fire entity
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/8/2017.
 */
public class TileEntityCampfire extends TileEntityInv<ExternalInventory> implements IPacketIDReceiver
{
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_OUTPUT = 2;
    public static final int SLOT_FUEL = 1;

    public static final int[] INPUT_SLOTS = new int[]{SLOT_INPUT, SLOT_FUEL};
    public static final int[] OUTPUT_SLOTS = new int[]{SLOT_OUTPUT};

    /** Map of inputs to outputs */
    private static HashMap<ItemStackWrapper, ItemStack> recipes = new HashMap();

    /** Remaining ticks on fuel */
    public int fuelTimer = 0;
    /** Progress on cooking items */
    public int cookTimer = 0;

    public boolean doUpdateClient = false;
    public boolean hasRecipe = false;

    @Override
    public void updateEntity()
    {
        if (!worldObj.isRemote)
        {
            //If we have no fuel get fuel
            if (fuelTimer <= 0)
            {
                ItemStack stack = getStackInSlot(SLOT_FUEL);
                if (stack != null)
                {
                    int burnTime = TileEntityFurnace.getItemBurnTime(stack);
                    if (burnTime > 0)
                    {
                        fuelTimer += burnTime;
                        decrStackSize(SLOT_FUEL, 1);
                        doUpdateClient = true;
                    }
                }
            }

            //If we have fuel run system
            if (fuelTimer > 0)
            {
                fuelTimer--;
                if(fuelTimer <= 0)
                {
                    doUpdateClient = true;
                }

                //If have no recipe, find recipe
                if (!hasRecipe && getStackInSlot(SLOT_INPUT) != null)
                {
                    ItemStackWrapper stack = new ItemStackWrapper(getStackInSlot(SLOT_INPUT));
                    if (recipes.containsKey(stack))
                    {
                        hasRecipe = true;
                        doUpdateClient = true;
                    }
                }

                //If we have a recipe, tick recipe timer
                if (hasRecipe)
                {
                    cookTimer++;
                    //If done, consume item and output
                    if (cookTimer >= 200)
                    {
                        ItemStack input = getStackInSlot(SLOT_INPUT);
                        if (input != null)
                        {
                            ItemStackWrapper stack = new ItemStackWrapper(input);
                            ItemStack recipe = recipes.get(stack);
                            ItemStack output = getStackInSlot(SLOT_OUTPUT);
                            if (recipe != null && (output == null || InventoryUtility.stacksMatch(recipe, output) && InventoryUtility.roomLeftInSlot(this, SLOT_OUTPUT) >= recipe.stackSize))
                            {
                                if (output != null)
                                {
                                    output.stackSize += recipe.stackSize;
                                    setInventorySlotContents(SLOT_OUTPUT, output);
                                }
                                else
                                {
                                    setInventorySlotContents(SLOT_OUTPUT, recipe.copy());
                                }
                                decrStackSize(SLOT_INPUT, 1);
                                cookTimer = 0;
                                doUpdateClient = true;
                            }
                        }
                    }
                }
                else
                {
                    cookTimer = 0;
                }
            }

            //Send description packet
            if (doUpdateClient)
            {
                doUpdateClient = false;
                //TODO send description packet
            }
        }
    }

    //==================================================
    //================== Save ==========================
    //==================================================

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        if (nbt.hasKey("inventory"))
        {
            NBTTagCompound tag = nbt.getCompoundTag("inventory");
            getInventory().load(tag);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        if (inventory_module != null)
        {
            NBTTagCompound tag = new NBTTagCompound();
            getInventory().save(tag);
            nbt.setTag("inventory", tag);
        }
    }

    //==================================================
    //================== Packet ========================
    //==================================================

    @Override
    public boolean read(ByteBuf buf, int id, EntityPlayer player, PacketType type)
    {
        return false;
    }

    //==================================================
    //================== IInventory ====================
    //==================================================

    @Override
    public void onInventoryChanged(int slot, ItemStack prev, ItemStack item)
    {
        if (!worldObj.isRemote && slot == SLOT_INPUT)
        {
            hasRecipe = false;
            doUpdateClient = true;
        }
    }

    @Override
    protected ExternalInventory createInventory()
    {
        return new ExternalInventory(this, 3);
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side)
    {
        if (side == 0)
        {
            return OUTPUT_SLOTS;
        }
        return INPUT_SLOTS;
    }

    @Override
    public boolean canStore(ItemStack stack, int slot, ForgeDirection side)
    {
        if (slot == SLOT_INPUT)
        {
            //TODO implement when recipes are added
        }
        else if (slot == SLOT_FUEL)
        {
            return TileEntityFurnace.isItemFuel(stack);
        }
        return false;
    }

    @Override
    public boolean canRemove(ItemStack stack, int slot, ForgeDirection side)
    {
        return slot == SLOT_OUTPUT;
    }

    //==================================================
    //================== Recipes =======================
    //==================================================

    public static void addRecipe(Block block, ItemStack output)
    {
        recipes.put(new ItemStackWrapper(block), output);
    }

    public static void addRecipe(Item item, ItemStack output)
    {
        recipes.put(new ItemStackWrapper(item), output);
    }

    public static void addRecipe(ItemStack input, ItemStack output)
    {
        recipes.put(new ItemStackWrapper(input), output);
    }
}
