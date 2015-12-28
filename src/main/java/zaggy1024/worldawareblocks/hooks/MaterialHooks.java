package zaggy1024.worldawareblocks.hooks;

import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

public class MaterialHooks
{
	public static Material getRealMaterial(IBlockAccess world, BlockPos pos)
	{
		return world.getBlockState(pos).getBlock().getMaterial();
	}
	
	public static Material getMaterial(IBlockAccess world, BlockPos pos)
	{
		IBlockState state = world.getBlockState(pos);
		
		if (state.getBlock() instanceof BlockFence)
		{
			int waters = 0;
			for (EnumFacing side : EnumFacing.HORIZONTALS)
			{
				BlockPos sidePos = pos.offset(side);
				
				if (!(world.getBlockState(sidePos).getBlock() instanceof BlockFence) && getMaterial(world, sidePos) == Material.water)
				{
					waters++;
					if (waters >= 2)
						return Material.water;
				}
			}
		}
		
		return getRealMaterial(world, pos);
	}
	
	public static boolean hasLevelProperty(IBlockState state)
	{
		return state.getPropertyNames().contains(BlockLiquid.LEVEL);
	}
	
	public static boolean hasLevelProperty(IBlockAccess world, BlockPos pos)
	{
		return hasLevelProperty(world.getBlockState(pos));
	}
}
