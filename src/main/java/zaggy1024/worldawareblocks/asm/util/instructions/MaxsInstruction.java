package zaggy1024.worldawareblocks.asm.util.instructions;

import org.objectweb.asm.MethodVisitor;

public class MaxsInstruction implements Instruction
{
	public final int maxStack;
	public final int maxLocals;
	
	public MaxsInstruction(int maxStack, int maxLocals)
	{
		this.maxStack = maxStack;
		this.maxLocals = maxLocals;
	}
	
	@Override
	public void visit(MethodVisitor visitor)
	{
		visitor.visitMaxs(maxStack, maxLocals);
	}
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName();
	}
}
