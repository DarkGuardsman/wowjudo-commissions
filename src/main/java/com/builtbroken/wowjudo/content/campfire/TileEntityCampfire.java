package com.builtbroken.wowjudo.content.campfire;

import com.builtbroken.mc.api.tile.IGuiTile;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.network.packet.PacketTile;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.prefab.inventory.ExternalInventory;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import com.builtbroken.mc.prefab.items.ItemStackWrapper;
import com.builtbroken.mc.prefab.tile.entity.TileEntityInv;
import com.builtbroken.wowjudo.content.campfire.gui.ContainerCampFire;
import com.builtbroken.wowjudo.content.campfire.gui.GuiCampFire;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.HashMap;

/**
 * Simple camp fire entity
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/8/2017.
 */
public class TileEntityCampfire extends TileEntityInv<ExternalInventory> implements IPacketIDReceiver, IGuiTile
{
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_OUTPUT = 2;
    public static final int SLOT_FUEL = 1;

    public static final int COOK_TIMER = 200;

    public static final int[] INPUT_SLOTS = new int[]{SLOT_INPUT, SLOT_FUEL};
    public static final int[] OUTPUT_SLOTS = new int[]{SLOT_OUTPUT};

    /** Map of inputs to outputs */
    private static HashMap<ItemStackWrapper, FireRecipe> recipes = new HashMap();
    private static HashMap<ItemStackWrapper, FireRecipe> outputs = new HashMap();

    /** Remaining ticks on fuel */
    public int fuelTimer = 0;
    public int itemFuelTime = 100;
    /** Progress on cooking items */
    public int cookTimer = 0;
    public boolean hasRecipe = false;
    public boolean hasFuel = false;

    @Override
    public void updateEntity()
    {
        if (!worldObj.isRemote)
        {
            //If have no recipe, find recipe
            if (!hasRecipe && getStackInSlot(SLOT_INPUT) != null)
            {
                ItemStackWrapper stack = new ItemStackWrapper(getStackInSlot(SLOT_INPUT));
                if (recipes.containsKey(stack))
                {
                    hasRecipe = true;
                }
            }

            //If we have no fuel get fuel
            if (hasRecipe && fuelTimer <= 0 || !hasFuel)
            {
                hasFuel = false;
                ItemStack stack = getStackInSlot(SLOT_FUEL);
                if (stack != null)
                {
                    int burnTime = TileEntityFurnace.getItemBurnTime(stack);
                    if (burnTime > 0)
                    {
                        hasFuel = true;
                        itemFuelTime = burnTime;
                        //Only consume fuel if we are cooking items
                        if (hasRecipe)
                        {
                            fuelTimer += burnTime;
                            decrStackSize(SLOT_FUEL, 1);
                        }
                    }
                }
            }


            //If we have a recipe, tick recipe timer
            if (hasRecipe || hasFuel)
            {
                //If we have fuel run system
                if (fuelTimer > 0)
                {
                    fuelTimer--;
                    if (hasRecipe)
                    {
                        cookTimer++;
                        //If done, consume item and output
                        if (cookTimer >= COOK_TIMER)
                        {
                            //Validate input
                            ItemStack input = getStackInSlot(SLOT_INPUT);
                            if (input != null)
                            {
                                //Get recipe
                                ItemStackWrapper stack = new ItemStackWrapper(input);
                                FireRecipe recipe = recipes.get(stack);
                                //Get output slot content
                                ItemStack output = getStackInSlot(SLOT_OUTPUT);
                                //Only process if we have a recipe for output and we can output into slot
                                if (recipe != null && (output == null || InventoryUtility.stacksMatch(recipe.output, output) && InventoryUtility.roomLeftInSlot(this, SLOT_OUTPUT) >= recipe.output.stackSize))
                                {
                                    //TODO output XP
                                    //Output item
                                    if (output != null)
                                    {
                                        output.stackSize += recipe.output.stackSize;
                                        setInventorySlotContents(SLOT_OUTPUT, output);
                                    }
                                    else
                                    {
                                        setInventorySlotContents(SLOT_OUTPUT, recipe.output.copy());
                                    }

                                    //Consume item
                                    decrStackSize(SLOT_INPUT, 1);
                                    //Reset
                                    hasRecipe = false;
                                    cookTimer = 0;
                                }
                            }
                        }
                    }
                }
                //If timer hits zero we have consumed our fuel item
                else
                {
                    hasFuel = false;
                }
            }
            //If we have no recipe reset cook time
            else if (cookTimer > 0)
            {
                cookTimer = 0;
            }

            //Update client to sync render state
            Engine.instance.packetHandler.sendToAllAround(getDescPacket(), this);
        }
    }

    //==================================================
    //================== Save ==========================
    //==================================================

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        cookTimer = nbt.getInteger("cookTime");
        fuelTimer = nbt.getInteger("fuelTime");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setInteger("cookTime", cookTimer);
        nbt.setInteger("fuelTime", fuelTimer);
    }

    //==================================================
    //================== Packet ========================
    //==================================================

    @Override
    public boolean read(ByteBuf buf, int id, EntityPlayer player, PacketType type)
    {
        if (worldObj.isRemote)
        {
            if (id == 0)
            {
                this.cookTimer = buf.readInt();
                this.fuelTimer = buf.readInt();
                this.hasFuel = buf.readBoolean();
                this.itemFuelTime = buf.readInt();
                return true;
            }
        }
        return false;
    }

    @Override
    public Packet getDescriptionPacket()
    {
        return Engine.instance.packetHandler.toMCPacket(getDescPacket());
    }

    public PacketTile getDescPacket()
    {
        return new PacketTile(this, 0, cookTimer, fuelTimer, hasFuel, itemFuelTime);
    }

    //==================================================
    //================== IInventory ====================
    //==================================================

    @Override
    public void onInventoryChanged(int slot, ItemStack prev, ItemStack item)
    {
        if (worldObj != null && !worldObj.isRemote)
        {
            if (slot == SLOT_INPUT)
            {
                hasRecipe = false;
            }
            else if (slot == SLOT_FUEL)
            {
                hasFuel = false;
            }
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

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player)
    {
        return new ContainerCampFire(player, this);
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player)
    {
        return new GuiCampFire(player, this);
    }

    //==================================================
    //================== Recipes =======================
    //==================================================

    public static void addRecipe(Block block, ItemStack output, float xp)
    {
        addRecipe(new ItemStack(block), output, xp);
    }

    public static void addRecipe(Item item, ItemStack output, float xp)
    {
        addRecipe(new ItemStack(item), output, xp);
    }

    public static void addRecipe(ItemStack input, ItemStack output, float xp)
    {
        FireRecipe recipe = new FireRecipe(output, xp);
        recipes.put(new ItemStackWrapper(input), recipe);
        outputs.put(new ItemStackWrapper(output), recipe);
    }

    public static float getXp(ItemStack stack)
    {
        ItemStackWrapper wrapper = new ItemStackWrapper(stack);
        if (outputs.containsKey(wrapper))
        {
            return outputs.get(wrapper).xp;
        }
        return 0;
    }

    public static class FireRecipe
    {
        public final ItemStack output;
        public final float xp;

        public FireRecipe(ItemStack output, float xp)
        {
            this.output = output;
            this.xp = xp;
        }
    }
}
