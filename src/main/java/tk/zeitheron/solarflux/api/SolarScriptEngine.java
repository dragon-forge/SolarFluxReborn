package tk.zeitheron.solarflux.api;

import net.minecraftforge.fml.common.Loader;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
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
	}

	public Object callFunction(String name, Object... args) throws ScriptException, NoSuchMethodException
	{
		return engineInvocable.invokeFunction(name, args);
	}

	public static ScriptEngine newEngine()
	{
		ScriptEngine se = new ScriptEngineManager(null).getEngineByName("Nashorn");

		try
		{
			se.put("panel", se.eval("function(){return Java.type('" + SolarInfo.class.getName() + "').customBuilder();}"));
			se.put("item", se.eval("function(mod, id){var js=Java.type('" + JSHelper.class.getName() + "');if(!id){return js.item(mod);}else{return js.item(mod,id);}}"));
			se.put("isModLoaded", se.eval("function(mod){return Java.type('" + Loader.class.getName() + "').isModLoaded(mod);}"));
		} catch(ScriptException e)
		{
			e.printStackTrace();
		}

		return se;
	}
}