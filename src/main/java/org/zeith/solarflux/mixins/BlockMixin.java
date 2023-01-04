package org.zeith.solarflux.mixins;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zeith.hammerlib.util.java.Cast;

@Mixin(Block.class)
public class BlockMixin
{
	@Inject(
			method = "toString",
			remap = false,
			at = @At("HEAD"),
			cancellable = true
	)
	public void toStringSFR(CallbackInfoReturnable<String> cir)
	{
		cir.setReturnValue(getClass().getName() + "{" + ForgeRegistries.BLOCKS.getKey(Cast.cast(this)) + "}");
	}
}
