package org.zeith.solarflux.mixins;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.*;

import java.util.*;

public class SFRMixinPlugin
		implements IMixinConfigPlugin
{
	private String compatPackage;
	
	@Override
	public void onLoad(String mixinPackage)
	{
		compatPackage = mixinPackage + ".compat";
	}
	
	@Override
	public String getRefMapperConfig()
	{
		return null;
	}
	
	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName)
	{
		if(mixinClassName.startsWith(compatPackage))
		{
			String[] modNMixin = mixinClassName.substring(compatPackage.length() + 1).split("[.]", 2);
			if(modNMixin.length == 2)
				return isModLoaded(modNMixin[0]);
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
		if(ModList.get() == null)
		{
			return LoadingModList
					.get()
					.getMods()
					.stream()
					.map(ModInfo::getModId)
					.anyMatch(modId::equals);
		} else
		{
			return ModList.get().isLoaded(modId);
		}
	}
}
