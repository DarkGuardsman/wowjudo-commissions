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
public class CommandStatSet extends SubCommand
{
    public String helpCommand3Arg;
    public String helpCommand2Arg;

    public CommandStatSet()
    {
        super("set");
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        //Lazy init
        if (helpCommand3Arg == null)
        {
            helpCommand3Arg = getPrefix() + " [player] [stat] [level]";
            helpCommand2Arg = getPrefix() + " [stat] [level]";
        }
        super.processCommand(sender, args);
    }

    @Override
    public boolean handleEntityPlayerCommand(EntityPlayer player, String[] args)
    {
        return handleConsoleCommand(player, args);
    }

    @Override
    public boolean handleConsoleCommand(ICommandSender sender, String[] args)
    {
        //Error check for too many arguments
        if (args.length > 3)
        {
            sender.addChatMessage(new ChatComponentText("Error: Too many arguments"));
            sender.addChatMessage(new ChatComponentText(helpCommand2Arg));
            sender.addChatMessage(new ChatComponentText(helpCommand3Arg));
        }
        //Error check for too few arguments
        else if (args.length < 2)
        {
            sender.addChatMessage(new ChatComponentText("Error: Too few arguments"));
            sender.addChatMessage(new ChatComponentText(helpCommand2Arg));
            sender.addChatMessage(new ChatComponentText(helpCommand3Arg));
        }
        //Normal run
        else
        {
            //Get player to apply state change
            EntityPlayer playerToEdit;
            if (args.length == 3)
            {
                //Find player
                playerToEdit = getPlayer(sender, args[0]);

                //Error if can't find player
                if (playerToEdit == null)
                {
                    sender.addChatMessage(new ChatComponentText("Error: Failed to find user '" + args[0] + "'"));
                    sender.addChatMessage(new ChatComponentText(helpCommand3Arg));
                    return true;
                }
            }
            //If sender is a player and argument count is 2, use self
            else if (sender instanceof EntityPlayer)
            {
                playerToEdit = (EntityPlayer) sender;
            }
            //Error if sender is console but argument count is two
            else
            {
                sender.addChatMessage(new ChatComponentText("Error: Too few arguments"));
                sender.addChatMessage(new ChatComponentText(helpCommand3Arg));
                return true;
            }

            int level = 0;
            int prevLevel = 0;
            int newLevel = 0;

            //Parse number for stat level
            String numberString = args.length == 2 ? args[1].toLowerCase() : args[2].toLowerCase();
            try
            {
                level = Integer.parseInt(numberString);
            }
            catch (NumberFormatException e)
            {
                sender.addChatMessage(new ChatComponentText("Error: Couldn't read '" + numberString + "' as a number for stat level"));
                return true;
            }

            //Get stat handler
            StatEntityProperty property = StatHandler.getPropertyForEntity(playerToEdit);
            if (property != null)
            {
                //Get stat name and lower case to reduce errors
                String statName = args.length == 2 ? args[0].toLowerCase() : args[1].toLowerCase();

                //Match stat and apply change
                switch (statName)
                {
                    case "hp":
                    case "health":
                    {
                        prevLevel = property.getHpIncrease();

                        if (prevLevel == level)
                        {
                            sender.addChatMessage(new ChatComponentText("Ignored: Health was already set to " + level));
                        }
                        else
                        {
                            property.setHpIncrease(level);
                            newLevel = property.getHpIncrease();
                            if (newLevel == level)
                            {
                                sender.addChatMessage(new ChatComponentText("Success: Health changed from " + prevLevel + " to " + newLevel));
                            }
                            else if (newLevel < level && newLevel == StatHandler.HEALTH_MAX)
                            {
                                sender.addChatMessage(new ChatComponentText("Warning: Health changed from " + prevLevel + " to " + newLevel + " but limited by max value of " + StatHandler.HEALTH_MAX));
                            }
                            else
                            {
                                sender.addChatMessage(new ChatComponentText("Error: Health changed from " + prevLevel + " to " + newLevel + " but was not near expected"));
                            }
                        }
                        return true;
                    }
                    case "speed":
                    {
                        prevLevel = property.getSpeedIncrease();

                        if (prevLevel == level)
                        {
                            sender.addChatMessage(new ChatComponentText("Ignored: Speed was already set to " + level));
                        }
                        else
                        {
                            property.setSpeedIncrease(level);
                            newLevel = property.getSpeedIncrease();
                            if (newLevel == level)
                            {
                                sender.addChatMessage(new ChatComponentText("Success: Speed changed from " + prevLevel + " to " + newLevel));
                            }
                            else if (newLevel < level && newLevel == StatHandler.SPEED_MAX)
                            {
                                sender.addChatMessage(new ChatComponentText("Warning: Speed changed from " + prevLevel + " to " + newLevel + " but limited by max value of " + StatHandler.SPEED_MAX));
                            }
                            else
                            {
                                sender.addChatMessage(new ChatComponentText("Error: Speed changed from " + prevLevel + " to " + newLevel + " but was not near expected"));
                            }
                        }
                        return true;
                    }
                    default:
                        sender.addChatMessage(new ChatComponentText("Error: Unknown stat '" + statName + "' either its not implement or doesn't exist"));
                        return true;
                }

            }
            else
            {
                sender.addChatMessage(new ChatComponentText("Error: Failed to load stat property object for user'" + playerToEdit.getCommandSenderName() + "'"));
            }
        }
        return true;
    }
}
