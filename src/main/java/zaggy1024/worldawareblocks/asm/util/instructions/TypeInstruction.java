package zaggy1024.worldawareblocks.asm.util.instructions;

import org.objectweb.asm.MethodVisitor;

public class TypeInstruction extends OpcodeInstruction
{
	public final String type;
	
	public TypeInstruction(int opcode, String type)
	{
		super(opcode);
		this.type = type;
	}
	
	@Override
	public void visit(MethodVisitor visitor)
	{
		visitor.visitTypeInsn(opcode, type);
	}
}
