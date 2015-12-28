package zaggy1024.worldawareblocks.asm.util.instructions;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MethodInstruction extends MemberInstruction
{
	public final boolean itf;
	
	public MethodInstruction(int opcode, String owner, String name, String desc, boolean itf)
	{
		super(opcode, owner, name, desc);
		this.itf = itf;
	}
	
	public MethodInstruction(int opcode, String owner, String name, String desc)
	{
		this(opcode, owner, name, desc, opcode == Opcodes.INVOKEINTERFACE);
	}
	
	@Override
	public void visit(MethodVisitor visitor)
	{
		visitor.visitMethodInsn(opcode, owner, name, desc, itf);
	}
}
