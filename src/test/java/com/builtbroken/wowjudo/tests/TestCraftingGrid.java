package com.builtbroken.wowjudo.tests;

import com.builtbroken.mc.testing.junit.AbstractTest;
import com.builtbroken.mc.testing.junit.VoltzTestRunner;
import com.builtbroken.mc.testing.junit.server.FakeDedicatedServer;
import com.builtbroken.mc.testing.junit.testers.TestPlayer;
import com.builtbroken.mc.testing.junit.world.FakeWorldServer;
import com.builtbroken.wowjudo.content.crafting.TileEntityCraftingTable;
import com.builtbroken.wowjudo.content.crafting.gui.ContainerCraftingTable;
import com.builtbroken.wowjudo.content.crafting.gui.InventoryCraftingMatrix3x3;
import com.mojang.authlib.GameProfile;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 8/22/2017.
 */
@RunWith(VoltzTestRunner.class)
public class TestCraftingGrid extends AbstractTest
{
    /** MC server instance for the entire class file to use */
    protected MinecraftServer server;
    /** World server to build tests inside, make sure to clean up as its used over all tests in this class */
    protected FakeWorldServer world;
    /** Tester that has not choice in what to test, test between tests but not deleted. Make sure to cleanup non-vanilla data between tests */
    protected TestPlayer player;

    ShapedOreRecipe testRecipe;

    @Override
    public void setUpForEntireClass()
    {
        super.setUpForEntireClass();
        server = new FakeDedicatedServer(new File(FakeWorldServer.baseFolder, "WJ_CraftingTableTest"));
        world = FakeWorldServer.newWorld(server, "WJ_CraftingTableTest");
        player = new TestPlayer(server, world, new GameProfile(null, "TileTester"));

        //Load ore dictionary
        OreDictionary.initVanillaEntries();
        testRecipe = new ShapedOreRecipe(Blocks.furnace, "CCC", "C C", "CCC", 'C', Blocks.cobblestone);
    }

    @Test
    public void test3x3GridOne()
    {
        TileEntityCraftingTable tile = new TileEntityCraftingTable();
        ContainerCraftingTable containerCraftingTable = new ContainerCraftingTable(player, tile);

        tile.setInventorySlotContents(0, new ItemStack(Blocks.cobblestone));
        tile.setInventorySlotContents(1, new ItemStack(Blocks.cobblestone));
        tile.setInventorySlotContents(2, new ItemStack(Blocks.cobblestone));

        tile.setInventorySlotContents(4, new ItemStack(Blocks.cobblestone));
        tile.setInventorySlotContents(6, new ItemStack(Blocks.cobblestone));

        tile.setInventorySlotContents(8, new ItemStack(Blocks.cobblestone));
        tile.setInventorySlotContents(9, new ItemStack(Blocks.cobblestone));
        tile.setInventorySlotContents(10, new ItemStack(Blocks.cobblestone));

        tryGrid(tile, containerCraftingTable.craftingMatrix3x3[0]);
    }

    @Test
    public void test3x3GridTwo()
    {
        TileEntityCraftingTable tile = new TileEntityCraftingTable();
        ContainerCraftingTable containerCraftingTable = new ContainerCraftingTable(player, tile);

        tile.setInventorySlotContents(1, new ItemStack(Blocks.cobblestone));
        tile.setInventorySlotContents(2, new ItemStack(Blocks.cobblestone));
        tile.setInventorySlotContents(3, new ItemStack(Blocks.cobblestone));

        tile.setInventorySlotContents(5, new ItemStack(Blocks.cobblestone));
        tile.setInventorySlotContents(7, new ItemStack(Blocks.cobblestone));

        tile.setInventorySlotContents(9, new ItemStack(Blocks.cobblestone));
        tile.setInventorySlotContents(10, new ItemStack(Blocks.cobblestone));
        tile.setInventorySlotContents(11, new ItemStack(Blocks.cobblestone));

        tryGrid(tile, containerCraftingTable.craftingMatrix3x3[1]);
    }

    @Test
    public void test3x3GridThree()
    {
        TileEntityCraftingTable tile = new TileEntityCraftingTable();
        ContainerCraftingTable containerCraftingTable = new ContainerCraftingTable(player, tile);

        tile.setInventorySlotContents(4, new ItemStack(Blocks.cobblestone));
        tile.setInventorySlotContents(5, new ItemStack(Blocks.cobblestone));
        tile.setInventorySlotContents(6, new ItemStack(Blocks.cobblestone));

        tile.setInventorySlotContents(8, new ItemStack(Blocks.cobblestone));
        tile.setInventorySlotContents(10, new ItemStack(Blocks.cobblestone));

        tile.setInventorySlotContents(12, new ItemStack(Blocks.cobblestone));
        tile.setInventorySlotContents(13, new ItemStack(Blocks.cobblestone));
        tile.setInventorySlotContents(14, new ItemStack(Blocks.cobblestone));

        tryGrid(tile, containerCraftingTable.craftingMatrix3x3[2]);
    }

    @Test
    public void test3x3GridFour()
    {
        TileEntityCraftingTable tile = new TileEntityCraftingTable();
        ContainerCraftingTable containerCraftingTable = new ContainerCraftingTable(player, tile);

        tile.setInventorySlotContents(5, new ItemStack(Blocks.cobblestone));
        tile.setInventorySlotContents(6, new ItemStack(Blocks.cobblestone));
        tile.setInventorySlotContents(7, new ItemStack(Blocks.cobblestone));

        tile.setInventorySlotContents(9, new ItemStack(Blocks.cobblestone));
        tile.setInventorySlotContents(11, new ItemStack(Blocks.cobblestone));

        tile.setInventorySlotContents(13, new ItemStack(Blocks.cobblestone));
        tile.setInventorySlotContents(14, new ItemStack(Blocks.cobblestone));
        tile.setInventorySlotContents(15, new ItemStack(Blocks.cobblestone));

        tryGrid(tile, containerCraftingTable.craftingMatrix3x3[3]);
    }

    public void tryGrid(TileEntityCraftingTable tile, InventoryCraftingMatrix3x3 craftingGrid)
    {
        System.out.println("==Inventory==");
        for (int i = 0; i < 16; i++)
        {
            if (i % 4 == 0)
            {
                System.out.println();
            }
            System.out.println("[" + i + "]" + tile.getInventory().getStackInSlot(i));
        }

        System.out.println("\n==Grid==");
        for (int i = 0; i < 9; i++)
        {
            if (i % 3 == 0)
            {
                System.out.println();
            }
            System.out.println("[" + i + "]" + craftingGrid.getStackInSlot(i));
        }

        for (int i = 0; i < 9; i++)
        {
            if (i != 4)
            {
                assertNotNull("contents of slot was null, slot = " + i, craftingGrid.getStackInSlot(i));
                assertTrue("contents of slot are not correct, slot = " + i, craftingGrid.getStackInSlot(i).getItem() == Item.getItemFromBlock(Blocks.cobblestone));
            }
            else
            {
                assertNull("contents of slot should be null, slot = " + i, craftingGrid.getStackInSlot(i));
            }
        }

        for (int row = 0; row < 3; row++)
        {
            for (int col = 0; col < 3; col++)
            {
                ItemStack stack = craftingGrid.getStackInRowAndColumn(row, col);
                if (row == 1 && col == 1)
                {
                    assertNull("contents of slot should be null, row = " + row + " col = " + col, stack);
                }
                else
                {
                    assertNotNull("contents of slot was null, row = " + row + " col = " + col, stack);
                    assertTrue("contents of slot are not correct, row = " + row + " col = " + col, stack.getItem() == Item.getItemFromBlock(Blocks.cobblestone));
                }

            }
        }

        assertTrue("Recipe did not match", testRecipe.matches(craftingGrid, world));
    }
}
