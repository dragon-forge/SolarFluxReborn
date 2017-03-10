package com.mrdimka.solarfluxreborn.client;

import com.mrdimka.solarfluxreborn.reference.Reference;

import net.minecraft.util.ResourceLocation;

public final class DestroyStage
{
	private static final ResourceLocation[] DESTROY_STAGES = new ResourceLocation[]
	{
		new ResourceLocation(Reference.MOD_ID, "textures/models/destroy_stage_0.png"),
		new ResourceLocation(Reference.MOD_ID, "textures/models/destroy_stage_1.png"),
		new ResourceLocation(Reference.MOD_ID, "textures/models/destroy_stage_2.png"),
		new ResourceLocation(Reference.MOD_ID, "textures/models/destroy_stage_3.png"),
		new ResourceLocation(Reference.MOD_ID, "textures/models/destroy_stage_4.png"),
		new ResourceLocation(Reference.MOD_ID, "textures/models/destroy_stage_5.png"),
		new ResourceLocation(Reference.MOD_ID, "textures/models/destroy_stage_6.png"),
		new ResourceLocation(Reference.MOD_ID, "textures/models/destroy_stage_7.png"),
		new ResourceLocation(Reference.MOD_ID, "textures/models/destroy_stage_8.png"),
		new ResourceLocation(Reference.MOD_ID, "textures/models/destroy_stage_9.png")
	};
	
	public static ResourceLocation getByProgress(float progress)
	{
		return DESTROY_STAGES[(int) Math.round(progress * (DESTROY_STAGES.length - 1))];
	}
}