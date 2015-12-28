package zaggy1024.worldawareblocks.asm.util.instructions;

import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;

public class InvokeDynamicInstruction implements Instruction
{
	public final String name;
	public final String desc;
	public final Handle bsm;
	public final Object[] bsmArgs;
	
	public InvokeDynamicInstruction(String name, String desc, Handle bsm, Object... bsmArgs)
	{
		this.name = name;
		this.desc = desc;
		this.bsm = bsm;
		this.bsmArgs = bsmArgs;
	}
	
	@Override
	public void visit(MethodVisitor visitor)
	{
		visitor.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
	}
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName();
	}
}
