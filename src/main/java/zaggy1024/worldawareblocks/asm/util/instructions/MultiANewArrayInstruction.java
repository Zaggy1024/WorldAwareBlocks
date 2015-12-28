package zaggy1024.worldawareblocks.asm.util.instructions;

import org.objectweb.asm.MethodVisitor;

public class MultiANewArrayInstruction implements Instruction
{
	public final String desc;
	public final int dims;
	
	public MultiANewArrayInstruction(String desc, int dims)
	{
		this.desc = desc;
		this.dims = dims;
	}
	
	@Override
	public void visit(MethodVisitor visitor)
	{
		visitor.visitMultiANewArrayInsn(desc, dims);
	}
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName();
	}
}
