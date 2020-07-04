package tk.zeitheron.solarflux.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import tk.zeitheron.solarflux.api.SolarFluxAPI;
import tk.zeitheron.solarflux.api.SolarInfo;
import tk.zeitheron.solarflux.block.BlockBaseSolar;
import tk.zeitheron.solarflux.init.SolarsSF;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.Locale;
import net.minecraft.client.resources.*;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.LanguageMap;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.IOUtils;

import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@SideOnly(Side.CLIENT)
public class SolarFluxResourcePack
		implements IResourcePack, IResourceManagerReloadListener
{
	public final Map<ResourceLocation, IResourceStreamSupplier> resourceMap = new HashMap<>();
	public final Map<String, List<String>> langs = new HashMap<>();
	public final List<SolarInfo> infos = new ArrayList<>();
	public final Set<String> domains = new HashSet<>();

	private static IResourceStreamSupplier ofText(String text)
	{
		return IResourceStreamSupplier.create(() -> true, () -> new ByteArrayInputStream(text.getBytes()));
	}

	private static IResourceStreamSupplier ofFile(Supplier<File> file)
	{
		return IResourceStreamSupplier.create(() -> file.get().isFile(), () -> new FileInputStream(file.get()));
	}

	public void addPanel(SolarInfo si)
	{
		BlockBaseSolar blk = si.getBlock();
		ResourceLocation reg = blk.getRegistryName();

		ResourceLocation blockstate = new ResourceLocation(reg.getNamespace(), "blockstates/" + reg.getPath() + ".json");
		ResourceLocation models_block = new ResourceLocation(reg.getNamespace(), "models/" + reg.getPath() + ".json");
		ResourceLocation models_item = new ResourceLocation(reg.getNamespace(), "models/item/" + reg.getPath() + ".json");

		float thicc = si.getHeight() * 16F;
		float thic2 = thicc + 0.25F;
		float reverseThicc = 16 - thicc;

		resourceMap.put(blockstate, ofText("{\"variants\":{\"normal\":{\"model\":\"" + reg.getNamespace() + ":block/" + reg.getPath() + "\"}}}"));
		resourceMap.put(models_item, ofText("{\"parent\":\"" + reg.getNamespace() + ":" + reg.getPath() + "\"}"));

		// Block Model
		resourceMap.put(models_block, ofText("{\"parent\":\"block/block\",\"textures\":{\"0\":\"" + reg.getNamespace() + ":blocks/" + reg.getPath() + "_base\",\"1\":\"" + reg.getNamespace() + ":blocks/" + reg.getPath() + "_top\",\"particle\":\"solarflux:blocks/example_base\"},\"elements\":[{\"name\":\"base\",\"from\":[0,0,0],\"to\":[16," + thicc + ",16],\"faces\":{\"north\":{\"uv\":[0," + reverseThicc + ",16,16],\"texture\":\"#0\"},\"east\":{\"uv\":[0," + reverseThicc + ",16,16],\"texture\":\"#0\"},\"south\":{\"uv\":[0," + reverseThicc + ",16,16],\"texture\":\"#0\"},\"west\":{\"uv\":[0," + reverseThicc + ",16,16],\"texture\":\"#0\"},\"up\":{\"uv\":[0,0,16,16],\"texture\":\"#1\"},\"down\":{\"uv\":[0,0,16,16],\"texture\":\"#0\"}}},{\"from\":[0," + thicc + ",0],\"to\":[16," + thic2 + ",1],\"faces\":{\"north\":{\"uv\":[0,0,16,0.25],\"texture\":\"#0\"},\"east\":{\"uv\":[0,0,1,0.25],\"texture\":\"#0\"},\"south\":{\"uv\":[0,0,16,0.25],\"texture\":\"#0\"},\"west\":{\"uv\":[0,0,1,0.25],\"texture\":\"#0\"},\"up\":{\"uv\":[0,0,16,1],\"texture\":\"#0\"},\"down\":{\"uv\":[0,0,16,1],\"texture\":\"#0\"}}},{\"from\":[0," + thicc + ",15],\"to\":[16," + thic2 + ",16],\"faces\":{\"north\":{\"uv\":[0,15,16,15.25],\"texture\":\"#0\"},\"east\":{\"uv\":[0,15,1,15.25],\"texture\":\"#0\"},\"south\":{\"uv\":[0,15,16,15.25],\"texture\":\"#0\"},\"west\":{\"uv\":[0,15,1,15.25],\"texture\":\"#0\"},\"up\":{\"uv\":[0,15,16,16],\"texture\":\"#0\"},\"down\":{\"uv\":[0,15,16,16],\"texture\":\"#0\"}}},{\"from\":[0," + thicc + ",1],\"to\":[1," + thic2 + ",15],\"faces\":{\"north\":{\"uv\":[0,0,1,0.25],\"texture\":\"#0\"},\"east\":{\"uv\":[0,0,14,0.25],\"texture\":\"#0\"},\"south\":{\"uv\":[0,0,1,0.25],\"texture\":\"#0\"},\"west\":{\"uv\":[1,0,15,0.25],\"texture\":\"#0\"},\"up\":{\"uv\":[0,1,1,15],\"texture\":\"#0\"},\"down\":{\"uv\":[0,1,1,15],\"texture\":\"#0\"}}},{\"from\":[15," + thicc + ",1],\"to\":[16," + thic2 + ",15],\"faces\":{\"north\":{\"uv\":[15,15,16,15.25],\"texture\":\"#0\"},\"east\":{\"uv\":[1,15,15,15.25],\"texture\":\"#0\"},\"south\":{\"uv\":[0,15,1,15.25],\"texture\":\"#0\"},\"west\":{\"uv\":[1,15,15,15.25],\"texture\":\"#0\"},\"up\":{\"uv\":[15,1,16,15],\"texture\":\"#0\"},\"down\":{\"uv\":[15,1,16,15],\"texture\":\"#0\"}}}]}"));

		if(si.isCustom)
		{
			ResourceLocation textures_blocks_base = new ResourceLocation(reg.getNamespace(), "textures/blocks/" + reg.getPath() + "_base.png");
			ResourceLocation textures_blocks_top = new ResourceLocation(reg.getNamespace(), "textures/blocks/" + reg.getPath() + "_top.png");
			ResourceLocation textures_blocks_base_mcmeta = new ResourceLocation(reg.getNamespace(), "textures/blocks/" + reg.getPath() + "_base.png.mcmeta");
			ResourceLocation textures_blocks_top_mcmeta = new ResourceLocation(reg.getNamespace(), "textures/blocks/" + reg.getPath() + "_top.png.mcmeta");
			{
				String n = reg.getPath().startsWith("custom_solar_panel_") ? reg.getPath().substring(19) : reg.getPath().substring(12);
				resourceMap.put(textures_blocks_base, ofFile(() -> new File(new File(SolarsSF.getConfigDir(), "textures"), n + "_base.png")));
				resourceMap.put(textures_blocks_base_mcmeta, ofFile(() -> new File(new File(SolarsSF.getConfigDir(), "textures"), n + "_base.mcmeta")));
				resourceMap.put(textures_blocks_top, ofFile(() -> new File(new File(SolarsSF.getConfigDir(), "textures"), n + "_top.png")));
				resourceMap.put(textures_blocks_top_mcmeta, ofFile(() -> new File(new File(SolarsSF.getConfigDir(), "textures"), n + "_top.mcmeta")));
			}
		}
	}

	boolean calling = false;

	@Override
	public InputStream getInputStream(ResourceLocation location) throws IOException
	{
		if(calling)
			return null;

		calling = true;
		try
		{
			InputStream in = resourceMap.get(location).create();
			calling = false;
			return in;
		} catch(RuntimeException e)
		{
			calling = false;
			if(e.getCause() instanceof IOException)
				throw (IOException) e.getCause();
			throw e;
		}
	}

	@Override
	public boolean resourceExists(ResourceLocation location)
	{
		IResourceStreamSupplier s;
		return (s = resourceMap.get(location)) != null && s.exists();
	}

	@Override
	public Set<String> getResourceDomains()
	{
		return domains;
	}

	@Override
	public <T extends IMetadataSection> T getPackMetadata(MetadataSerializer metadataSerializer, String metadataSectionName) throws IOException
	{
		return readMetadata(metadataSerializer, new ByteArrayInputStream("{\"pack\": {\"pack_format\": 1,\"description\": \"External & Generated resources for SolarFluxReborn\"}}".getBytes()), metadataSectionName);
	}

	static <T extends IMetadataSection> T readMetadata(MetadataSerializer metadataSerializer, InputStream p_110596_1_, String sectionName)
	{
		JsonObject jsonobject = null;
		BufferedReader bufferedreader = null;

		try
		{
			bufferedreader = new BufferedReader(new InputStreamReader(p_110596_1_, StandardCharsets.UTF_8));
			jsonobject = (new JsonParser()).parse(bufferedreader).getAsJsonObject();
		} catch(RuntimeException runtimeexception)
		{
			throw new JsonParseException(runtimeexception);
		} finally
		{
			IOUtils.closeQuietly((Reader) bufferedreader);
		}

		return (T) metadataSerializer.parseMetadataSection(sectionName, jsonobject);
	}

	@Override
	public BufferedImage getPackImage() throws IOException
	{
		return null;
	}

	@Override
	public String getPackName()
	{
		return "SolarFluxReborn Builtin";
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager)
	{
		injectSolarPanelLanguages();
	}

	public static void injectSolarPanelLanguages()
	{
		if(SolarFluxAPI.SOLAR_PANELS == null)
			return;
		Field i18nLocalef = I18n.class.getDeclaredFields()[0];
		i18nLocalef.setAccessible(true);
		try
		{
			Locale locale = Locale.class.cast(i18nLocalef.get(null));
			Field propertiesf = Locale.class.getDeclaredFields()[2];
			propertiesf.setAccessible(true);
			Map<String, String> properties = Map.class.cast(propertiesf.get(locale));
			String code = Minecraft.getMinecraft().gameSettings.language;
			Map<String, List<String>> langs = new HashMap<>();
			for(SolarInfo si : SolarFluxAPI.SOLAR_PANELS.getValuesCollection())
				if(si.localizations != null)
				{
					for(String lang : si.localizations.keySet())
					{
						List<String> ls = langs.get(lang);
						if(ls == null)
							langs.put(lang, ls = new ArrayList<>());
						String v;
						if(!ls.contains(v = si.localizations.get(lang)))
							ls.add(si.getBlock().getTranslationKey() + ".name=" + v);
					}

					if(si.localizations.containsKey("en_us"))
						properties.put(si.getBlock().getTranslationKey() + ".name", si.localizations.get("en_us"));

					if(si.localizations.containsKey(code))
						properties.put(si.getBlock().getTranslationKey() + ".name", si.localizations.get(code));
				}
			if(langs.containsKey(code))
			{
				StringBuilder sb = new StringBuilder("# Builtin & Generated by Solar Flux Reborn lang file.\n");
				langs.get(code).stream().map(ln -> "\n" + ln).forEach(sb::append);
				LanguageMap.inject(new ByteArrayInputStream(sb.toString().getBytes(StandardCharsets.UTF_8)));
			}
		} catch(ReflectiveOperationException err)
		{
			err.printStackTrace();
		}
	}

	public interface IResourceStreamSupplier
	{
		static IResourceStreamSupplier create(BooleanSupplier exists, IIOSupplier<InputStream> streamable)
		{
			return new IResourceStreamSupplier()
			{
				@Override
				public boolean exists()
				{
					return exists.getAsBoolean();
				}

				@Override
				public InputStream create() throws IOException
				{
					return streamable.get();
				}
			};
		}

		boolean exists();

		InputStream create() throws IOException;
	}

	@FunctionalInterface
	public interface IIOSupplier<T>
	{
		T get() throws IOException;
	}
}