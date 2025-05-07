package org.zeith.solarflux.mixins;

import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.moddiscovery.ModInfo;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.*;

import java.util.*;
import java.util.regex.Pattern;

public class ConfigPlugin
		implements IMixinConfigPlugin
{
	Pattern compatRegex;
	
	@Override
	public void onLoad(String mixinPackage)
	{
		compatRegex = Pattern.compile(String.format("%s(?<mod>[^.]+)", Pattern.quote(mixinPackage + ".compat.")));
	}
	
	@Override
	public String getRefMapperConfig()
	{
		return null;
	}
	
	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName)
	{
		var m = compatRegex.matcher(mixinClassName);
		if(m.find())
		{
			var mod = m.group("mod");
			return isModLoaded(mod);
		}
		return true;
	}
	
	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets)
	{
	}
	
	@Override
	public List<String> getMixins()
	{
		return null;
	}
	
	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo)
	{
	}
	
	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo)
	{
	}
	
	private static boolean isModLoaded(String modId)
	{
		if(ModList.get() != null)
			return ModList.get().isLoaded(modId);
		return LoadingModList
				.get()
				.getMods()
				.stream()
				.map(ModInfo::getModId)
				.anyMatch(modId::equals);
	}
}