package zaggy1024.worldawareblocks.asm.util.instructions;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class JumpInstruction extends OpcodeInstruction
{
	public final Label label;
	
	public JumpInstruction(int opcode, Label label)
	{
		super(opcode);
		this.label = label;
	}
	
	@Override
	public void visit(MethodVisitor visitor)
	{
		visitor.visitJumpInsn(opcode, label);
	}
}
