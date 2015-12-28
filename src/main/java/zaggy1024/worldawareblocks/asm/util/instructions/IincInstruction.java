package zaggy1024.worldawareblocks.asm.util.instructions;

import org.objectweb.asm.MethodVisitor;

public class IincInstruction implements Instruction
{
	public final int var;
	public final int increment;
	
	public IincInstruction(int var, int increment)
	{
		this.var = var;
		this.increment = increment;
	}
	
	@Override
	public void visit(MethodVisitor visitor)
	{
		visitor.visitIincInsn(var, increment);
	}
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName();
	}
}
