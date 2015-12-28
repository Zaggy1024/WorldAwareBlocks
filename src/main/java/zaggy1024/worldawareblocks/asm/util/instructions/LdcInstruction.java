package zaggy1024.worldawareblocks.asm.util.instructions;

import org.objectweb.asm.MethodVisitor;

public class LdcInstruction implements Instruction
{
	public final Object cst;
	
	public LdcInstruction(Object cst)
	{
		this.cst = cst;
	}
	
	@Override
	public void visit(MethodVisitor visitor)
	{
		visitor.visitLdcInsn(cst);
	}
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName() + "[cst=" + cst + "]";
	}
}
