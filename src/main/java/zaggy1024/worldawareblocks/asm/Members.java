package zaggy1024.worldawareblocks.asm;

import zaggy1024.worldawareblocks.asm.util.instructions.*;

public class Members
{
	@SuppressWarnings("unused")
	private static boolean srg = false;
	
	public static class IBlockAccess
	{
		public static final String name = "net/minecraft/world/IBlockAccess";
		
		public static Method mGetBlockState = new Method("getBlockState",
				"(Lnet/minecraft/util/BlockPos;)Lnet/minecraft/block/state/IBlockState;");
	}
	
	public static class World
	{
		public static final String name = "net/minecraft/world/World";
		
		public static Method mHandleMaterialAcceleration = new Method("handleMaterialAcceleration",
				"(Lnet/minecraft/util/AxisAlignedBB;Lnet/minecraft/block/material/Material;Lnet/minecraft/entity/Entity;)Z");
	}
	
	public static class BlockPos
	{
		public static final String name = "net/minecraft/util/BlockPos";
		
		public static Method mUp = new Method("up", "()Lnet/minecraft/util/BlockPos;");
	}
	
	public static class IBlockState
	{
		public static final String name = "net/minecraft/block/state/IBlockState";
	}
	
	public static class Block
	{
		public static final String name = "net/minecraft/block/Block";
		
		public static String fBlockMaterial;
		
		public static Method mGetBlockMaterial = new Method("getMaterial",
				"()Lnet/minecraft/block/material/Material;");
	}
	
	public static class Material
	{
		public static final String name = "net/minecraft/block/material/Material";
	}
	
	public static class BlockFluidRenderer
	{
		public static final String name = "net/minecraft/client/renderer/BlockFluidRenderer";
		
		public static Method mGetFluidHeight = new Method("getFluidHeight",
				"(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/BlockPos;Lnet/minecraft/block/material/Material;)F");
	}
	
	public static class BlockLiquid
	{
		public static final String name = "net/minecraft/block/BlockLiquid";
		
		public static Method mGetLevel = new Method("getLevel",
				"(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/BlockPos;)I");
		
		public static Method mGetFlowVector = new Method("getFlowVector",
				"(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/BlockPos;)Lnet/minecraft/util/Vec3;");
	}
	
	public static class BlockDynamicLiquid
	{
		public static final String name = "net/minecraft/block/BlockDynamicLiquid";
		
		public static Method mGetPossibleFlowDirections = new Method("getPossibleFlowDirections",
				"(Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;)Ljava/util/Set;");
		
		public static Method mIsBlocked = new Method("isBlocked",
				"(Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;Lnet/minecraft/block/state/IBlockState;)Z");
	}
	
	public static void init(boolean srg)
	{
		Members.srg = srg;
		
		Block.fBlockMaterial = "blockMaterial";
		
		if (srg)
		{
			IBlockAccess.mGetBlockState.name = "func_180495_p";
			
			World.mHandleMaterialAcceleration.name = "func_72918_a";
			
			BlockPos.mUp.name = "func_177984_a";
			
			Block.fBlockMaterial = "field_149764_J";
			Block.mGetBlockMaterial.name = "func_149688_o";
			
			BlockFluidRenderer.mGetFluidHeight.name = "func_178269_a";
			
			BlockLiquid.mGetLevel.name = "func_176362_e";
			BlockLiquid.mGetFlowVector.name = "func_180687_h";
			
			BlockDynamicLiquid.mGetPossibleFlowDirections.name = "func_176376_e";
			BlockDynamicLiquid.mIsBlocked.name = "func_176372_g";
		}
	}
	
	public static String className(String desc)
	{
		return desc.replace("/", ".");
	}
	
	public static String descName(String clazz)
	{
		return clazz.replace(".", "/");
	}
	
	public static String friendlyClassName(String clazz)
	{
		return clazz.substring(clazz.lastIndexOf('.') + 1);
	}
	
	public static class Method
	{
		public String name;
		public String desc;
		
		private Method(String name, String desc)
		{
			this.name = name;
			this.desc = desc;
		}
		
		public boolean matches(String name, String desc)
		{
			return this.name.equals(name) && this.desc.equals(desc);
		}
		
		public boolean matches(Instruction insn)
		{
			if (insn instanceof MethodInstruction)
			{
				MethodInstruction mInsn = (MethodInstruction) insn;
				return matches(mInsn.name, mInsn.desc);
			}
			
			return false;
		}
	}
}
