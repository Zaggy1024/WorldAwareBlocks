package zaggy1024.worldawareblocks.asm.util.instructions;

import org.objectweb.asm.MethodVisitor;

public class IntInstruction extends OpcodeInstruction
{
	public final int operand;
	
	public IntInstruction(int opcode, int operand)
	{
		super(opcode);
		this.operand = operand;
	}
	
	@Override
	public void visit(MethodVisitor visitor)
	{
		visitor.visitIntInsn(opcode, operand);
	}
}
