package tk.zeitheron.solarflux.compat.thaumcraft;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Joiner;
import tk.zeitheron.solarflux.InfoSF;
import tk.zeitheron.solarflux.SolarFlux;
import tk.zeitheron.solarflux.api.SolarFluxAPI;
import tk.zeitheron.solarflux.api.SolarInfo;
import tk.zeitheron.solarflux.api.compat.ISolarFluxCompat;
import tk.zeitheron.solarflux.api.compat.SFCompat;
import tk.zeitheron.solarflux.block.BlockBaseSolar;
import tk.zeitheron.solarflux.compat.thaumcraft.research.ResearchAddendumBuilder;
import tk.zeitheron.solarflux.compat.thaumcraft.research.ResearchEntryBuilder;
import tk.zeitheron.solarflux.compat.thaumcraft.research.ResearchStageBuilder;
import tk.zeitheron.solarflux.compat.thaumcraft.theorycraft.AidSolarPanel;
import tk.zeitheron.solarflux.compat.thaumcraft.theorycraft.CardPhotovoltaicCell;
import tk.zeitheron.solarflux.compat.thaumcraft.theorycraft.CardSolarPanel;
import tk.zeitheron.solarflux.init.ItemsSF;
import tk.zeitheron.solarflux.init.SolarsSF;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.capabilities.IPlayerKnowledge.EnumKnowledgeType;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.internal.CommonInternals;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.api.research.ResearchEntry.EnumResearchMeta;
import thaumcraft.api.research.ResearchStage.Knowledge;
import thaumcraft.api.research.ScanBlock;
import thaumcraft.api.research.ScanningManager;
import thaumcraft.api.research.theorycraft.TheorycraftManager;
import thaumcraft.common.lib.CommandThaumcraft;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.lib.research.ResearchManager;

@SFCompat(modid = "thaumcraft")
public class CompatThaumcraft implements ISolarFluxCompat
{
	public static ResearchCategory RES_CAT;
	
	public static final Aspect SOL = Aspect.getAspect("sol") != null ? Aspect.getAspect("sol") : new Aspect("sol", 0xFFB600, new Aspect[] { Aspect.LIGHT, Aspect.LIFE }, new ResourceLocation(InfoSF.MOD_ID, "textures/gui/aspect_sol.png"), 1);
	
	public static SolarInfo alchemicalSolar, brassSolar, thaumiumSolar, voidSolar;
	
	@Override
	public void registerSolarInfos(List<SolarInfo> panels)
	{
		panels.add(alchemicalSolar = new SolarInfo(8, 64, 125_000).setRegistryName(InfoSF.MOD_ID, "alchemical"));
		panels.add(brassSolar = new SolarInfo(32, 256, 425_000).setRegistryName(InfoSF.MOD_ID, "alchemical_brass"));
		panels.add(thaumiumSolar = new SolarInfo(128, 1_024, 2_000_000).setRegistryName(InfoSF.MOD_ID, "thaumium"));
		panels.add(voidSolar = new SolarInfo(512, 4_096, 8_000_000).setRegistryName(InfoSF.MOD_ID, "void_metal"));
	}
	
	@Override
	public void init()
	{
		RES_CAT = ResearchCategories.registerCategory("SOLARFLUX", "UNLOCKALCHEMY", new AspectList().add(Aspect.ALCHEMY, 10).add(Aspect.LIGHT, 2).add(Aspect.MAGIC, 10).add(Aspect.LIFE, 5).add(Aspect.AVERSION, 5).add(Aspect.DESIRE, 5).add(Aspect.WATER, 5), new ResourceLocation(InfoSF.MOD_ID, "textures/items/photovoltaic_cell_6.png"), new ResourceLocation(InfoSF.MOD_ID, "textures/gui/thaumonomicon_back.jpg"), new ResourceLocation(InfoSF.MOD_ID, "textures/gui/gui_research_back_over.png"));
		registerScan();
		registerTheorycraft();
		registerTCRecipes();
	}
	
	@Override
	public void postInit()
	{
		reloadResearch();
		insertAspect();
	}
	
	public void registerTCRecipes()
	{
		// CRUCIBLE RECIPES
		
		addCrucibleRecipe("alchemical_solar_panel", "SFR_SOLARFLUX", new ItemStack(alchemicalSolar.getBlock()), new ItemStack(SolarsSF.CORE_PANELS[0].getBlock()), new AspectList().add(SOL, 20).add(Aspect.MECHANISM, 10));
		
		// ARCANE WORKBENCH RECIPES
		
		addShapedArcaneRecipe("brass_solar_panel", "SFR_BRASS_SOLAR_PANEL", 20, new AspectList(), new ItemStack(brassSolar.getBlock()), "ppp", "bsb", "bbb", 'p', new ItemStack(ItemsSF.PHOTOVOLTAIC_CELL_1), 'b', "plateBrass", 's', new ItemStack(alchemicalSolar.getBlock()));
		
		// INFUSION MATRIX RECIPES
		
		addInfusionRecipe("thaumium_solar_panel", new ItemStack(thaumiumSolar.getBlock()), "SFR_THAUMIUM_SOLAR_PANEL", 3, new ItemStack(brassSolar.getBlock()), new AspectList().add(SOL, 40).add(Aspect.ENERGY, 20), new ItemStack(ItemsTC.plate, 1, 2), new ItemStack(ItemsSF.PHOTOVOLTAIC_CELL_2), new ItemStack(ItemsTC.plate, 1, 2), new ItemStack(ItemsSF.PHOTOVOLTAIC_CELL_2), new ItemStack(ItemsTC.plate, 1, 2), new ItemStack(ItemsSF.PHOTOVOLTAIC_CELL_2), new ItemStack(ItemsTC.plate, 1, 2), new ItemStack(ItemsTC.mechanismSimple));
		addInfusionRecipe("void_metal_solar_panel", new ItemStack(voidSolar.getBlock()), "SFR_VOID_SOLAR_PANEL", 4, new ItemStack(thaumiumSolar.getBlock()), new AspectList().add(SOL, 60).add(Aspect.ENERGY, 40).add(Aspect.VOID, 20), new ItemStack(ItemsTC.plate, 1, 3), new ItemStack(ItemsSF.PHOTOVOLTAIC_CELL_3), new ItemStack(ItemsTC.plate, 1, 3), new ItemStack(ItemsSF.PHOTOVOLTAIC_CELL_3), new ItemStack(ItemsTC.plate, 1, 3), new ItemStack(ItemsSF.PHOTOVOLTAIC_CELL_3), new ItemStack(ItemsTC.plate, 1, 3), new ItemStack(ItemsTC.mechanismComplex));
	}
	
	List<Block> solarPanels;
	Block[] solarPanelsA;
	
	public void registerScan()
	{
		solarPanels = ForgeRegistries.BLOCKS.getValuesCollection().stream().filter(b -> b instanceof BlockBaseSolar).collect(Collectors.toList());
		
		ScanningManager.addScannableThing(new ScanBlock("!SOLARPANELS", solarPanelsA = solarPanels.toArray(new Block[solarPanels.size()])));
	}
	
	public void registerTheorycraft()
	{
		for(Block sp : solarPanelsA)
			TheorycraftManager.registerAid(new AidSolarPanel(sp));
		TheorycraftManager.registerCard(CardSolarPanel.class);
		TheorycraftManager.registerCard(CardPhotovoltaicCell.class);
	}
	
	public void reloadResearch()
	{
		SolarFlux.LOG.info("Registering TC researches...");
		
		new REB().setBaseInfo("SFR_SOLARFLUX", "solarflux", 0, 0, new ResourceLocation(InfoSF.MOD_ID, "textures/items/photovoltaic_cell_6.png")).setMeta(EnumResearchMeta.HIDDEN, EnumResearchMeta.SPIKY).setStages(new RSB().setText("research_stage." + InfoSF.MOD_ID + ":solarflux.1").setKnow(new Knowledge(EnumKnowledgeType.OBSERVATION, RES_CAT, 1)).build(), new RSB().setText("research_stage." + InfoSF.MOD_ID + ":solarflux.2").setRecipes(InfoSF.MOD_ID + ":alchemical_solar_panel").build()).setParents("FIRSTSTEPS", "!SOLARPANELS").buildAndRegister();
		
		new REB().setBaseInfo("SFR_BRASS_SOLAR_PANEL", "brass_solar_panel", 0, 2, new ItemStack(brassSolar.getBlock())).setMeta(EnumResearchMeta.HIDDEN).setStages(new RSB().setText("research_stage." + InfoSF.MOD_ID + ":brass_solar_panel.1").setConsumedItems(new ItemStack(ItemsSF.PHOTOVOLTAIC_CELL_1)).setRequiredCraft(new ItemStack(SolarsSF.CORE_PANELS[1].getBlock())).setKnow(new Knowledge(EnumKnowledgeType.THEORY, RES_CAT, 1)).build(), new RSB().setText("research_stage." + InfoSF.MOD_ID + ":brass_solar_panel.2").setRecipes(InfoSF.MOD_ID + ":brass_solar_panel").build()).setParents("SFR_SOLARFLUX", "METALLURGY@1").buildAndRegister();
		new REB().setBaseInfo("SFR_THAUMIUM_SOLAR_PANEL", "thaumium_solar_panel", 0, 4, new ItemStack(thaumiumSolar.getBlock())).setMeta(EnumResearchMeta.HIDDEN).setStages(new RSB().setText("research_stage." + InfoSF.MOD_ID + ":thaumium_solar_panel.1").setConsumedItems(new ItemStack(ItemsSF.PHOTOVOLTAIC_CELL_2)).setRequiredCraft(new ItemStack(brassSolar.getBlock())).setKnow(new Knowledge(EnumKnowledgeType.THEORY, RES_CAT, 1)).build(), new RSB().setText("research_stage." + InfoSF.MOD_ID + ":thaumium_solar_panel.2").setRecipes(InfoSF.MOD_ID + ":thaumium_solar_panel").build()).setParents("SFR_BRASS_SOLAR_PANEL", "BASEARTIFICE", "METALLURGY@2").buildAndRegister();
		new REB().setBaseInfo("SFR_VOID_SOLAR_PANEL", "void_metal_solar_panel", 0, 6, new ItemStack(voidSolar.getBlock())).setMeta(EnumResearchMeta.HIDDEN).setStages(new RSB().setText("research_stage." + InfoSF.MOD_ID + ":void_metal_solar_panel.1").setConsumedItems(new ItemStack(ItemsSF.PHOTOVOLTAIC_CELL_3)).setRequiredCraft(new ItemStack(thaumiumSolar.getBlock())).setKnow(new Knowledge(EnumKnowledgeType.THEORY, RES_CAT, 1)).build(), new RSB().setText("research_stage." + InfoSF.MOD_ID + ":void_metal_solar_panel.2").setRecipes(InfoSF.MOD_ID + ":void_metal_solar_panel").build()).setParents("SFR_THAUMIUM_SOLAR_PANEL", "BASEARTIFICE", "BASEELDRITCH").buildAndRegister();
	}
	
	public void insertAspect()
	{
		for(Block nitor : BlocksTC.nitor.values())
			appendAspects(new ItemStack(nitor), new AspectList().add(SOL, 15));
		appendAspects(new ItemStack(Blocks.TORCH), new AspectList().add(SOL, 1));
		appendAspects(new ItemStack(Items.GLOWSTONE_DUST), new AspectList().add(SOL, 10));
		appendAspects(new ItemStack(ItemsSF.MIRROR), new AspectList().add(SOL, 15));
		SolarFluxAPI.SOLAR_PANELS.forEach(si -> appendAspects(new ItemStack(si.getBlock()), new AspectList().add(SOL, 5 + (int) Math.ceil(si.getGeneration()))));
	}
	
	private static void appendAspects(String oreDict, AspectList toAdd)
	{
		List<ItemStack> ores = ThaumcraftApiHelper.getOresWithWildCards(oreDict);
		if(toAdd == null)
			toAdd = new AspectList();
		if(ores != null && ores.size() > 0)
			for(ItemStack ore : ores)
				try
				{
					ItemStack oc = ore.copy();
					oc.setCount(1);
					appendAspects(oc, toAdd);
				} catch(Exception oc)
				{
				}
	}
	
	private static void appendAspects(ItemStack stack, AspectList toAdd)
	{
		toAdd = toAdd.copy();
		
		// Finds item's aspects, and if there are any, adds them to appended
		// aspects
		{
			AspectList al = ThaumcraftCraftingManager.getObjectTags(stack);
			if(al != null)
				toAdd = toAdd.add(al);
		}
		
		CommonInternals.objectTags.put(CommonInternals.generateUniqueItemstackId(stack), toAdd);
	}
	
	private static void removeAspects(ItemStack stack, Aspect... aspects)
	{
		AspectList al = ThaumcraftCraftingManager.getObjectTags(stack);
		if(al != null)
		{
			for(Aspect a : aspects)
				al.remove(a);
			CommonInternals.objectTags.put(CommonInternals.generateUniqueItemstackId(stack), al);
		}
	}
	
	private static String addIfPresent(String item, AspectList al, String prefix)
	{
		Item it = GameRegistry.findRegistry(Item.class).getValue(new ResourceLocation(item));
		if(it != null)
		{
			List<String> fullAspectList = Arrays.stream(al.getAspectsSortedByAmount()).map(a -> al.getAmount(a) + "x " + a.getName()).collect(Collectors.toList());
			SolarFlux.LOG.info("I " + prefix + "found " + item + " and I added some aspects to it! " + Joiner.on(", ").join(fullAspectList));
			if(prefix.isEmpty())
				prefix = "also ";
			appendAspects(new ItemStack(it), al);
		}
		return prefix;
	}
	
	private static class RAB extends ResearchAddendumBuilder
	{
	}
	
	private static class RSB extends ResearchStageBuilder
	{
	}
	
	private static class REB extends ResearchEntryBuilder
	{
		public ResearchEntryBuilder setBaseInfo(String key, String name, int x, int y, Object... icons)
		{
			return super.setBaseInfo(key, "SOLARFLUX", "research_name." + InfoSF.MOD_ID + ":" + name, x, y, icons);
		}
	}
	
	private static Method addResearchToCategory = null;
	
	public static void addResearchToCategory(ResearchEntry ri)
	{
		if(addResearchToCategory == null)
			try
			{
				addResearchToCategory = ResearchManager.class.getDeclaredMethod("addResearchToCategory", ResearchEntry.class);
				addResearchToCategory.setAccessible(true);
			} catch(NoSuchMethodException | SecurityException e)
			{
				SolarFlux.LOG.error(e);
			}
		
		try
		{
			addResearchToCategory.invoke(null, ri);
		} catch(Throwable e)
		{
			SolarFlux.LOG.error(e);
		}
	}
	
	private static void addCrucibleRecipe(String path, String research, ItemStack output, Object catalyst, AspectList aspects)
	{
		ThaumcraftApi.addCrucibleRecipe(new ResourceLocation(InfoSF.MOD_ID, path), new CrucibleRecipe(research, output, catalyst, aspects));
	}
	
	private static void addInfusionRecipe(String path, Object output, String research, int instability, Object catalyst, AspectList aspects, Object... inputs)
	{
		ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(InfoSF.MOD_ID, path), new InfusionRecipe(research, output, instability, aspects, catalyst, inputs));
	}
	
	static ResourceLocation defaultGroup = new ResourceLocation(InfoSF.MOD_ID);
	
	private static void addShapedArcaneRecipe(String path, String res, int vis, AspectList crystals, ItemStack result, Object... recipe)
	{
		ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(InfoSF.MOD_ID, path), new ShapedArcaneRecipe(defaultGroup, res, vis, crystals, result, recipe));
	}
	
	private static void addShapedArcaneRecipe(String path, String res, int vis, AspectList crystals, Item result, Object... recipe)
	{
		ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(InfoSF.MOD_ID, path), new ShapedArcaneRecipe(defaultGroup, res, vis, crystals, result, recipe));
	}
	
	private static void addShapedArcaneRecipe(String path, String res, int vis, AspectList crystals, Block result, Object... recipe)
	{
		ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(InfoSF.MOD_ID, path), new ShapedArcaneRecipe(defaultGroup, res, vis, crystals, result, recipe));
	}
	
	@SubscribeEvent
	public void commandEvent(CommandEvent ce)
	{
		if(ce.getCommand() instanceof CommandThaumcraft && ce.getParameters().length > 0 && ce.getParameters()[0].equalsIgnoreCase("reload"))
		{
			new Thread(() ->
			{
				while(RES_CAT.research.containsKey("SFR_SOLARFLUX"))
					try
					{
						Thread.sleep(10L);
					} catch(InterruptedException e)
					{
						e.printStackTrace();
					}
				
				reloadResearch();
			}).start();
		}
	}
}