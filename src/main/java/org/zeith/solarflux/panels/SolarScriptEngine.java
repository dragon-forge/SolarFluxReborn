package org.zeith.solarflux.panels;

import com.google.common.base.Suppliers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.ModList;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.zeith.hammerlib.core.RecipeHelper;
import org.zeith.hammerlib.core.js.JsFactory;
import org.zeith.hammerlib.core.js.parsers.OpenJDKParser;
import org.zeith.hammerlib.event.recipe.RegisterRecipesEvent;
import org.zeith.solarflux.SolarFlux;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
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
		ScriptEngine se = NASHORN_FACTORY.getScriptEngine(new String[] {
				"-doe",
				"--language=es6"
		}, getAppClassLoader(), SolarScriptEngine::checkClass);
		try
		{
			se.put("panel", se.eval("function(){return Java.type('" + SolarPanel.class.getName() + "').customBuilder();}"));
			se.put("ingredient", se.eval("function(e){return Java.type('" + RecipeHelper.class.getName() + "').fromComponent(e);}"));
			se.put("isEmpty", se.eval("function(e){return Java.type('" + SolarScriptEngine.class.getName() + "').isEmpty(e);}"));
			se.put("newMaterial", se.eval("function(name){return Java.type('" + JSHelper.class.getName() + "').newJSItem(name);}"));
			se.put("item", se.eval("function(mod, id){var js=Java.type('" + JSHelper.class.getName() + "');if(!id){return js.item(mod);}else{return js.item(mod,id);}}"));
			se.put("tag", se.eval("function(mod, id){var js=Java.type('" + JSHelper.class.getName() + "');if(!id){return js.tag(mod);}else{return js.tag(mod,id);}}"));
			se.put("isModLoaded", se.eval("function(mod){return Java.type('" + ModList.class.getName() + "').get().isLoaded(mod);}"));
		} catch(ScriptException e)
		{
			SolarFlux.LOG.error("Failed to create new JS engine!", e);
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
	
	static final Supplier<Set<String>> ALLOWED_CLASSES = Suppliers.memoize(() -> new HashSet<>(Arrays.asList(
			SolarPanel.class.getName(),
			RecipeHelper.class.getName(),
			SolarScriptEngine.class.getName(),
			JSHelper.class.getName(),
			ModList.class.getName()
	)));
	
	static boolean checkClass(String s)
	{
		return ALLOWED_CLASSES.get().contains(s);
	}
	
	private static ClassLoader getAppClassLoader()
	{
		// Revisit: script engine implementation needs the capability to
		// find the class loader of the context in which the script engine
		// is running so that classes will be found and loaded properly
		return Objects.requireNonNullElseGet(
				Thread.currentThread().getContextClassLoader(),
				NashornScriptEngineFactory.class::getClassLoader
		);
	}
}