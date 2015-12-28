package zaggy1024.worldawareblocks.asm.util.instructions;

import org.objectweb.asm.MethodVisitor;

public interface Instruction
{
	public void visit(MethodVisitor visitor);
}
