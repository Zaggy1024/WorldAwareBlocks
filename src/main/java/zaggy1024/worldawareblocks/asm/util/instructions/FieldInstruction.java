package zaggy1024.worldawareblocks.asm.util.instructions;

import org.objectweb.asm.MethodVisitor;

public class FieldInstruction extends MemberInstruction
{
	public FieldInstruction(int opcode, String owner, String name, String desc)
	{
		super(opcode, owner, name, desc);
	}
	
	@Override
	public void visit(MethodVisitor visitor)
	{
		visitor.visitFieldInsn(opcode, owner, name, desc);
	}
}
