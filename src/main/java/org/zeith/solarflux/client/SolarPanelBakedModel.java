package org.zeith.solarflux.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.IDynamicBakedModel;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.zeith.solarflux.InfoSF;
import org.zeith.solarflux.block.SolarPanelBlock;
import org.zeith.solarflux.block.SolarPanelTile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

public class SolarPanelBakedModel
		implements IDynamicBakedModel
{
	public static final FaceBakery COOKER = new FaceBakery();
	public final SolarPanelBlock block;
	public final ResourceLocation registryName;
	final ResourceLocation modelName = new ModelResourceLocation(InfoSF.MOD_ID, "solar_panel", "");
	
	public SolarPanelBakedModel(SolarPanelBlock spb)
	{
		this.block = spb;
		this.registryName = ForgeRegistries.BLOCKS.getKey(spb);
	}
	
	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction sideIn, @NotNull RandomSource rand, @NotNull ModelData extraData, @Nullable RenderType renderType)
	{
		List<BakedQuad> quads = new ArrayList<>();
		Direction[] sides = sideIn == null ? Direction.values() : new Direction[] { sideIn };
		for(Direction side : sides)
			if(side != null)
			{
				Level world = extraData.get(SolarPanelTile.WORLD_PROP);
				BlockPos pos = extraData.get(SolarPanelTile.POS_PROP);
				
				TextureAtlasSprite top = t_top(), base = t_base();
				
				float h = block.panel.getPanelData().height * 16F;
				
				quads.add(COOKER.bakeQuad( //
						new Vector3f(0, 0, 0), new Vector3f(16, h, 16), //
						new BlockElementFace(null, 0, "#0", new BlockFaceUV(new float[] {
								0,
								side.getAxis() == Direction.Axis.Y ? 0 : (16F - h),
								16,
								16
						}, 4)), //
						side == Direction.UP ? top : base, side, BlockModelRotation.X0_Y0, null, true, modelName));
				
				// world/pos not set? no connected textures == no crash!
				if(world == null || pos == null)
					return quads;
				
				boolean west = false, east = false, north = false, south = false;
				
				if(west = world.getBlockState(pos.west()).getBlock() != block)
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
				
				if(east = world.getBlockState(pos.east()).getBlock() != block)
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
				
				if(north = world.getBlockState(pos.north()).getBlock() != block)
					quads.add(COOKER.bakeQuad( //
							new Vector3f(1, h, 0), new Vector3f(15, h + 0.25F, 1), //
							new BlockElementFace(null, 0, "#0", new BlockFaceUV(new float[] {
									0,
									0,
									16,
									1
							}, 4)), //
							base, side, BlockModelRotation.X0_Y0, null, true, modelName));
				
				if(south = world.getBlockState(pos.south()).getBlock() != block)
					quads.add(COOKER.bakeQuad( //
							new Vector3f(1, h, 15), new Vector3f(15, h + 0.25F, 16), //
							new BlockElementFace(null, 0, "#0", new BlockFaceUV(new float[] {
									0,
									0,
									16,
									1
							}, 4)), //
							base, side, BlockModelRotation.X0_Y0, null, true, modelName));
				
				if(west || north || world.getBlockState(pos.west().north()).getBlock() != block)
					quads.add(COOKER.bakeQuad( //
							new Vector3f(0, h, 0), new Vector3f(1, h + 0.25F, 1), //
							new BlockElementFace(null, 0, "#0", new BlockFaceUV(new float[] {
									0,
									0,
									1,
									1
							}, 4)), //
							base, side, BlockModelRotation.X0_Y0, null, true, modelName));
				
				if(east || north || world.getBlockState(pos.east().north()).getBlock() != block)
					quads.add(COOKER.bakeQuad( //
							new Vector3f(15, h, 0), new Vector3f(16, h + 0.25F, 1), //
							new BlockElementFace(null, 0, "#0", new BlockFaceUV(new float[] {
									15,
									0,
									16,
									1
							}, 4)), //
							base, side, BlockModelRotation.X0_Y0, null, true, modelName));
				
				if(south || east || world.getBlockState(pos.south().east()).getBlock() != block)
					quads.add(COOKER.bakeQuad( //
							new Vector3f(15, h, 15), new Vector3f(16, h + 0.25F, 16), //
							new BlockElementFace(null, 0, "#0", new BlockFaceUV(new float[] {
									15,
									15,
									16,
									16
							}, 4)), //
							base, side, BlockModelRotation.X0_Y0, null, true, modelName));
				
				if(west || south || world.getBlockState(pos.west().south()).getBlock() != block)
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
	public ItemTransforms getTransforms()
	{
		return new ItemTransforms(
				getTransform(ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND),
				getTransform(ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND),
				getTransform(ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND),
				getTransform(ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND),
				getTransform(ItemTransforms.TransformType.HEAD),
				getTransform(ItemTransforms.TransformType.GUI),
				getTransform(ItemTransforms.TransformType.GROUND),
				getTransform(ItemTransforms.TransformType.FIXED)
		);
	}
	
	@Override
	public ItemOverrides getOverrides()
	{
		return ItemOverrides.EMPTY;
	}
	
	public ItemTransform getTransform(ItemTransforms.TransformType type)
	{
		switch(type)
		{
			case GUI:
				return new ItemTransform(new Vector3f(30, 45, 0), new Vector3f(0, 0, 0), new Vector3f(0.625F, 0.625F, 0.625F));
			default:
				break;
		}
		return ItemTransform.NO_TRANSFORM;
	}
	
	@Override
	public TextureAtlasSprite getParticleIcon()
	{
		return ThreadLocalRandom.current().nextInt(5) > 0 ? t_base() : t_top();
	}
	
	final Function<ResourceLocation, TextureAtlasSprite> spriteGetter = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
	ResourceLocation baseTx, topTx;
	
	public TextureAtlasSprite t_base()
	{
		if(baseTx == null)
			baseTx = new ResourceLocation(registryName.getNamespace(), "block/" + registryName.getPath() + "_base");
		return spriteGetter.apply(baseTx);
	}
	
	public TextureAtlasSprite t_top()
	{
		if(topTx == null)
			topTx = new ResourceLocation(registryName.getNamespace(), "block/" + registryName.getPath() + "_top");
		return spriteGetter.apply(topTx);
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
}