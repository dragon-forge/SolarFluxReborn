package org.zeith.solarflux.mixins;

import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.*;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.solarflux.api.*;

@Implements({
		@Interface(iface = IFurnaceBlockEntity.class, prefix = "ifbe$")
})
@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin
		extends BaseContainerBlockEntity
		implements WorldlyContainer, RecipeCraftingHolder, StackedContentsCompatible
{
	@Shadow
	int litTimeRemaining;
	
	@Shadow
	int litTotalTime;
	
	@Shadow protected NonNullList<ItemStack> items;
	
	@Shadow
	@Final
	private RecipeManager.CachedCheck<SingleRecipeInput, ? extends AbstractCookingRecipe> quickCheck;
	
	protected AbstractFurnaceBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
	}
	
	public boolean ifbe$activateWithSolarPanel(ServerLevel level, ISolarPanelTile solar)
	{
		BlockPos pos = getBlockPos();
		
		SingleRecipeInput singlerecipeinput = new SingleRecipeInput(getItem(0));
		RecipeHolder<? extends AbstractCookingRecipe> irecipe =
				quickCheck.getRecipeFor(singlerecipeinput, level)
						  .orElse(null);
		
		SingleRecipeInput input = new SingleRecipeInput(items.get(0));
		
		if(litTimeRemaining <= 1 && irecipe != null && AbstractFurnaceBlockEntity.canBurn(level.registryAccess(), irecipe, input, items, getMaxStackSize()) && solar.energy() >= 1000)
		{
			litTimeRemaining = litTotalTime = 201;
			solar.energy(solar.energy() - 1000L);
			
			BlockState state = level.getBlockState(pos);
			if(state.hasProperty(AbstractFurnaceBlock.LIT) && !state.getValue(AbstractFurnaceBlock.LIT))
			{
				state = state.setValue(AbstractFurnaceBlock.LIT, true);
				
				level.setBlock(pos, state, 3);
				
				level.blockEntityChanged(pos);
				if(!state.isAir())
					level.updateNeighbourForOutputSignal(pos, state.getBlock());
			}
			
			setChanged();
			return true;
		}
		
		return false;
	}
	
	public Direction ifbe$getSideForSolarPanel()
	{
		return Direction.UP;
	}
}