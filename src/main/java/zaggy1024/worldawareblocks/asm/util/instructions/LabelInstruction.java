package zaggy1024.worldawareblocks.asm.util.instructions;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class LabelInstruction implements Instruction
{
	public final Label label;
	
	public LabelInstruction(Label label)
	{
		this.label = label;
	}
	
	@Override
	public void visit(MethodVisitor visitor)
	{
		visitor.visitLabel(label);
	}
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName();
	}
}
