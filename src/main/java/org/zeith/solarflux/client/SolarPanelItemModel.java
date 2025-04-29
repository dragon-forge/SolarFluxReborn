package org.zeith.solarflux.client;

import com.google.gson.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.*;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import org.jetbrains.annotations.*;
import org.zeith.hammerlib.client.model.*;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.hammerlib.util.mcf.Resources;
import org.zeith.solarflux.block.SolarPanelBlock;

import java.util.*;
import java.util.function.Function;

import static org.zeith.solarflux.client.SolarPanelBakedModel.createSolarPanelQuads;

@LoadUnbakedGeometry(path = "solar_panel")
public class SolarPanelItemModel
		implements IUnbakedGeometry<SolarPanelItemModel>
{
	final SolarPanelBlock block;
	Material baseTx, topTx;
	
	public SolarPanelItemModel(JsonObject obj, JsonDeserializationContext context)
	{
		this.block = Cast.optionally(BuiltInRegistries.BLOCK.get(Resources.location(GsonHelper.getAsString(obj, "panel"))), SolarPanelBlock.class)
				.orElseThrow(() -> new JsonSyntaxException("Unable to find solar panel block by id '" + GsonHelper.getAsString(obj, "panel") + "'"));
		
		var registryName = BuiltInRegistries.BLOCK.getKey(block);
		
		baseTx = new Material(InventoryMenu.BLOCK_ATLAS, Resources.location(registryName.getNamespace(), "block/" + registryName.getPath() + "_base"));
		topTx = new Material(InventoryMenu.BLOCK_ATLAS, Resources.location(registryName.getNamespace(), "block/" + registryName.getPath() + "_top"));
	}
	
	@Override
	public BakedModel bake(IGeometryBakingContext context, ModelBaker bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides)
	{
		return new Baked(block, spriteGetter.apply(topTx), spriteGetter.apply(baseTx));
	}
	
	private static class Baked
			implements IBakedModel
	{
		public static final FaceBakery COOKER = new FaceBakery();
		
		public final SolarPanelBlock block;
		public final TextureAtlasSprite top, base;
		
		public Baked(SolarPanelBlock block, TextureAtlasSprite top, TextureAtlasSprite base)
		{
			this.block = block;
			this.top = top;
			this.base = base;
		}
		
		@Override
		public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction sideIn, @NotNull RandomSource rand, @NotNull ModelData data, @Nullable RenderType renderType)
		{
			SolarPanelModelData spmd = SolarPanelModelData.findOrItem(data);
			List<BakedQuad> quads = new ArrayList<>();
			Direction[] sides = sideIn == null ? Direction.values() : new Direction[] {sideIn};
			float h = block.panel.getPanelData().height * 16F;
			for(Direction side : sides)
				if(side != null)
					createSolarPanelQuads(quads, side, h, top, base, spmd);
			return quads;
		}
		
		@Override
		public boolean useAmbientOcclusion()
		{
			return false;
		}
		
		@Override
		public boolean isGui3d()
		{
			return false;
		}
		
		@Override
		public boolean usesBlockLight()
		{
			return true;
		}
		
		@Override
		public boolean isCustomRenderer()
		{
			return false;
		}
		
		final RandomSource rng = RandomSource.create();
		
		@Override
		public TextureAtlasSprite getParticleIcon()
		{
			return rng.nextInt(3) == 0 ? top : base;
		}
	}
}