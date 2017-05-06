package com.builtbroken.wowjudo.content.generator;

import com.builtbroken.jlib.lang.StringHelpers;
import com.builtbroken.mc.api.tile.access.IGuiTile;
import com.builtbroken.mc.api.tile.access.IRotation;
import com.builtbroken.mc.api.tile.provider.ITankProvider;
import com.builtbroken.mc.codegen.annotations.ExternalInventoryWrapped;
import com.builtbroken.mc.codegen.annotations.MultiBlockWrapped;
import com.builtbroken.mc.codegen.annotations.TankProviderWrapped;
import com.builtbroken.mc.codegen.annotations.TileWrapped;
import com.builtbroken.mc.imp.transform.region.Cube;
import com.builtbroken.mc.lib.energy.UniversalEnergySystem;
import com.builtbroken.mc.lib.world.map.TileMapRegistry;
import com.builtbroken.mc.lib.world.radar.RadarMap;
import com.builtbroken.mc.lib.world.radar.data.RadarObject;
import com.builtbroken.mc.lib.world.radar.data.RadarTile;
import com.builtbroken.mc.prefab.inventory.ExternalInventory;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import com.builtbroken.mc.prefab.tile.logic.TileMachineNode;
import com.builtbroken.wowjudo.SurvivalMod;
import com.builtbroken.wowjudo.content.generator.gui.ContainerPowerGen;
import com.builtbroken.wowjudo.content.generator.gui.GuiPowerGen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 5/4/2017.
 */
@TileWrapped(className = "TileWrapperPowerGenerator")
@ExternalInventoryWrapped()
@TankProviderWrapped()
@MultiBlockWrapped()
public class TilePowerGenerator extends TileMachineNode<ExternalInventory> implements ITankProvider, IRotation, IGuiTile
{
    public static final List<String> supportedFluids = new ArrayList();
    public static final List<String> supportedTiles = new ArrayList();

    public static final int BUCKET_INPUT_SLOT = 0;
    public static final int BUCKET_OUTPUT_SLOT = 1;

    public static int fuelConsumedPerTick = 10;
    public static int powerProviderRange = 100;

    static
    {
        supportedFluids.add("fuel");
        supportedTiles.add("com.builtbroken.armory.content.sentry.tile.TileSentry");
    }

    protected boolean isPowered = false;

    protected FluidTank tank = new FluidTank(10 * FluidContainerRegistry.BUCKET_VOLUME);

    private ForgeDirection dirCache;
    private Cube areaOfEffect;

    public TilePowerGenerator()
    {
        super("machine.power.gen", SurvivalMod.DOMAIN);
    }

    @Override
    protected ExternalInventory createInventory()
    {
        return new ExternalInventory(this, 2);
    }

    @Override
    public void update(long tick)
    {
        if (isServer())
        {
            //Clear invalid fluids
            if (tank.getFluid() != null && tank.getFluid().getFluid() != SurvivalMod.fuel)
            {
                SurvivalMod.instance.logger().info("Error clearing invalid fluid '" + tank.getFluid() + "' found in " + this);
                tank.drain(Integer.MAX_VALUE, true);
            }

            //Fill tank from input items
            if (!isFull())
            {
                ItemStack bucketStack = getInventory().getStackInSlot(BUCKET_INPUT_SLOT);
                if (bucketStack != null)
                {
                    if (bucketStack.getItem() instanceof IFluidContainerItem)
                    {
                        FluidStack fluidStack = ((IFluidContainerItem) bucketStack.getItem()).getFluid(bucketStack);
                        if (fluidStack != null && fluidStack.getFluid() == SurvivalMod.fuel)
                        {
                            int room = tank.getCapacity() - tank.getFluidAmount();
                        }
                    }
                    else if (FluidContainerRegistry.isFilledContainer(bucketStack))
                    {
                        FluidStack fluidStack = FluidContainerRegistry.getFluidForFilledItem(bucketStack);
                        if (fluidStack != null && fluidStack.getFluid() == SurvivalMod.fuel)
                        {
                            int room = tank.getCapacity() - tank.getFluidAmount();
                            if (room >= fluidStack.amount) //Ensure can take entire bucket to prevent issues
                            {
                                ItemStack container = FluidContainerRegistry.drainFluidContainer(bucketStack.copy());
                                ItemStack output = getInventory().getStackInSlot(BUCKET_OUTPUT_SLOT);
                                if (container != null && (output == null || InventoryUtility.stacksMatch(container, output) && InventoryUtility.roomLeftInSlot(getInventory(), BUCKET_OUTPUT_SLOT) >= 1)
                                {
                                    //Decrease input
                                    getInventory().decrStackSize(BUCKET_INPUT_SLOT, 1);

                                    //Output empty container
                                    if (output == null)
                                    {
                                        output = container.copy();
                                    }
                                    else
                                    {
                                        output.stackSize++;
                                    }
                                    getInventory().setInventorySlotContents(BUCKET_OUTPUT_SLOT, output);
                                }
                            }
                        }
                    }
                }
            }

            //Cycle power
            isPowered = false;

            //Consume full, update power state
            if (hasFuel())
            {
                if (tank.getFluidAmount() > fuelConsumedPerTick)
                {
                    isPowered = true;
                }
                tank.drain(fuelConsumedPerTick, true);
            }

            //Power devices nearby
            if (isPowered)
            {
                if (areaOfEffect == null)
                {
                    areaOfEffect = new Cube(toPos().sub(powerProviderRange), toPos().add(powerProviderRange)).cropToWorld();
                }

                long startTime = System.nanoTime();

                RadarMap map = TileMapRegistry.getRadarMapForDim(world().provider.dimensionId);
                List<RadarObject> objects = map.getRadarObjects(areaOfEffect, true);
                for (RadarObject object : objects)
                {
                    if (object instanceof RadarTile)
                    {
                        TileEntity tileEntity = ((RadarTile) object).tile;
                        if (tileEntity != null && tileEntity != getHost())
                        {
                            if (supportedTiles.contains(tileEntity.getClass().getName()))
                            {
                                UniversalEnergySystem.fill(tick, ForgeDirection.UNKNOWN, Integer.MAX_VALUE, true);
                            }
                        }
                    }
                }

                startTime = System.nanoTime() - startTime;
                SurvivalMod.instance.logger().info("SurvialMod: Power gen search time " + StringHelpers.formatNanoTime(startTime)); //TODO remove debug
            }
        }
    }

    public boolean hasFuel()
    {
        return tank.getFluid() != null && tank.getFluid().getFluid() != null && supportedFluids.contains(tank.getFluid().getFluid().getName());
    }

    public boolean isFull()
    {
        return hasFuel() && tank.getFluidAmount() >= tank.getCapacity();
    }

    @Override
    public IFluidTank getTankForFluid(Fluid fluid)
    {
        if (fluid != null && supportedFluids.contains(fluid.getName()))
        {
            return tank;
        }
        return null;
    }

    @Override
    public ForgeDirection getDirection()
    {
        if (dirCache == null)
        {
            dirCache = ForgeDirection.getOrientation(world().getBlockMetadata(xi(), yi(), zi()));
        }
        return dirCache;
    }

    public static boolean isFuelBucket(ItemStack stack)
    {
        if (stack.getItem() instanceof IFluidContainerItem)
        {
            FluidStack fluidStack = ((IFluidContainerItem) stack.getItem()).getFluid(stack);
            if (fluidStack != null && fluidStack.getFluid() == SurvivalMod.fuel)
            {
                return true;
            }
        }
        else if (FluidContainerRegistry.isFilledContainer(stack))
        {
            FluidStack fluidStack = FluidContainerRegistry.getFluidForFilledItem(stack);
            if (fluidStack != null && fluidStack.getFluid() == SurvivalMod.fuel)
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player)
    {
        return new ContainerPowerGen(player, this);
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player)
    {
        return new GuiPowerGen(player, this);
    }

    @Override
    protected String getClassDisplayName()
    {
        return "TilePowerGenerator";
    }
}
