package zaggy1024.worldawareblocks.asm.util;

import java.util.*;

import org.apache.commons.lang3.tuple.Pair;
import org.objectweb.asm.*;
import org.objectweb.asm.util.Printer;

import zaggy1024.worldawareblocks.asm.util.instructions.*;

public class LoggingAdapter extends NonPrivateAnalyzerAdapter implements Opcodes
{
	public static enum ChangeType
	{
		PUSH, POP;
		
		@Override
		public String toString()
		{
			return name();
		}
	}
	
	public static class StackChange
	{
		/**
		 * The slot affected by this change.
		 */
		public final int slot;
		/**
		 * Possible values: LoggingAdapter.PUSH, LoggingAdapter.POP.
		 */
		public final ChangeType change;
		public final Object type;
		
		public StackChange(int slot, ChangeType change, Object type)
		{
			this.slot = slot;
			this.change = change;
			this.type = type;
		}
		
		@Override
		public String toString()
		{
			return "StackChange[slot=" + slot + ",change=" + change + ",type='" + getTypeName(type) + "']";
		}
	}
	
	public final List<Pair<Instruction, List<StackChange>>> instructions = new ArrayList<Pair<Instruction, List<StackChange>>>();
	
	public LoggingAdapter(int api, String owner, int access, String name, String desc, MethodVisitor mv)
	{
		super(api, owner, access, name, desc, mv);
	}
	
	public int getStoreInsn(int var, int start)
	{
		ListIterator<Pair<Instruction, List<StackChange>>> iter = instructions.listIterator(start);
		
		while (iter.hasPrevious())
		{
			Pair<Instruction, List<StackChange>> insnData = iter.previous();
			
			if (insnData.getLeft() instanceof VarInstruction)
			{
				VarInstruction varInsn = (VarInstruction) insnData.getLeft();
				
				if (varInsn.var == var)
				{
					switch (varInsn.opcode)
					{
					case ISTORE:
					case LSTORE:
					case FSTORE:
					case DSTORE:
					case ASTORE:
						return iter.nextIndex();
					}
				}
			}
		}
		
		return -1;
	}
	
	/**
	 * Searches for the origin of the variable loaded at this instruction index.
	 * @param start	The instruction (exclusive) to start searching from.
	 * @param def The value to return if finding the stack pushing instruction fails.
	 */
	public int getVariableOriginInsn(int start, int def)
	{
		if (start != -1)
		{
			Pair<Instruction, List<StackChange>> insn = instructions.get(start);
			
			if (insn.getLeft() instanceof VarInstruction)
			{
				VarInstruction varInsn = (VarInstruction) insn.getLeft();
				
				switch (varInsn.opcode)
				{
				case ILOAD:
				case LLOAD:
				case FLOAD:
				case DLOAD:
				case ALOAD:
					int storeInsnIndex = getStoreInsn(varInsn.var, start);
					
					if (storeInsnIndex >= 0)
						return getStackOriginInsn(getStackChange(instructions.get(storeInsnIndex), ChangeType.POP).slot, storeInsnIndex);
				}
			}
		}
		
		return def;
	}
	
	/**
	 * Searches for the origin of the variable loaded at this instruction index.
	 * @param start	The instruction (exclusive) to start searching from.
	 * @return The index of the instruction if found, otherwise the {@code start}.
	 */
	public int getVariableOriginInsn(int start)
	{
		return getVariableOriginInsn(start, start);
	}
	
	/**
	 * @param slot The stack slot to look for the origin of.
	 * @param start The instruction to start the search from (exclusive).
	 * @return The index of the instruction that put the value in the slot of the stack.
	 */
	public int getStackOriginInsn(int slot, int start)
	{
		ListIterator<Pair<Instruction, List<StackChange>>> iter = instructions.listIterator(start);
		
		while (iter.hasPrevious())
		{
			Pair<Instruction, List<StackChange>> checkInsn = iter.previous();
			
			for (StackChange change : checkInsn.getRight())
			{
				if (change.change == ChangeType.PUSH && change.slot == slot)
				{
					return iter.nextIndex();
				}
			}
		}
		
		return -1;
	}
	
	public int getStackOriginInsn(int slot)
	{
		return getStackOriginInsn(slot, instructions.size());
	}
	
	public StackChange getStackChange(List<StackChange> changes, ChangeType changeType, int start)
	{
		ListIterator<StackChange> iter = changes.listIterator(changes.size());
		boolean foundStart = start == -1;
		
		while (iter.hasPrevious())
		{
			StackChange change = iter.previous();
			
			if (change.change == changeType)
			{
				if (!foundStart)
				{
					if (change.slot == start)
						foundStart = true;
				}
				else 
				{
					return change;
				}
			}
		}
		
		return null;
	}
	
	public StackChange getStackChange(List<StackChange> changes, ChangeType changeType)
	{
		return getStackChange(changes, changeType, -1);
	}
	
	public StackChange getStackChange(Pair<Instruction, List<StackChange>> insn, ChangeType changeType, int start)
	{
		return getStackChange(insn.getRight(), changeType, start);
	}
	
	public StackChange getStackChange(Pair<Instruction, List<StackChange>> insn, ChangeType changeType)
	{
		return getStackChange(insn.getRight(), changeType);
	}
	
	/*private List<Instruction> gatherAffectingInstructions(int start, int var)
	{
		ListIterator<Pair<Instruction, List<StackChange>>> iter = instructions.listIterator(start + 1);
		
		List<Instruction> instructions = new ArrayList<Instruction>();
		
		while (iter.hasNext())
		{
			Pair<Instruction, List<StackChange>> checkInsn = iter.next();
			
			if (checkInsn.getLeft() instanceof VarInstruction)
			{
				VarInstruction insn = (VarInstruction) checkInsn.getLeft();
				
				if (insn.var == var)
				{
					switch (insn.opcode)
					{
					case ISTORE:
					case LSTORE:
					case FSTORE:
					case DSTORE:
					case ASTORE:
						instructions.addAll(gatherInstructions(iter.nextIndex()));
					}
				}
			}
		}
		
		return instructions;
	}*/
	
	private List<Instruction> gatherAffectingInstructions(int start, int slot)
	{
		ListIterator<Pair<Instruction, List<StackChange>>> iter = instructions.listIterator(start + 1);
		
		while (iter.hasNext())
		{
			Pair<Instruction, List<StackChange>> checkInsn = iter.next();
			
			ListIterator<StackChange> changeIter = checkInsn.getRight().listIterator(checkInsn.getRight().size());
			
			while (changeIter.hasPrevious())
			{
				StackChange change = changeIter.previous();
				
				if (change.change == ChangeType.POP && change.slot == slot)
				{	// Change consumes the slot we're looking for, so gather all instructions contributing to the checked instruction.
					return gatherInstructions(iter.previousIndex(), change);
				}
			}
		}
		
		return new ArrayList<Instruction>();
	}
	
	/**
	 * @param start The position of the instruction to get all dependency instructions for.
	 * @param ignoreChange A change to ignore in the search.
	 * @return All the required instructions necessary for the instruction at the provided position.
	 */
	private List<Instruction> gatherInstructions(int start, StackChange ignoreChange)
	{
		ListIterator<Pair<Instruction, List<StackChange>>> iter = instructions.listIterator(start + 1);
		boolean first = true;
		
		List<Instruction> insnsOut = new ArrayList<Instruction>();
		Set<Integer> pops = new HashSet<Integer>();	// TODO: Change to BitSet!
		
		while (iter.hasPrevious())
		{
			Pair<Instruction, List<StackChange>> checkInsn = iter.previous();
			
			if (first)
			{
				first = false;
				insnsOut.add(checkInsn.getLeft());
			}
			
			ListIterator<StackChange> changeIter = checkInsn.getRight().listIterator(checkInsn.getRight().size());
			
			while (changeIter.hasPrevious())
			{
				StackChange change = changeIter.previous();
				
				if (change != ignoreChange && !change.type.equals(TOP))
				{
					switch (change.change)
					{
					case POP:	// If a slot was popped, we need to look for its origin.
						pops.add(change.slot);
						break;
					case PUSH:
						if (pops.remove(change.slot))
							insnsOut.add(checkInsn.getLeft());
						//instructions.addAll(instructions.size() - 1, gatherAffectingInstructions(iter.nextIndex(), ));
						break;
					}
				}
			}
			
			if (checkInsn.getLeft() instanceof ZeroInstruction)
			{
				ZeroInstruction insn = (ZeroInstruction) checkInsn.getLeft();
				StackChange dupPush = null;
				int pushes = 0;
				
				switch (insn.opcode)
				{
				case Opcodes.DUP:
				case Opcodes.DUP_X1:
				case Opcodes.DUP_X2:
					pushes = 1;
					break;
				case Opcodes.DUP2:
				case Opcodes.DUP2_X1:
				case Opcodes.DUP2_X2:
					pushes = 2;
					break;
				}
				
				for (int i = 0; i < pushes; i++)
				{
					dupPush = getStackChange(checkInsn.getRight(), ChangeType.PUSH,
							dupPush == null ? -1 : dupPush.slot);
					if (dupPush != ignoreChange)
					{
						List<Instruction> dupInsns = gatherAffectingInstructions(iter.nextIndex(), dupPush.slot);
						Collections.reverse(dupInsns);
						insnsOut.addAll(insnsOut.size() - 1, dupInsns);
					}
					// If dup, add the added slot to the slots we're searching for.
				}
			}
			
			if (pops.isEmpty())
			{
				break;
			}
		}
		
		Collections.reverse(insnsOut);
		return insnsOut;
	}
	
	/**
	 * @param start The position of the instruction to get all dependency instructions for.
	 * @return All the required instructions necessary for the instruction at the provided position.
	 */
	public List<Instruction> gatherInstructions(int start)
	{
		return gatherInstructions(start, null);
	}
	
	private Instruction instruction = null;
	private List<StackChange> changes = new ArrayList<StackChange>();
	
	private void log()
	{
		if (instruction == null)
			throw new RuntimeException("Instruction was not created to log.");
		
		instructions.add(Pair.of(instruction, changes));
		changes = new ArrayList<StackChange>();
		instruction = null;
	}
	
	private void initializeLogTypes(List<StackChange> changes, Object oldType, Object newType)
	{
		ListIterator<StackChange> changesIter = changes.listIterator();
		
		while (changesIter.hasNext())
		{
			StackChange change = changesIter.next();
			
			if (change.type == oldType)
				changesIter.set(new StackChange(change.slot, change.change, newType));
		}
	}
	
	@Override
	protected void initializeType(Object oldType, Object newType)
	{
		super.initializeType(oldType, newType);
		
		ListIterator<Pair<Instruction, List<StackChange>>> iter = instructions.listIterator();
		
		while (iter.hasNext())
		{
			initializeLogTypes(iter.next().getRight(), oldType, newType);
		}
		
		initializeLogTypes(changes, oldType, newType);
	}
	
	// Stack changes
	@Override
	protected void push(int i, int size, Object type)
	{
		changes.add(new StackChange(i, ChangeType.PUSH, type));
		super.push(i, size, type);
	}
	
	@Override
	protected Object pop(int i, int size)
	{
		Object removed = super.pop(i, size);
		changes.add(new StackChange(i, ChangeType.POP, removed));
		return removed;
	}
	
	// Instructions
	@Override
	public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack)
	{
		instruction = new FrameInstruction(type, nLocal, local, nStack, stack);
		super.visitFrame(type, nLocal, local, nStack, stack);
		log();
	}
	
	@Override
	public void visitInsn(int opcode)
	{
		instruction = new ZeroInstruction(opcode);
		super.visitInsn(opcode);
		log();
	}
	
	@Override
	public void visitIntInsn(int opcode, int operand)
	{
		instruction = new IntInstruction(opcode, operand);
		super.visitIntInsn(opcode, operand);
		log();
	}
	
	@Override
	public void visitVarInsn(int opcode, int var)
	{
		instruction = new VarInstruction(opcode, var);
		super.visitVarInsn(opcode, var);
		log();
	}
	
	@Override
	public void visitTypeInsn(int opcode, String type)
	{
		instruction = new TypeInstruction(opcode, type);
		super.visitTypeInsn(opcode, type);
		log();
	}
	
	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String desc)
	{
		instruction = new FieldInstruction(opcode, owner, name, desc);
		super.visitFieldInsn(opcode, owner, name, desc);
		log();
	}
		
	@Deprecated
	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String desc)
	{
		instruction = new MethodInstruction(opcode, owner, name, desc);
		super.visitMethodInsn(opcode, owner, name, desc);
		log();
	}
	
	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf)
	{
		instruction = new MethodInstruction(opcode, owner, name, desc, itf);
		super.visitMethodInsn(opcode, owner, name, desc, itf);
		log();
	}
	
	@Override
	public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs)
	{
		instruction = new InvokeDynamicInstruction(name, desc, bsm, bsmArgs);
		super.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
		log();
	}
	
	@Override
	public void visitJumpInsn(int opcode, Label label)
	{
		instruction = new JumpInstruction(opcode, label);
		super.visitJumpInsn(opcode, label);
		log();
	}
	
	@Override
	public void visitLabel(Label label)
	{
		instruction = new LabelInstruction(label);
		super.visitLabel(label);
		log();
	}
	
	@Override
	public void visitLdcInsn(Object cst)
	{
		instruction = new LdcInstruction(cst);
		super.visitLdcInsn(cst);
		log();
	}
	
	@Override
	public void visitIincInsn(int var, int increment)
	{
		instruction = new IincInstruction(var, increment);
		super.visitIincInsn(var, increment);
		log();
	}
	
	@Override
	public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels)
	{
		instruction = new TableSwitchInstruction(min, max, dflt, labels);
		super.visitTableSwitchInsn(min, max, dflt, labels);
		log();
	}
	
	@Override
	public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels)
	{
		instruction = new LookupSwitchInstruction(dflt, keys, labels);
		super.visitLookupSwitchInsn(dflt, keys, labels);
		log();
	}
	
	@Override
	public void visitMultiANewArrayInsn(String desc, int dims)
	{
		instruction = new MultiANewArrayInstruction(desc, dims);
		super.visitMultiANewArrayInsn(desc, dims);
		log();
	}
	
	@Override
	public void visitMaxs(int maxStack, int maxLocals)
	{
		instruction = new MaxsInstruction(maxStack, maxLocals);
		super.visitMaxs(maxStack, maxLocals);
		log();
	}
	
	public static String getTypeName(Object type)
	{
		if (type == null)
			return "null";
		
		if (type instanceof Integer)
		{
			switch ((Integer) type)
			{
			case 0:
				return "TOP";
			case 1:
				return "INTEGER";
			case 2:
				return "FLOAT";
			case 3:
				return "DOUBLE";
			case 4:
				return "LONG";
			case 5:
				return "NULL";
			case 6:
				return "UNINITIALIZED_THIS";
			}
		}
		
		return type.toString();
	}
}
