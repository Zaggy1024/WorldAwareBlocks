package zaggy1024.worldawareblocks.asm;

import org.apache.commons.lang3.tuple.Pair;
import org.objectweb.asm.*;

import zaggy1024.worldawareblocks.asm.Members.IBlockAccess;
import zaggy1024.worldawareblocks.asm.util.*;
import zaggy1024.worldawareblocks.asm.util.LoggingAdapter.*;
import zaggy1024.worldawareblocks.asm.util.instructions.*;

import static zaggy1024.worldawareblocks.asm.Members.*;

import java.util.*;

public class MaterialTransformer extends ClassVisitor implements Opcodes
{
	public Map<String, Integer> injections = new HashMap<String, Integer>();
	public String currentInjection = null;
	
	private final String className;
	
	public MaterialTransformer(String className, ClassVisitor cv)
	{
		super(ASM5, cv);
		
		this.className = className;
	}
	
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
	{
		if (!Block.mGetBlockMaterial.matches(name, desc))
		{
			return new MethodAdapter(api, name, desc,
					new LoggingAdapter(api, className, access, name, desc,
							super.visitMethod(access, name, desc, signature, exceptions)));
		}
		
		return super.visitMethod(access, name, desc, signature, exceptions);
	}
	
	private static enum CurrentFix
	{
		NONE,
		LIQUID_CHECK, LIQUID_CHECK_NOT, LIQUID_CHECK_FLUID_REND,
		REAL_MATERIAL;
	}
	
	private class MethodAdapter extends MethodVisitor
	{
		final LoggingAdapter log;
		final CurrentFix fix;
		
		final String name;
		final String desc;
		
		String logName;
		
		protected MethodAdapter(int api, String name, String desc, LoggingAdapter log)
		{
			super(api, log);
			
			this.name = name;
			this.desc = desc;
			
			this.log = log;
			
			CurrentFix fix = CurrentFix.NONE;
			
			String classDesc = descName(className);
			
			if (classDesc.equals(World.name) && World.mHandleMaterialAcceleration.matches(name, desc))
			{
				//fix = CurrentFix.LIQUID_CHECK;
				fix = CurrentFix.REAL_MATERIAL;
			}
			else if (classDesc.equals(BlockFluidRenderer.name) && BlockFluidRenderer.mGetFluidHeight.matches(name, desc))
			{
				//fix = CurrentFix.LIQUID_CHECK_FLUID_REND;
				fix = CurrentFix.REAL_MATERIAL;
			}
			else if (classDesc.equals(BlockLiquid.name))
			{
				if (BlockLiquid.mGetLevel.matches(name, desc)
						|| BlockLiquid.mGetFlowVector.matches(name, desc))
					fix = CurrentFix.REAL_MATERIAL;
			}
			else if (classDesc.equals(BlockDynamicLiquid.name))
			{
				/*if (BlockDynamicLiquid.mGetPossibleFlowDirections.matches(name, desc))
					fix = CurrentFix.LIQUID_CHECK;
				else if (BlockDynamicLiquid.mIsBlocked.matches(name, desc))
					fix = CurrentFix.REAL_MATERIAL;*/
				if (BlockDynamicLiquid.mGetPossibleFlowDirections.matches(name, desc)
						|| BlockDynamicLiquid.mIsBlocked.matches(name, desc))
					fix = CurrentFix.REAL_MATERIAL;
			}
			
			this.fix = fix;
			
			currentInjection = getLogName();
		}
		
		private String getLogName()
		{
			if (logName == null)
				logName = friendlyClassName(className) + "." + name;// + desc;
			
			return logName;
		}
		
		public void injected(Map<String, Integer> map)
		{
			Integer value = map.get(getLogName());
			map.put(getLogName(), value == null ? 1 : value + 1);
		}
		
		private List<Instruction> prepareInjection()
		{
			// Get origin of Block instance on stack.
			int blockOrigin = log.getStackOriginInsn(log.stack.size() - 1);
			blockOrigin = log.getVariableOriginInsn(blockOrigin);
			
			if (blockOrigin == -1)
				return null;
			
			Pair<Instruction, List<StackChange>> blockInsn = log.instructions.get(blockOrigin);
			
			// Get origin of IBlockState instance on stack.
			StackChange change = log.getStackChange(blockInsn, ChangeType.POP);
			
			// If change == null, that means that the Block instance didn't come from inside this method,
			// so no use trying to find the getBlockState call.
			if (change == null)
				return null;
			
			int blockStateOrigin = log.getStackOriginInsn(change.slot, blockOrigin);
			blockStateOrigin = log.getVariableOriginInsn(blockStateOrigin);
			
			if (blockStateOrigin == -1)
				return null;
			
			Pair<Instruction, List<StackChange>> blockStateInsn = log.instructions.get(blockStateOrigin);
			
			// Check if the instruction is IBlockAccess.getBlockState(BlockPos pos)
			if (!IBlockAccess.mGetBlockState.matches(blockStateInsn.getLeft()))
				return null;
			
			// Get the origin of the IBlockAccess instance on the stack.
			StackChange worldPop = log.getStackChange(blockStateInsn, ChangeType.POP);
			int worldOrigin = log.getStackOriginInsn(worldPop.slot, blockStateOrigin);
			
			if (worldOrigin == -1)
				return null;
			
			// Get the origin of the BlockPos instance on the stack.
			StackChange blockPosPop = log.getStackChange(blockStateInsn, ChangeType.POP, worldPop.slot);
			int blockPosOrigin = log.getStackOriginInsn(blockPosPop.slot, blockStateOrigin);
			
			if (blockPosOrigin == -1)
				return null;
			
			// Gather instructions to reproduce the IBlockAccess.
			List<Instruction> insns = log.gatherInstructions(worldOrigin);
			
			if (insns.isEmpty())
				throw new RuntimeException("Failed to get any instructions to reproduce an IBlockAccess instance.");
			
			// Gather instructions to reproduce the BlockPos.
			List<Instruction> blockPosInsns = log.gatherInstructions(blockPosOrigin);
			
			if (blockPosInsns.isEmpty())
				throw new RuntimeException("Failed to get any instructions to reproduce a BlockPos instance.");
			
			// Combine the instructions and return them.
			insns.addAll(blockPosInsns);
			return insns;
		}
		
		private void inject(List<Instruction> stackFillers)
		{
			log.visitInsn(POP);
			
			for (Instruction insn : stackFillers)
				insn.visit(log);
			
			String method;
			
			if (fix == CurrentFix.REAL_MATERIAL)
				method = "getRealMaterial";
			else
				method = "getMaterial";
			
			log.visitMethodInsn(INVOKESTATIC,
					"zaggy1024/worldawareblocks/hooks/MaterialHooks",
					method,
					"(L" + IBlockAccess.name + ";L" + BlockPos.name + ";)L" + Material.name + ";",
					false);
			
			injected(injections);
		}
		
		@Override
		public void visitFieldInsn(int opcode, String owner, String name, String desc)
		{
			if (opcode == GETFIELD && Block.fBlockMaterial.equals(name))
			{
				List<Instruction> insns = prepareInjection();
				
				if (insns != null)
				{
					inject(insns);
					return;
				}
			}
			
			super.visitFieldInsn(opcode, owner, name, desc);
		}
		
		@Override
		public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf)
		{
			if (Block.mGetBlockMaterial.matches(name, desc))
			{
				List<Instruction> insns = prepareInjection();
				
				if (insns != null)
				{
					inject(insns);
					return;
				}
			}
			
			super.visitMethodInsn(opcode, owner, name, desc, itf);
		}
		
		private boolean prepareWorldBlockPos()
		{
			int materialOrigin = log.getStackOriginInsn(log.stack.size() - 2);
			materialOrigin = log.getVariableOriginInsn(materialOrigin);
			
			if (materialOrigin == -1)
				return false;
			
			Pair<Instruction, List<StackChange>> materialInsn = log.instructions.get(materialOrigin);
			
			int worldSlot = log.getStackChange(materialInsn, ChangeType.POP).slot;
			int worldOrigin = log.getStackOriginInsn(worldSlot, materialOrigin);
			int blockPosSlot = log.getStackChange(materialInsn, ChangeType.POP, worldSlot).slot;
			int blockPosOrigin = log.getStackOriginInsn(blockPosSlot, materialOrigin);
			
			if (blockPosOrigin == -1)
				return false;
			
			List<Instruction> insns = log.gatherInstructions(worldOrigin);
			insns.addAll(log.gatherInstructions(blockPosOrigin));
			
			if (fix == CurrentFix.LIQUID_CHECK_FLUID_REND)
			{
				Instruction upCheck = insns.get(insns.size() - 1);
				
				if (BlockPos.mUp.matches(upCheck) && ((MethodInstruction) upCheck).owner.equals(BlockPos.name))
					return false;
			}
			
			log.visitInsn(POP);
			log.visitInsn(POP);
			
			for (Instruction insn : insns)
				insn.visit(log);
			return true;
		}
		
		@Override
		public void visitJumpInsn(int opcode, Label label)
		{
			//Two Materials on the stack.
			switch (fix)
			{
			case LIQUID_CHECK:
			case LIQUID_CHECK_NOT:
			case LIQUID_CHECK_FLUID_REND:
				switch (opcode)
				{
				case IF_ACMPEQ:
				case IF_ACMPNE:
					if (Collections.frequency(log.stack, Material.name) == 2
						&& prepareWorldBlockPos())
					{
						log.visitMethodInsn(INVOKESTATIC,
								"zaggy1024/worldawareblocks/hooks/MaterialHooks",
								"hasLevelProperty",
								"(L" + IBlockAccess.name + ";L" + BlockPos.name + ";)Z",
								false);
						
						switch (fix)
						{
						case LIQUID_CHECK_NOT:
						case LIQUID_CHECK_FLUID_REND:
							log.visitJumpInsn(IFNE, label);
							break;
						default:
							log.visitJumpInsn(IFEQ, label);
							break;
						}
						return;
					}
				}
				
				break;
			default:
				break;
			}
			
			super.visitJumpInsn(opcode, label);
		}
	}
}
