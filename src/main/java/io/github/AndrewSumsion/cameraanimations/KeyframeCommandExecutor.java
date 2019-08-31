package io.github.AndrewSumsion.cameraanimations;

import io.github.AndrewSumsion.cameraanimations.animation.AnimationCurve;
import io.github.AndrewSumsion.cameraanimations.animation.Keyframe;
import io.github.AndrewSumsion.cameraanimations.interpolation.Interpolator;
import io.github.AndrewSumsion.cameraanimations.util.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KeyframeCommandExecutor implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to run this command.");
            return true;
        }
        Player player = (Player) sender;
        PlayerData playerData = PlayerData.getData(player);
        if(args.length == 0) {
            return usage(player);
        }
        if(args[0].equalsIgnoreCase("add")) {
            String usageString = "/keyframe add <length>";
            float duration = 0F;
            if(playerData.getQueuedAnimation().totalKeyframes() > 0) {
                if(args.length < 2) {
                    return usage(player, usageString);
                }
                try {
                    duration = Float.valueOf(args[1]);
                } catch (NumberFormatException e) {
                    return usage(player, usageString);
                }
            }
            playerData.addKeyframe(player.getLocation(), duration);
            player.sendMessage(ChatColor.GREEN + "Added position #" + playerData.getQueuedAnimation().totalKeyframes() + " at (" + player.getLocation().getX()+", " +player.getLocation().getY()+", " +player.getLocation().getZ()+")");
        }
        else if(args[0].equalsIgnoreCase("preview")) {
            int length = -1;
            if(args.length > 1) {
                try {
                    length = Utils.timeToTicks(args[1]);
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "Invalid Time");
                    return true;
                }
            }
            player.sendMessage(ChatColor.GREEN + "Previewing...");
            playerData.getQueuedAnimation().play(player, length);
        }
        else if(args[0].equalsIgnoreCase("update")) {
            String usageString = "/keyframe update <keyframe> [newLength]";
            if(args.length < 2) {
                return usage(player, usageString);
            }
            int keyframeIndex;
            try {
                keyframeIndex = Integer.valueOf(args[1]) - 1;
            } catch (NumberFormatException e) {
                return usage(player, usageString);
            }
            if(keyframeIndex >= playerData.getQueuedAnimation().totalKeyframes() || keyframeIndex < 0) {
                player.sendMessage(ChatColor.RED + "Invalid Keyframe");
                return true;
            }
            Keyframe keyframe = playerData.getQueuedAnimation().getKeyframe(keyframeIndex);
            int newLength = keyframe.getLength();
            if(args.length == 3) {
                try {
                    newLength = (int)(Double.valueOf(args[2]) * 20D);
                } catch (NumberFormatException e) {
                    return usage(player, usageString);
                }
            }
            Keyframe newKeyframe = new Keyframe(keyframe.getAnimationCurve(), newLength, keyframe.getLocationProperty(), keyframe.getProperties());
            keyframe.getLocationProperty().setValue(player.getLocation());
            keyframe.setLength(newLength);
            player.sendMessage(ChatColor.GREEN + "Updated keyframe #" + (keyframeIndex + 1) + " to your location" +
                    (args.length == 3 ? " with new length of " + args[2] + " seconds": "") + ".");
        }
        else if(args[0].equalsIgnoreCase("clear")) {
            playerData.resetQueuedAnimation();
            player.sendMessage(ChatColor.GREEN + "Queued keyframes cleared.");
        }
        else if(args[0].equalsIgnoreCase("delete")) {
            String usageString = "/keyframe delete <keyframe>";
            if (args.length < 2) {
                return usage(player, usageString);
            }
            int keyframeIndex;
            try {
                keyframeIndex = Integer.valueOf(args[1]) - 1;
            } catch (NumberFormatException e) {
                return usage(player, usageString);
            }
            if (keyframeIndex >= playerData.getQueuedAnimation().totalKeyframes() || keyframeIndex < 0) {
                player.sendMessage(ChatColor.RED + "Invalid Keyframe");
                return true;
            }
            playerData.getQueuedAnimation().deleteKeyframe(keyframeIndex);
            player.sendMessage(ChatColor.GREEN + "Deleted keyframe #" + (keyframeIndex + 1));
        }
        else if(args[0].equalsIgnoreCase("curve")) {
            String usageString = "/keyframe curve <keyframe> <animation curve>";
            if(args.length < 3) {
                return usage(player, usageString);
            }
            int keyframeIndex;
            try {
                keyframeIndex = Integer.valueOf(args[1]) - 1;
            } catch (NumberFormatException e) {
                return usage(player, usageString);
            }
            if (keyframeIndex >= playerData.getQueuedAnimation().totalKeyframes() || keyframeIndex < 0) {
                player.sendMessage(ChatColor.RED + "Invalid Keyframe");
                return true;
            }
            AnimationCurve animationCurve = AnimationCurve.getAnimationCurve(args[2]);
            if(animationCurve == null) {
                player.sendMessage(ChatColor.RED + "Invalid Animation Curve");
                return true;
            }
            playerData.getQueuedAnimation().getKeyframe(keyframeIndex).setAnimationCurve(animationCurve);
            player.sendMessage(ChatColor.GREEN + "Set animation curve of keyframe #" + args[1] + " to " + args[2]);
        }
        else if(args[0].equalsIgnoreCase("interpolation")) {
            String usageString = "/keyframe interpolation <interpolation type>";
            if(args.length < 2) {
                return usage(player, usageString);
            }
            Interpolator interpolator = Interpolator.fromUserFriendlyName(args[1]);
            if(interpolator == null) {
                player.sendMessage(ChatColor.RED + "Invalid Interpolation Type!");
                player.sendMessage(ChatColor.RED + "Options are: linear, catmull-rom, cubic, cubic-hermite");
                return true;
            }
            playerData.getQueuedAnimation().setInterpolator(interpolator);
            player.sendMessage(ChatColor.GREEN + "Set interpolation type to " + args[1] + ".");
        }
        else if(args[0].equalsIgnoreCase("help")) {
            if(args.length < 2) {
                return usage(player);
            }
            if(args[1].equalsIgnoreCase("add")) {
                player.sendMessage(ChatColor.AQUA + "/keyframe add <length>");
                player.sendMessage(ChatColor.BLUE + "  Adds a keyframe <length> seconds after the last keyframe.");
                player.sendMessage(ChatColor.BLUE + "  If it's the first keyframe, <length> is not used.");
            } else if(args[1].equalsIgnoreCase("preview")) {
                player.sendMessage(ChatColor.AQUA + "/keyframe preview");
                player.sendMessage(ChatColor.BLUE + "  Previews the animation you're working on.");
            } else if(args[1].equalsIgnoreCase("update")) {
                player.sendMessage(ChatColor.AQUA + "/keyframe update <keyframe> [newLength]");
                player.sendMessage(ChatColor.BLUE + "  Updates the specified keyframe to your current position.");
                player.sendMessage(ChatColor.BLUE + "  [newLength] optionally changes the length of the keyframe.");
            } else if(args[1].equalsIgnoreCase("delete")) {
                player.sendMessage(ChatColor.AQUA + "/keyframe delete <keyframe>");
                player.sendMessage(ChatColor.BLUE + "  Removes the specified keyframe.");
            } else if(args[1].equalsIgnoreCase("clear")) {
                player.sendMessage(ChatColor.AQUA + "/keyframe clear");
                player.sendMessage(ChatColor.BLUE + "  Clears all keyframes.");
            } else if(args[1].equalsIgnoreCase("curve")) {
                player.sendMessage(ChatColor.AQUA + "/keyframe curve <keyframe> <animation curve>");
                player.sendMessage(ChatColor.BLUE + "  Changes the timing function of the specified keyframe.");
                player.sendMessage(ChatColor.BLUE + "  Presets: (linear, ease, ease-in, ease-out, ease-in-out)");
                player.sendMessage(ChatColor.BLUE + "  For custom timing functions use cubic-bezier(xx,xx,xx,xx) as\n   generated by cubic-bezier.com");
            } else if(args[1].equalsIgnoreCase("interpolation")) {
                player.sendMessage(ChatColor.AQUA + "/keyframe interpolation <interpolation type>");
                player.sendMessage(ChatColor.BLUE + "  Sets the interpolation mode of the queued animation.");
                player.sendMessage(ChatColor.BLUE + "  Options are: linear, catmull-rom, cubic, cubic-hermite");
            } else {
                return usage(player);
            }

        } else {
            return usage(player);
        }
        return true;
    }

    private boolean usage(Player player, String usageString) {
        player.sendMessage(ChatColor.RED + "Usage: " + usageString);
        return true;
    }

    private boolean usage(Player player) {
        player.sendMessage(ChatColor.AQUA + "Usage:\n"
                + ChatColor.BLUE + "/keyframe add <length>\n"
                + ChatColor.BLUE + "/keyframe update <keyframe> [newLength]\n"
                + ChatColor.BLUE + "/keyframe preview\n"
                + ChatColor.BLUE + "/keyframe delete <keyframe>\n"
                + ChatColor.BLUE + "/keyframe clear\n"
                + ChatColor.BLUE + "/keyframe set-curve <keyframe> <animation curve>\n"
                + ChatColor.GREEN + "Use "+ChatColor.AQUA+"/keyframe help <subcommand>"+ChatColor.GREEN+" for info about each."
        );
        return true;
    }
}
