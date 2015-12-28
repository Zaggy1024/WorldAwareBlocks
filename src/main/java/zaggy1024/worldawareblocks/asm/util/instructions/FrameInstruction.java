package zaggy1024.worldawareblocks.asm.util.instructions;

import org.objectweb.asm.MethodVisitor;

public class FrameInstruction implements Instruction
{
	public final int type;
	public final int nLocal;
	public final Object[] local;
	public final int nStack;
	public final Object[] stack;
	
	public FrameInstruction(int type, int nLocal, Object[] local, int nStack, Object[] stack)
	{
		this.type = type;
		this.nLocal = nLocal;
		this.local = local;
		this.nStack = nStack;
		this.stack = stack;
	}
	
	@Override
	public void visit(MethodVisitor visitor)
	{
		visitor.visitFrame(type, nLocal, local, nStack, stack);
	}
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName();
	}
}
