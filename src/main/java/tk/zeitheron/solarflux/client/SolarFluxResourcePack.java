package tk.zeitheron.solarflux.client;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

import com.google.gson.JsonObject;

import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.SimpleReloadableResourceManager;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.ResourceLocation;
import tk.zeitheron.solarflux.block.SolarPanelBlock;
import tk.zeitheron.solarflux.panels.SolarPanels;

public class SolarFluxResourcePack implements IResourcePack
{
	public final Map<ResourceLocation, IResourceStreamSupplier> resourceMap = new HashMap<>();
	
	public static void addResourcePack(SimpleReloadableResourceManager manager)
	{
		manager.addResourcePack(getPackInstance());
	}
	
	private static IResourceStreamSupplier ofText(String text)
	{
		return IResourceStreamSupplier.create(() -> true, () -> new ByteArrayInputStream(text.getBytes()));
	}
	
	private static IResourceStreamSupplier ofFile(File file)
	{
		return IResourceStreamSupplier.create(file::isFile, () -> new FileInputStream(file));
	}
	
	private static IResourceStreamSupplier ofInternal(String path)
	{
		boolean exists = false;
		
		try(InputStream in = SolarFluxResourcePack.class.getResourceAsStream(path))
		{
			exists = in != null;
		} catch(IOException e)
		{
			exists = false;
		}
		
		final boolean fe = exists;
		
		return IResourceStreamSupplier.create(() -> fe, () -> SolarFluxResourcePack.class.getResourceAsStream(path));
	}
	
	static SolarFluxResourcePack packInstance;
	
	public static SolarFluxResourcePack getPackInstance()
	{
		if(packInstance == null)
			packInstance = new SolarFluxResourcePack();
		packInstance.init();
		return packInstance;
	}
	
	public void init()
	{
		resourceMap.clear();
		
		SolarPanels.listPanels().forEach(si ->
		{
			SolarPanelBlock blk = si.getBlock();
			ResourceLocation reg = blk.getRegistryName();
			
			ResourceLocation blockstate = new ResourceLocation(reg.getNamespace(), "blockstates/" + reg.getPath() + ".json");
			ResourceLocation models_block = new ResourceLocation(reg.getNamespace(), "models/" + reg.getPath() + ".json");
			ResourceLocation models_item = new ResourceLocation(reg.getNamespace(), "models/item/" + reg.getPath() + ".json");
			
			float thicc = si.getClientPanelData().height * 16F;
			float thic2 = thicc + 0.25F;
			float reverseThicc = 16 - thicc;
			
			resourceMap.put(blockstate, ofText("{\"variants\":{\"\":{\"model\":\"" + reg.getNamespace() + ":block/" + reg.getPath() + "\"}}}"));
			resourceMap.put(models_item, ofText("{\"parent\":\"" + reg.getNamespace() + ":" + reg.getPath() + "\"}"));
			
			// Block Model
			resourceMap.put(models_block, ofText("{\"parent\":\"block/block\",\"textures\":{\"0\":\"" + reg.getNamespace() + ":blocks/" + reg.getPath() + "_base\",\"1\":\"" + reg.getNamespace() + ":blocks/" + reg.getPath() + "_top\",\"particle\":\"solarflux:blocks/example_base\"},\"elements\":[{\"name\":\"base\",\"from\":[0,0,0],\"to\":[16," + thicc + ",16],\"faces\":{\"north\":{\"uv\":[0," + reverseThicc + ",16,16],\"texture\":\"#0\"},\"east\":{\"uv\":[0," + reverseThicc + ",16,16],\"texture\":\"#0\"},\"south\":{\"uv\":[0," + reverseThicc + ",16,16],\"texture\":\"#0\"},\"west\":{\"uv\":[0," + reverseThicc + ",16,16],\"texture\":\"#0\"},\"up\":{\"uv\":[0,0,16,16],\"texture\":\"#1\"},\"down\":{\"uv\":[0,0,16,16],\"texture\":\"#0\"}}},{\"from\":[0," + thicc + ",0],\"to\":[16," + thic2 + ",1],\"faces\":{\"north\":{\"uv\":[0,0,16,0.25],\"texture\":\"#0\"},\"east\":{\"uv\":[0,0,1,0.25],\"texture\":\"#0\"},\"south\":{\"uv\":[0,0,16,0.25],\"texture\":\"#0\"},\"west\":{\"uv\":[0,0,1,0.25],\"texture\":\"#0\"},\"up\":{\"uv\":[0,0,16,1],\"texture\":\"#0\"},\"down\":{\"uv\":[0,0,16,1],\"texture\":\"#0\"}}},{\"from\":[0," + thicc + ",15],\"to\":[16," + thic2 + ",16],\"faces\":{\"north\":{\"uv\":[0,15,16,15.25],\"texture\":\"#0\"},\"east\":{\"uv\":[0,15,1,15.25],\"texture\":\"#0\"},\"south\":{\"uv\":[0,15,16,15.25],\"texture\":\"#0\"},\"west\":{\"uv\":[0,15,1,15.25],\"texture\":\"#0\"},\"up\":{\"uv\":[0,15,16,16],\"texture\":\"#0\"},\"down\":{\"uv\":[0,15,16,16],\"texture\":\"#0\"}}},{\"from\":[0," + thicc + ",1],\"to\":[1," + thic2 + ",15],\"faces\":{\"north\":{\"uv\":[0,0,1,0.25],\"texture\":\"#0\"},\"east\":{\"uv\":[0,0,14,0.25],\"texture\":\"#0\"},\"south\":{\"uv\":[0,0,1,0.25],\"texture\":\"#0\"},\"west\":{\"uv\":[1,0,15,0.25],\"texture\":\"#0\"},\"up\":{\"uv\":[0,1,1,15],\"texture\":\"#0\"},\"down\":{\"uv\":[0,1,1,15],\"texture\":\"#0\"}}},{\"from\":[15," + thicc + ",1],\"to\":[16," + thic2 + ",15],\"faces\":{\"north\":{\"uv\":[15,15,16,15.25],\"texture\":\"#0\"},\"east\":{\"uv\":[1,15,15,15.25],\"texture\":\"#0\"},\"south\":{\"uv\":[0,15,1,15.25],\"texture\":\"#0\"},\"west\":{\"uv\":[1,15,15,15.25],\"texture\":\"#0\"},\"up\":{\"uv\":[15,1,16,15],\"texture\":\"#0\"},\"down\":{\"uv\":[15,1,16,15],\"texture\":\"#0\"}}}]}"));
			
			if(si.isCustom)
			{
				File customDir = new File(SolarPanels.CONFIG_DIR, "textures");
				ResourceLocation textures_blocks_base = new ResourceLocation(reg.getNamespace(), "textures/blocks/" + reg.getPath() + "_base.png");
				ResourceLocation textures_blocks_top = new ResourceLocation(reg.getNamespace(), "textures/blocks/" + reg.getPath() + "_top.png");
				ResourceLocation textures_blocks_base_mcmeta = new ResourceLocation(reg.getNamespace(), "textures/blocks/solar_base_" + reg.getPath() + "_base.png.mcmeta");
				ResourceLocation textures_blocks_top_mcmeta = new ResourceLocation(reg.getNamespace(), "textures/blocks/solar_top_" + reg.getPath() + "_top.png.mcmeta");
				{
					String n = reg.getPath().startsWith("sp_custom_") ? reg.getPath().substring(10) : reg.getPath().substring(3);
					resourceMap.put(textures_blocks_base, ofFile(new File(customDir, n + "_base.png")));
					resourceMap.put(textures_blocks_base_mcmeta, ofFile(new File(customDir, n + "_base.mcmeta")));
					resourceMap.put(textures_blocks_top, ofFile(new File(customDir, n + "_top.png")));
					resourceMap.put(textures_blocks_top_mcmeta, ofFile(new File(customDir, n + "_top.mcmeta")));
				}
			}
		});
	}
	
	@Override
	public void close() throws IOException
	{
	}
	
	@Override
	public InputStream getRootResourceStream(String fileName) throws IOException
	{
		throw new FileNotFoundException(fileName);
	}
	
	@Override
	public InputStream getResourceStream(ResourcePackType type, ResourceLocation location) throws IOException
	{
		try
		{
			InputStream in = resourceMap.get(location).create();
			return in;
		} catch(RuntimeException e)
		{
			if(e.getCause() instanceof IOException)
				throw (IOException) e.getCause();
			throw e;
		}
	}
	
	@Override
	public Collection<ResourceLocation> getAllResourceLocations(ResourcePackType type, String pathIn, int maxDepth, Predicate<String> filter)
	{
		return Collections.emptyList();
	}
	
	@Override
	public boolean resourceExists(ResourcePackType type, ResourceLocation location)
	{
		IResourceStreamSupplier s;
		return (s = resourceMap.get(location)) != null && s.exists();
	}
	
	@Override
	public Set<String> getResourceNamespaces(ResourcePackType type)
	{
		return Collections.singleton("solarflux");
	}
	
	@Override
	public <T> T getMetadata(IMetadataSectionSerializer<T> deserializer) throws IOException
	{
		JsonObject obj = new JsonObject();
		obj.addProperty("pack_format", 4);
		obj.addProperty("description", "Generated resources for SolarFlux");
		return deserializer.deserialize(obj);
	}
	
	@Override
	public String getName()
	{
		return "Solar Flux Generated Resources";
	}
	
	public static interface IResourceStreamSupplier
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
	public static interface IIOSupplier<T>
	{
		T get() throws IOException;
	}
}