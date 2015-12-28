package zaggy1024.worldawareblocks.asm.util.instructions;

import org.objectweb.asm.MethodVisitor;

public class ZeroInstruction extends OpcodeInstruction
{
	public ZeroInstruction(int opcode)
	{
		super(opcode);
	}
	
	@Override
	public void visit(MethodVisitor visitor)
	{
		visitor.visitInsn(opcode);
	}
}
