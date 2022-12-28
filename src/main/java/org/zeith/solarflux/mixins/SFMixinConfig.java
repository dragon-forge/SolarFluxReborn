package org.zeith.solarflux.mixins;

import net.minecraft.Util;
import net.minecraftforge.fml.ModList;
import org.jline.utils.Log;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.*;

public class SFMixinConfig
		implements IMixinConfigPlugin
{
	public static final Map<String, String> MOD_FB_CLASS_NAMES = Collections.unmodifiableMap(Util.make(new HashMap<>(), map ->
	{
		map.put("ae2", "appeng.api.networking.energy.IAEPowerStorage");
	}));
	
	public static final Map<String, List<String>> EXTRA_MIXINS = Collections.unmodifiableMap(Util.make(new HashMap<>(), map ->
	{
		map.put("ae2", List.of(
				"compat.ae2.SolarPanelTileMixin"
		));
	}));
	
	public static final Map<String, String> MIXIN_MOD_IDS = Collections.unmodifiableMap(Util.make(new HashMap<>(), map ->
	{
		for(var e : EXTRA_MIXINS.entrySet())
		{
			for(String mixin : e.getValue())
			{
				map.put(mixin, e.getKey());
			}
		}
	}));
	
	private String mixinPackage;
	
	@Override
	public void onLoad(String mixinPackage)
	{
		this.mixinPackage = mixinPackage;
	}
	
	@Override
	public String getRefMapperConfig()
	{
		return null;
	}
	
	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName)
	{
		if(mixinClassName.startsWith(mixinPackage))
		{
			var mixinName = mixinClassName.substring(mixinPackage.length() + 1);
			if(MIXIN_MOD_IDS.containsKey(mixinName))
			{
				var modID = MIXIN_MOD_IDS.get(mixinName);
				boolean shouldLoad = true;
				
				if(ModList.get() == null)
				{
					var tryLoadClass = MOD_FB_CLASS_NAMES.get(modID);
					if(tryLoadClass != null)
						try
						{
							Class.forName(tryLoadClass);
						} catch(ClassNotFoundException e)
						{
							shouldLoad = false;
						}
				} else
					shouldLoad = ModList.get().isLoaded(modID);
				
				if(shouldLoad) Log.info("Allowing mixin " + mixinName);
				else Log.info("Skipping mixin " + mixinName);
				
				return shouldLoad;
			}
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
}