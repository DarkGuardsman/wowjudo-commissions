package com.builtbroken.wowjudo.stats.command;

import com.builtbroken.mc.core.commands.prefab.SubCommand;
import com.builtbroken.wowjudo.stats.StatEntityProperty;
import com.builtbroken.wowjudo.stats.StatHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/16/2017.
 */
public class CommandStatReset extends SubCommand
{
    public CommandStatReset()
    {
        super("reset");
    }

    @Override
    public boolean isHelpCommand(String[] args)
    {
        return args != null && args.length >= 1 && (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?"));
    }

    @Override
    public boolean handleEntityPlayerCommand(EntityPlayer player, String[] args)
    {
        return handleConsoleCommand(player, args);
    }

    @Override
    public boolean handleConsoleCommand(ICommandSender sender, String[] args)
    {
        if (args == null || args.length == 0 || args[0] == null)
        {
            if (sender instanceof EntityPlayer)
            {
                resetPlayer(sender, (EntityPlayer) sender);
            }
            else
            {
                sender.addChatMessage(new ChatComponentText("Error: Too few arguments"));
                printHelp(sender, 0);
            }
        }
        else if (args.length == 1)
        {
            EntityPlayer target = getPlayer(sender, args[0]);
            if (target != null)
            {
                resetPlayer(sender, target);
            }
            else
            {
                sender.addChatMessage(new ChatComponentText("Error: Could not find player '" + args[0] + "'"));
            }
        }
        else
        {
            sender.addChatMessage(new ChatComponentText("Error: Too many arguments"));
            printHelp(sender, 0);
        }
        return true;
    }

    protected void resetPlayer(ICommandSender sender, EntityPlayer player)
    {
        StatEntityProperty property = StatHandler.getPropertyForEntity(player);
        if (property != null)
        {
            if (property.reset())
            {
                sender.addChatMessage(new ChatComponentText("Success: Reset skills" + (player == sender ? "" : " for " + player.getCommandSenderName())));
            }
            else
            {
                sender.addChatMessage(new ChatComponentText("Failed: Reset skills some but not all skills" + (player == sender ? "" : " for " + player.getCommandSenderName())));
            }
        }
        else
        {
            sender.addChatMessage(new ChatComponentText("Error: Too many arguments"));
        }
    }
}
