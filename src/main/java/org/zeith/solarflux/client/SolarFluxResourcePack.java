package org.zeith.solarflux.client;

import com.google.gson.JsonObject;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.ResourceLocation;
import org.zeith.solarflux.block.SolarPanelBlock;
import org.zeith.solarflux.items.ItemsSF;
import org.zeith.solarflux.panels.SolarPanels;

import java.io.*;
import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

public class SolarFluxResourcePack
		implements IResourcePack
{
	public final Map<ResourceLocation, IResourceStreamSupplier> resourceMap = new HashMap<>();
	
	private static IResourceStreamSupplier ofText(String text)
	{
		return IResourceStreamSupplier.create(() -> true, () -> new ByteArrayInputStream(text.getBytes()));
	}
	
	private static IResourceStreamSupplier ofFile(File file)
	{
		return IResourceStreamSupplier.create(file::isFile, () -> new FileInputStream(file));
	}
	
	private static IResourceStreamSupplier oneOfFiles(File... file)
	{
		return IResourceStreamSupplier.create(
				() -> Arrays.stream(file).anyMatch(File::isFile),
				() -> new FileInputStream(Arrays.stream(file).filter(File::isFile).findFirst().orElseThrow(FileNotFoundException::new))
		);
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
		
		ItemsSF.JS_MATERIALS.forEach(i ->
		{
			ResourceLocation reg = i.getRegistryName();
			
			ResourceLocation models_item = new ResourceLocation(reg.getNamespace(), "models/item/" + reg.getPath() + ".json");
			
			resourceMap.put(models_item, ofText("{\"parent\":\"item/generated\",\"textures\":{\"layer0\":\"" + reg.getNamespace() + ":items/materials/" + reg.getPath() + "\"}}"));
			
			File textures = new File(SolarPanels.CONFIG_DIR, "textures");
			File items = new File(textures, "items");
			ResourceLocation textures_items = new ResourceLocation(reg.getNamespace(), "textures/items/materials/" + reg.getPath() + ".png");
			{
				resourceMap.put(textures_items, ofFile(new File(items, reg.getPath() + ".png")));
			}
		});
		
		SolarPanels.listPanels().forEach(si ->
		{
			SolarPanelBlock blk = si.getBlock();
			ResourceLocation reg = blk.identifier;
			
			ResourceLocation blockstate = new ResourceLocation(reg.getNamespace(), "blockstates/" + reg.getPath() + ".json");
			ResourceLocation models_block = new ResourceLocation(reg.getNamespace(), "models/" + reg.getPath() + ".json");
			ResourceLocation models_item = new ResourceLocation(reg.getNamespace(), "models/item/" + reg.getPath() + ".json");
			
			float thicc = si.getPanelData().height * 16F;
			float thic2 = thicc + 0.25F;
			float reverseThicc = 16 - thicc;
			
			resourceMap.put(blockstate, ofText("{\"variants\":{\"\":{\"model\":\"" + reg.getNamespace() + ":block/" + reg.getPath() + "\"}}}"));
			resourceMap.put(models_item, ofText("{\"parent\":\"" + reg.getNamespace() + ":" + reg.getPath() + "\"}"));
			
			// Block Model
			resourceMap.put(models_block, ofText("{\"parent\":\"block/block\",\"textures\":{\"0\":\"" + reg.getNamespace() + ":blocks/" + reg.getPath() + "_base\",\"1\":\"" + reg.getNamespace() + ":blocks/" + reg.getPath() + "_top\",\"particle\":\"solarflux:blocks/example_base\"},\"elements\":[{\"name\":\"base\",\"from\":[0,0,0],\"to\":[16," + thicc + ",16],\"faces\":{\"north\":{\"uv\":[0," + reverseThicc + ",16,16],\"texture\":\"#0\"},\"east\":{\"uv\":[0," + reverseThicc + ",16,16],\"texture\":\"#0\"},\"south\":{\"uv\":[0," + reverseThicc + ",16,16],\"texture\":\"#0\"},\"west\":{\"uv\":[0," + reverseThicc + ",16,16],\"texture\":\"#0\"},\"up\":{\"uv\":[0,0,16,16],\"texture\":\"#1\"},\"down\":{\"uv\":[0,0,16,16],\"texture\":\"#0\"}}},{\"from\":[0," + thicc + ",0],\"to\":[16," + thic2 + ",1],\"faces\":{\"north\":{\"uv\":[0,0,16,0.25],\"texture\":\"#0\"},\"east\":{\"uv\":[0,0,1,0.25],\"texture\":\"#0\"},\"south\":{\"uv\":[0,0,16,0.25],\"texture\":\"#0\"},\"west\":{\"uv\":[0,0,1,0.25],\"texture\":\"#0\"},\"up\":{\"uv\":[0,0,16,1],\"texture\":\"#0\"},\"down\":{\"uv\":[0,0,16,1],\"texture\":\"#0\"}}},{\"from\":[0," + thicc + ",15],\"to\":[16," + thic2 + ",16],\"faces\":{\"north\":{\"uv\":[0,15,16,15.25],\"texture\":\"#0\"},\"east\":{\"uv\":[0,15,1,15.25],\"texture\":\"#0\"},\"south\":{\"uv\":[0,15,16,15.25],\"texture\":\"#0\"},\"west\":{\"uv\":[0,15,1,15.25],\"texture\":\"#0\"},\"up\":{\"uv\":[0,15,16,16],\"texture\":\"#0\"},\"down\":{\"uv\":[0,15,16,16],\"texture\":\"#0\"}}},{\"from\":[0," + thicc + ",1],\"to\":[1," + thic2 + ",15],\"faces\":{\"north\":{\"uv\":[0,0,1,0.25],\"texture\":\"#0\"},\"east\":{\"uv\":[0,0,14,0.25],\"texture\":\"#0\"},\"south\":{\"uv\":[0,0,1,0.25],\"texture\":\"#0\"},\"west\":{\"uv\":[1,0,15,0.25],\"texture\":\"#0\"},\"up\":{\"uv\":[0,1,1,15],\"texture\":\"#0\"},\"down\":{\"uv\":[0,1,1,15],\"texture\":\"#0\"}}},{\"from\":[15," + thicc + ",1],\"to\":[16," + thic2 + ",15],\"faces\":{\"north\":{\"uv\":[15,15,16,15.25],\"texture\":\"#0\"},\"east\":{\"uv\":[1,15,15,15.25],\"texture\":\"#0\"},\"south\":{\"uv\":[0,15,1,15.25],\"texture\":\"#0\"},\"west\":{\"uv\":[1,15,15,15.25],\"texture\":\"#0\"},\"up\":{\"uv\":[15,1,16,15],\"texture\":\"#0\"},\"down\":{\"uv\":[15,1,16,15],\"texture\":\"#0\"}}}]}"));
			
			if(si.isCustom)
			{
				File textures = new File(SolarPanels.CONFIG_DIR, "textures");
				File blocks = new File(textures, "blocks");
				ResourceLocation textures_blocks_base = new ResourceLocation(reg.getNamespace(), "textures/blocks/" + reg.getPath() + "_base.png");
				ResourceLocation textures_blocks_top = new ResourceLocation(reg.getNamespace(), "textures/blocks/" + reg.getPath() + "_top.png");
				ResourceLocation textures_blocks_base_mcmeta = new ResourceLocation(reg.getNamespace(), "textures/blocks/" + reg.getPath() + "_base.png.mcmeta");
				ResourceLocation textures_blocks_top_mcmeta = new ResourceLocation(reg.getNamespace(), "textures/blocks/" + reg.getPath() + "_top.png.mcmeta");
				{
					String n = reg.getPath().startsWith("sp_custom_") ? reg.getPath().substring(10) : reg.getPath().substring(3);
					resourceMap.put(textures_blocks_base, ofFile(new File(blocks, n + "_base.png")));
					resourceMap.put(textures_blocks_base_mcmeta, oneOfFiles(new File(blocks, n + "_base.mcmeta"), new File(blocks, n + "_base.png.mcmeta")));
					resourceMap.put(textures_blocks_top, ofFile(new File(blocks, n + "_top.png")));
					resourceMap.put(textures_blocks_top_mcmeta, oneOfFiles(new File(blocks, n + "_top.mcmeta"), new File(blocks, n + "_top.png.mcmeta")));
				}
			}
		});
	}
	
	@Override
	public void close()
	{
	}
	
	@Override
	public InputStream getRootResource(String fileName) throws IOException
	{
		throw new FileNotFoundException(fileName);
	}
	
	@Override
	public InputStream getResource(ResourcePackType type, ResourceLocation location) throws IOException
	{
		try
		{
			return resourceMap.get(location).create();
		} catch(RuntimeException e)
		{
			if(e.getCause() instanceof IOException)
				throw (IOException) e.getCause();
			throw e;
		}
	}
	
	@Override
	public Collection<ResourceLocation> getResources(ResourcePackType type, String namespaceIn, String pathIn, int maxDepthIn, Predicate<String> filterIn)
	{
		return Collections.emptyList();
	}
	
	@Override
	public boolean hasResource(ResourcePackType type, ResourceLocation location)
	{
		IResourceStreamSupplier s;
		return (s = resourceMap.get(location)) != null && s.exists();
	}
	
	@Override
	public Set<String> getNamespaces(ResourcePackType type)
	{
		return Collections.singleton("solarflux");
	}
	
	@Override
	public <T> T getMetadataSection(IMetadataSectionSerializer<T> deserializer) throws IOException
	{
		if(deserializer.getMetadataSectionName().equals("pack"))
		{
			JsonObject obj = new JsonObject();
			obj.addProperty("pack_format", 6);
			obj.addProperty("description", "Generated resources for SolarFlux");
			return deserializer.fromJson(obj);
		}
		return null;
	}
	
	@Override
	public String getName()
	{
		return "Solar Flux Generated Resources";
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