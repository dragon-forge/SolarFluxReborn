package org.zeith.solarflux.client;

import com.google.gson.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.zeith.hammerlib.client.model.IBakedModel;
import org.zeith.hammerlib.client.model.LoadUnbakedGeometry;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.solarflux.block.SolarPanelBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@LoadUnbakedGeometry(path = "solar_panel")
public class SolarPanelItemModel
		implements IUnbakedGeometry<SolarPanelItemModel>
{
	final SolarPanelBlock block;
	Material baseTx, topTx;
	
	public SolarPanelItemModel(JsonObject obj, JsonDeserializationContext context)
	{
		this.block = Cast.optionally(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(GsonHelper.getAsString(obj, "panel"))), SolarPanelBlock.class)
				.orElseThrow(() -> new JsonSyntaxException("Unable to find solar panel block by id '" + GsonHelper.getAsString(obj, "panel") + "'"));
		
		var registryName = ForgeRegistries.BLOCKS.getKey(block);
		
		baseTx = new Material(InventoryMenu.BLOCK_ATLAS, new ResourceLocation(registryName.getNamespace(), "block/" + registryName.getPath() + "_base"));
		topTx = new Material(InventoryMenu.BLOCK_ATLAS, new ResourceLocation(registryName.getNamespace(), "block/" + registryName.getPath() + "_top"));
	}
	
	@Override
	public BakedModel bake(IGeometryBakingContext context, ModelBaker bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation)
	{
		return new Baked(block, spriteGetter.apply(topTx), spriteGetter.apply(baseTx), modelLocation);
	}
	
	private static class Baked
			implements IBakedModel
	{
		public static final FaceBakery COOKER = new FaceBakery();
		
		public final ResourceLocation modelName;
		public final SolarPanelBlock block;
		public final TextureAtlasSprite top, base;
		
		public Baked(SolarPanelBlock block, TextureAtlasSprite top, TextureAtlasSprite base, ResourceLocation modelName)
		{
			this.block = block;
			this.top = top;
			this.base = base;
			this.modelName = modelName;
		}
		
		@Override
		public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction sideIn, @NotNull RandomSource rand, @NotNull ModelData data, @Nullable RenderType renderType)
		{
			List<BakedQuad> quads = new ArrayList<>();
			Direction[] sides = sideIn == null ? Direction.values() : new Direction[] { sideIn };
			for(Direction side : sides)
				if(side != null)
				{
					float h = block.panel.getPanelData().height * 16F;
					
					quads.add(COOKER.bakeQuad(
							new Vector3f(0, 0, 0), new Vector3f(16, h, 16),
							new BlockElementFace(null, 0, "#0", new BlockFaceUV(new float[] {
									0,
									side.getAxis() == Direction.Axis.Y ? 0 : (16F - h),
									16,
									16
							}, 4)),
							side == Direction.UP ? top : base, side, BlockModelRotation.X0_Y0, null, true, modelName));
					
					quads.add(COOKER.bakeQuad( //
							new Vector3f(0, h, 1), new Vector3f(1, h + 0.25F, 15), //
							new BlockElementFace(null, 0, "#0", new BlockFaceUV(side != Direction.UP ? new float[] {
									0,
									0,
									16,
									1
							} : new float[] {
									0,
									0,
									1,
									16
							}, 4)), //
							base, side, BlockModelRotation.X0_Y0, null, true, modelName));
					
					quads.add(COOKER.bakeQuad( //
							new Vector3f(15, h, 1), new Vector3f(16, h + 0.25F, 15), //
							new BlockElementFace(null, 0, "#0", new BlockFaceUV(side != Direction.UP ? new float[] {
									0,
									0,
									16,
									1
							} : new float[] {
									15,
									0,
									16,
									16
							}, 4)), //
							base, side, BlockModelRotation.X0_Y0, null, true, modelName));
					
					quads.add(COOKER.bakeQuad( //
							new Vector3f(1, h, 0), new Vector3f(15, h + 0.25F, 1), //
							new BlockElementFace(null, 0, "#0", new BlockFaceUV(new float[] {
									0,
									0,
									16,
									1
							}, 4)), //
							base, side, BlockModelRotation.X0_Y0, null, true, modelName));
					
					quads.add(COOKER.bakeQuad( //
							new Vector3f(1, h, 15), new Vector3f(15, h + 0.25F, 16), //
							new BlockElementFace(null, 0, "#0", new BlockFaceUV(new float[] {
									0,
									0,
									16,
									1
							}, 4)), //
							base, side, BlockModelRotation.X0_Y0, null, true, modelName));
					
					quads.add(COOKER.bakeQuad( //
							new Vector3f(0, h, 0), new Vector3f(1, h + 0.25F, 1), //
							new BlockElementFace(null, 0, "#0", new BlockFaceUV(new float[] {
									0,
									0,
									1,
									1
							}, 4)), //
							base, side, BlockModelRotation.X0_Y0, null, true, modelName));
					
					quads.add(COOKER.bakeQuad( //
							new Vector3f(15, h, 0), new Vector3f(16, h + 0.25F, 1), //
							new BlockElementFace(null, 0, "#0", new BlockFaceUV(new float[] {
									15,
									0,
									16,
									1
							}, 4)), //
							base, side, BlockModelRotation.X0_Y0, null, true, modelName));
					
					quads.add(COOKER.bakeQuad( //
							new Vector3f(15, h, 15), new Vector3f(16, h + 0.25F, 16), //
							new BlockElementFace(null, 0, "#0", new BlockFaceUV(new float[] {
									15,
									15,
									16,
									16
							}, 4)), //
							base, side, BlockModelRotation.X0_Y0, null, true, modelName));
					
					quads.add(COOKER.bakeQuad( //
							new Vector3f(0, h, 15), new Vector3f(1, h + 0.25F, 16), //
							new BlockElementFace(null, 0, "#0", new BlockFaceUV(new float[] {
									0,
									15,
									1,
									16
							}, 4)), //
							base, side, BlockModelRotation.X0_Y0, null, true, modelName));
				}
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