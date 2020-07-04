package tk.zeitheron.solarflux.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.server.command.CommandTreeBase;
import tk.zeitheron.solarflux.init.SolarsSF;
import tk.zeitheron.solarflux.net.NetworkSF;

import java.util.Arrays;
import java.util.List;

public class CommandSolarFlux extends CommandTreeBase
{
	{
		addSubcommand(new Reload());
	}

	@Override
	public String getName()
	{
		return "solarflux";
	}

	@Override
	public List<String> getAliases()
	{
		return Arrays.asList("sfr", "sf");
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "/solarflux <sub>";
	}

	private static class Reload extends CommandBase
	{
		@Override
		public String getName()
		{
			return "reload";
		}

		@Override
		public String getUsage(ICommandSender sender)
		{
			return "/solarflux reload";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
		{
			sender.sendMessage(new TextComponentTranslation("info.solarflux.reload"));
			SolarsSF.refreshConfigs();
			server.getPlayerList().getPlayers().forEach(mp->
			{
				NetworkSF.INSTANCE.sendAllPanels(mp);
			});
		}
	}
}