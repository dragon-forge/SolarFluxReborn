package org.zeith.solarflux.panels;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.ModList;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.zeith.hammerlib.core.RecipeHelper;
import org.zeith.hammerlib.event.recipe.RegisterRecipesEvent;
import org.zeith.solarflux.items.ItemsSF;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class SolarScriptEngine
{
	final ScriptEngine engine;
	final Invocable engineInvocable;

	public SolarScriptEngine(Stream<String> lines) throws ScriptException
	{
		StringBuilder content = new StringBuilder();
		Map<String, String> defines = new HashMap<>();
		lines.forEach(ln ->
		{
			AtomicReference<String> tmp = new AtomicReference<>(ln);
			defines.forEach((src, dst) -> tmp.set(tmp.get().replaceAll(src, dst)));
			ln = tmp.get();

			if(ln.startsWith("define "))
			{
				String[] kv = ln.substring(7).split(" ", 2);
				if(kv.length == 2) defines.put(kv[0], kv[1]);
				ln = "// Processed: " + ln;
			}

			if(ln.startsWith("import ") && ln.endsWith(";"))
			{
				String clazz = ln.substring(7, ln.length() - 1);
				ln = "var " + clazz.substring(clazz.lastIndexOf('.') + 1) + " = Java.type(\"" + clazz + "\");";
			}

			content.append(ln).append(System.lineSeparator());
		});
		this.engineInvocable = (Invocable) (this.engine = newEngine());
		this.engine.eval(content.toString());
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void reloadRecipes(RegisterRecipesEvent e)
	{
		try
		{
			callFunction("registerRecipes", e);
		} catch(NoSuchMethodException ignored)
		{
		} catch(Throwable er)
		{
			er.printStackTrace();
		}
	}

	public Object callFunction(String name, Object... args) throws ScriptException, NoSuchMethodException
	{
		return engineInvocable.invokeFunction(name, args);
	}

	private static final NashornScriptEngineFactory NASHORN_FACTORY = new NashornScriptEngineFactory();

	public static ScriptEngine newEngine()
	{
		// Use openjdk nashorn that forge adds as a library (nashorn-core-15.3.jar)
		ScriptEngine se = NASHORN_FACTORY.getScriptEngine();
		try
		{
			se.put("panel", se.eval("function(){return Java.type('" + SolarPanel.class.getName() + "').customBuilder();}"));
			se.put("ingredient", se.eval("function(e){return Java.type('" + RecipeHelper.class.getName() + "').fromComponent(e);}"));
			se.put("isEmpty", se.eval("function(e){return Java.type('" + SolarScriptEngine.class.getName() + "').isEmpty(e);}"));
			se.put("newMaterial", se.eval("function(name){return Java.type('" + ItemsSF.class.getName() + "').newJSItem(name);}"));
			se.put("item", se.eval("function(mod, id){var js=Java.type('" + JSHelper.class.getName() + "');if(!id){return js.item(mod);}else{return js.item(mod,id);}}"));
			se.put("tag", se.eval("function(mod, id){var js=Java.type('" + JSHelper.class.getName() + "');if(!id){return js.tag(mod);}else{return js.tag(mod,id);}}"));
			se.put("isModLoaded", se.eval("function(mod){return Java.type('" + ModList.class.getName() + "').get().isLoaded(mod);}"));
		} catch(ScriptException e)
		{
			e.printStackTrace();
		}

		return se;
	}

	public static boolean isEmpty(Object o)
	{
		if(o instanceof Ingredient i && i.isEmpty()) return true;
		if(o instanceof ItemStack s && s.isEmpty()) return true;
		if(o instanceof FluidStack f && f.isEmpty()) return true;
		return o == null;
	}
}