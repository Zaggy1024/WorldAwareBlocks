package zaggy1024.worldawareblocks.asm.util.instructions;

import org.objectweb.asm.MethodVisitor;

public class VarInstruction extends OpcodeInstruction
{
	public final int var;
	
	public VarInstruction(int opcode, int var)
	{
		super(opcode);
		this.var = var;
	}
	
	@Override
	public void visit(MethodVisitor visitor)
	{
		visitor.visitVarInsn(opcode, var);
	}
	
	@Override
	public String getStringBase()
	{
		return super.getStringBase() + ",var=" + var;
	}
}
