package com.builtbroken.wowjudo.content.generator.gui;

import com.builtbroken.mc.core.References;
import com.builtbroken.mc.imp.transform.region.Rectangle;
import com.builtbroken.mc.imp.transform.vector.Point;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.mc.prefab.gui.GuiContainerBase;
import com.builtbroken.mc.prefab.gui.buttons.GuiButton9px;
import com.builtbroken.wowjudo.SurvivalMod;
import com.builtbroken.wowjudo.content.generator.TilePowerGenerator;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

import java.awt.*;


/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/8/2017.
 */
public class GuiPowerGen extends GuiContainerBase
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(SurvivalMod.DOMAIN, References.GUI_DIRECTORY + "fuel_gen.png");
    private TilePowerGenerator generator;

    private GuiButton9px onButton;
    private GuiButton9px offButton;

    public GuiPowerGen(EntityPlayer player, TilePowerGenerator generator)
    {
        super(new ContainerPowerGen(player, generator));
        this.baseTexture = TEXTURE;
        this.generator = generator;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        int x = 3 + guiLeft;
        int y = 3 + guiTop;
        onButton = (GuiButton9px) add(GuiButton9px.newOnButton(0, x, y)).setEnabled(!generator.turnedOn);
        offButton = (GuiButton9px) add(GuiButton9px.newOffButton(1, x + 9, y)).setEnabled(generator.turnedOn);
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        final int buttonId = button.id;
        //Turn on
        if (buttonId == 0)
        {
            generator.setOnState(true);
        }
        //Turn off
        else if (buttonId == 1)
        {
            generator.setOnState(false);
        }
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();

        //Update power state
        if (generator.turnedOn)
        {
            onButton.disable();
            offButton.enable();
        }
        else
        {
            onButton.enable();
            offButton.disable();
        }
    }


    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(f, mouseX, mouseY);

        //---------------------------------------------------------
        //FUEL RENDER CODE
        int x = guiLeft + 70;
        int y = guiTop + 20;

        //Background
        this.drawTexturedModalRect(x, y, 0, 167, 94, 50);

        //Fuel
        if (generator.tank.getFluid() != null)
        {
            Fluid fluid = generator.tank.getFluid().getFluid();
            int c = fluid.getColor();
            Color color = null;
            if (c != 0xFFFFFF)
            {
                color = new Color(c);
            }
            else if (fluid.getName().equalsIgnoreCase("lava"))
            {
                color = Color.RED;
            }
            else if (fluid.getName().equalsIgnoreCase("water"))
            {
                color = Color.BLUE;
            }
            else if (fluid.getName().equalsIgnoreCase("fuel"))
            {
                color = Color.YELLOW;
            }
            GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 1.0F);
        }
        float volume_render = (float) generator.tank.getFluidAmount() / (float) generator.tank.getCapacity();
        int renderHeight = (int) Math.ceil(50 * volume_render);
        this.drawTexturedModalRect(x + 1, y + (50 - renderHeight), 95 + 20, 167, 94, renderHeight);

        //Render fluid tank
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.drawTexturedModalRect(x + 1, y, 95, 167, 20, 50);

        //---------------------------------------------------------
        //Render slots
        for (Object object : inventorySlots.inventorySlots)
        {
            if (object instanceof Slot)
            {
                drawSlot((Slot) object);
            }
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        //GUI name
        drawStringCentered(LanguageUtility.getLocal("tile." + SurvivalMod.PREFX + "powerGen.gui.name"), 88, 4);

        //Inventory name
        drawString(LanguageUtility.getLocal("tile." + SurvivalMod.PREFX + "powerGen.gui.inventory.name"), 8, 74);

        int x = guiLeft + 70;
        int y = guiTop + 20;
        int w = 94;
        int h = 50;

        //Tooltip for fluid tank
        if (new Rectangle(x, y, x + w, y + h).isWithin(new Point(mouseX, mouseY)))
        {
            FluidStack fluid = generator.tank.getFluid();
            if (fluid != null)
            {
                this.drawTooltip(mouseX - this.guiLeft, mouseY - this.guiTop + 10, "Volume: " + fluid.amount + "/" + generator.tank.getCapacity() + "mb of " + fluid.getLocalizedName());
            }
            else
            {
                this.drawTooltip(mouseX - this.guiLeft, mouseY - this.guiTop + 10, "Empty");
            }
        }
    }
}
