package com.zeitheron.solarflux.commands;

import java.util.Arrays;
import java.util.List;

import com.zeitheron.solarflux.api.SolarFluxAPI;
import com.zeitheron.solarflux.init.SolarsSF;
import com.zeitheron.solarflux.net.NetworkSF;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.server.command.CommandTreeBase;

public class CommandSolarFlux extends CommandTreeBase
{
	@Override
	public String getName()
	{
		return "solarflux";
	}
	
	@Override
	public List<String> getAliases()
	{
		return Arrays.asList("sfr");
	}
	
	@Override
	public String getUsage(ICommandSender sender)
	{
		return "/sfr help";
	}
	
	{
		addSubcommand(new CommandBase()
		{
			@Override
			public String getUsage(ICommandSender sender)
			{
				return null;
			}
			
			@Override
			public String getName()
			{
				return "reload";
			}
			
			@Override
			public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
			{
				sender.sendMessage(new TextComponentString("Reloading configs..."));
				long start = System.currentTimeMillis();
				
				SolarsSF.reloadConfigs();
				
				sender.sendMessage(new TextComponentString("All panel settings reloaded in " + (System.currentTimeMillis() - start) + " ms."));
				
				start = System.currentTimeMillis();
				sender.sendMessage(new TextComponentString("Sending solar infos to online players..."));
				
				SolarFluxAPI.SOLAR_PANELS.forEach(si -> server.getPlayerList().getPlayers().forEach(mp -> NetworkSF.INSTANCE.send(mp, si)));
				
				sender.sendMessage(new TextComponentString("Done. " + SolarFluxAPI.SOLAR_PANELS.getValuesCollection().size() + " panel settings were sent to " + server.getPlayerList().getPlayers().size() + " players in " + (System.currentTimeMillis() - start) + " ms."));
			}
		});
	}
}