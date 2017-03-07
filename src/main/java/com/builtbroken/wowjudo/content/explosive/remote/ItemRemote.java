package com.builtbroken.wowjudo.content.explosive.remote;

import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.wowjudo.JudoMod;
import com.builtbroken.wowjudo.content.explosive.tile.TileEntityExplosive;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/7/2017.
 */
public class ItemRemote extends Item
{
    public ItemRemote()
    {
        setHasSubtypes(true);
        setMaxStackSize(1);
        setNoRepair();

        setUnlocalizedName(JudoMod.PREFX + "explosiveRemote");
        setTextureName(JudoMod.PREFX + "C4_remote");
        setCreativeTab(JudoMod.creativeTab);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if (!world.isRemote)
        {
            Location location = getLinkLocation(stack);
            if (location != null)
            {
                if (location.isChunkLoaded())
                {
                    double distance = location.distance(player);
                    if (distance < 100) //TODO set via config
                    {
                        TileEntity tile = location.getTileEntity();
                        if (tile instanceof TileEntityExplosive)
                        {
                            ((TileEntityExplosive) tile).trigger(player);
                            player.addChatComponentMessage(new ChatComponentText("Explosives have been triggered!!"));
                        }
                    }
                    else
                    {
                        player.addChatComponentMessage(new ChatComponentText("Out of range! Limited to 100m, distance is " + distance));
                    }
                }
                else
                {
                    player.addChatComponentMessage(new ChatComponentText("That area of the map is not loaded!"));
                }
            }
            else
            {
                player.addChatComponentMessage(new ChatComponentText("You need to link to the explosive!"));
            }
        }
        return stack;
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        return false;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        if (!world.isRemote)
        {
            TileEntity tile = world.getTileEntity(x, y, z);
            //Null check to avoid clicking any machine that might be desired to open a GUI
            if (tile != null)
            {
                if (tile instanceof TileEntityExplosive)
                {
                    settLinkLocation(stack, new Location(world, x, y, z));
                    player.addChatComponentMessage(new ChatComponentText("Link set to: " + getLinkLocation(stack)));
                }
            }
            else
            {
                onItemRightClick(stack, world, player);
            }
        }
        return true;
    }

    @Override
    public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player)
    {
        return true;
    }

    public Location getLinkLocation(ItemStack stack)
    {
        if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("location"))
        {
            return new Location(stack.getTagCompound().getCompoundTag("location"));
        }
        return null;
    }

    public void settLinkLocation(ItemStack stack, Location location)
    {
        if (stack.getTagCompound() == null)
        {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound().setTag("location", location.toNBT());
    }
}
