package org.zeith.solarflux.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.*;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.solarflux.api.IFurnaceBlockEntity;
import org.zeith.solarflux.api.ISolarPanelTile;

import javax.annotation.Nullable;

@Implements({
		@Interface(iface = IFurnaceBlockEntity.class, prefix = "ifbe$")
})
@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin
		extends BaseContainerBlockEntity
		implements WorldlyContainer, RecipeCraftingHolder, StackedContentsCompatible
{
	@Shadow
	@Final
	private RecipeType<? extends AbstractCookingRecipe> recipeType;
	
	@Shadow
	int litTime;
	
	@Shadow
	int litDuration;
	
	@Shadow
	@Final
	private RecipeManager.CachedCheck<SingleRecipeInput, ? extends AbstractCookingRecipe> quickCheck;
	
	protected AbstractFurnaceBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
	}
	
	public boolean ifbe$activateWithSolarPanel(ISolarPanelTile solar)
	{
		BlockPos pos = getBlockPos();
		Level lvl = getLevel();
		
		SingleRecipeInput singlerecipeinput = new SingleRecipeInput(getItem(0));
		RecipeHolder<? extends AbstractCookingRecipe> irecipe =
				quickCheck.getRecipeFor(singlerecipeinput, lvl)
						  .orElse(null);
		
		if(litTime <= 1 && irecipe != null && SolarFlux$canSmelt(irecipe.value()) && solar.energy() >= 1000)
		{
			litTime = 201;
			litDuration = 201;
			solar.energy(solar.energy() - 1000L);
			
			BlockState state = lvl.getBlockState(pos);
			if(state.hasProperty(AbstractFurnaceBlock.LIT) && !state.getValue(AbstractFurnaceBlock.LIT))
			{
				state = state.setValue(AbstractFurnaceBlock.LIT, true);
				
				lvl.setBlock(pos, state, 3);
				
				lvl.blockEntityChanged(pos);
				if(!state.isAir())
					lvl.updateNeighbourForOutputSignal(pos, state.getBlock());
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
	
	@Unique
	private boolean SolarFlux$canSmelt(@Nullable Recipe<?> recipe)
	{
		if(getItem(0).isEmpty() || recipe == null)
			return false;
		
		ItemStack result = recipe.getResultItem(getLevel().registryAccess());
		if(result.isEmpty()) return false;
		
		ItemStack curResIt = getItem(2);
		if(curResIt.isEmpty())
			return true;
		
		if(!ItemStack.isSameItem(curResIt, result))
			return false;
		
		if(curResIt.getCount() + result.getCount() <= getMaxStackSize() && curResIt.getCount() + result.getCount() <= curResIt.getMaxStackSize())
			return true;
		
		return curResIt.getCount() + result.getCount() <= result.getMaxStackSize();
	}
}