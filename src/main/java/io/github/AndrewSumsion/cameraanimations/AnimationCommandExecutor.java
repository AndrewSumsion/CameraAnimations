package io.github.AndrewSumsion.cameraanimations;

import io.github.AndrewSumsion.cameraanimations.animation.Animation;
import io.github.AndrewSumsion.cameraanimations.animation.RunningAnimation;
import io.github.AndrewSumsion.cameraanimations.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AnimationCommandExecutor implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 1) {
            return usage(sender);
        }
        if(args[0].equalsIgnoreCase("play") && !(sender instanceof Player)) {
            String usageString = "/animation play <name> <player> [duration]";
            if(args.length < 3) {
                return usage(sender, usageString);
            }
            Animation animation = AnimationHandler.getAnimation(args[1]);
            if(animation == null) {
                sender.sendMessage(ChatColor.RED + "Animation \"" + args[1] + "\" does not exist!");
                return true;
            }
            Player player = Bukkit.getPlayer(args[2]);
            if(player == null) {
                sender.sendMessage(ChatColor.RED + "Player \"" + args[2] + "\" not found!");
                return true;
            }
            int length = -1;
            if(args.length > 3) {
                try {
                    length = Utils.timeToTicks(args[3]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid Time");
                    return true;
                }
            }
            sender.sendMessage(ChatColor.GREEN+"Playing animation \"" + args[1] + "\" for " + player.getName() + ".");
            animation.play(player, length);
            return true;
        }
        else if(args[0].equalsIgnoreCase("list")) {
            sender.sendMessage(ChatColor.AQUA+"Animations:");
            sender.sendMessage(ChatColor.BLUE+join(AnimationHandler.getAnimations().keySet(), ", "));
            return true;
        }
        else if(args[0].equalsIgnoreCase("delete")) {
            String usageString = "/animation delete <name>";
            if(args.length < 2) {
                return usage(sender, usageString);
            }
            Animation animation = AnimationHandler.getAnimation(args[1]);
            if(animation == null) {
                sender.sendMessage(ChatColor.RED + "Animation \"" + args[1] + "\" does not exist!");
                return true;
            }
            AnimationHandler.deleteAnimation(args[1]);
            sender.sendMessage(ChatColor.GREEN+"Animation \"" + args[1] + "\" deleted.");
            return true;
        } else if(args[0].equalsIgnoreCase("stop")) {
            String usageString = "/animation stop [player]";
            if((sender instanceof Player) && args.length == 1) {
                RunningAnimation animation = PlayerData.getData((Player) sender).getRunningAnimation();
                if(animation != null) animation.stop();
                sender.sendMessage(ChatColor.GREEN + "Animation stopped.");
                return true;
            }
            if(args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Specify a player");
                return true;
            }
            Player targetPlayer = Bukkit.getPlayer(args[1]);
            if(targetPlayer == null) {
                sender.sendMessage(ChatColor.RED + "Player \"" + args[1] + "\" not found!");
                return true;
            }
            PlayerData.getData(targetPlayer).getRunningAnimation().stop();
            sender.sendMessage(ChatColor.GREEN + "Stopped animation for " + args[1]);
        }
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED+"You must be a player to use this command!");
            return true;
        }
        Player player = (Player) sender;
        PlayerData playerData = PlayerData.getData(player);

        if(args[0].equalsIgnoreCase("create")) {
            String usageString = "/animation create <name>";
            if(args.length < 2) {
                return usage(player, usageString);
            }
            AnimationHandler.addAnimation(args[1], playerData.getQueuedAnimation());
            playerData.resetQueuedAnimation();
            player.sendMessage(ChatColor.GREEN + "Created animation \"" + args[1] + "\"");
        }
        else if(args[0].equalsIgnoreCase("play")) {
            String usageString = "/animation play <name> [player] [duration]";
            if(args.length < 2) {
                return usage(player, usageString);
            }
            Animation animation = AnimationHandler.getAnimation(args[1]);
            if(animation == null) {
                player.sendMessage(ChatColor.RED + "Animation \"" + args[1] + "\" does not exist!");
                return true;
            }
            int length = -1;
            Player targetPlayer = player;
            if(args.length > 2) {
                targetPlayer = Bukkit.getPlayer(args[2]);
                if(targetPlayer == null) {
                    try {
                        length = Utils.timeToTicks(args[2]);
                        targetPlayer = player;
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.RED+"Player \"" + args[2] + "\" not found!");
                        return true;
                    }
                }
            }
            if(args.length > 3 && length == -1) {
                try {
                    length = Utils.timeToTicks(args[3]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid Time");
                    return true;
                }
            }
            if(!targetPlayer.getName().equals(player.getName())) {
                player.sendMessage(ChatColor.GREEN + "Playing animation \"" + args[1] + "\" for " + targetPlayer.getName() + ".");
            }
            animation.play(targetPlayer, length);
        }
        else if(args[0].equalsIgnoreCase("edit")) {
            String usageString = "/animation edit [name]";
            if(playerData.getEditing() != null) {
                String animationName = playerData.getEditing();
                AnimationHandler.addAnimation(animationName, playerData.getQueuedAnimation());
                playerData.resetQueuedAnimation();
                playerData.setEditing(null);
                player.sendMessage(ChatColor.GREEN+"Saved changes to \"" + animationName + "\".");
                return true;
            }
            if(args.length < 2) {
                return usage(player, usageString);
            }
            if(AnimationHandler.getAnimation(args[1]) == null) {
                player.sendMessage(ChatColor.RED+"Unknown animation: \"" + args[1] + "\"");
                return true;
            }
            playerData.setEditing(args[1]);
            playerData.setQueuedAnimation(new Animation(AnimationHandler.getAnimation(args[1])));
            player.sendMessage(ChatColor.GREEN+"Now editing \"" + args[1] + "\".");
        }

        return true;
    }

    private boolean usage(CommandSender sender) {

        return true;
    }

    private boolean usage(CommandSender sender, String usageString) {
        sender.sendMessage(ChatColor.RED + "Usage: " + usageString);
        return true;
    }

    private static String join(Iterable<String> list, String delim) {

        StringBuilder sb = new StringBuilder();

        String loopDelim = "";

        for(String s : list) {

            sb.append(loopDelim);
            sb.append(s);

            loopDelim = delim;
        }

        return sb.toString();
    }

}
