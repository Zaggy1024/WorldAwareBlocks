package zaggy1024.worldawareblocks.asm.util.instructions;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class LookupSwitchInstruction implements Instruction
{
	public final Label dflt;
	public final int[] keys;
	public final Label[] labels;
	
	public LookupSwitchInstruction(Label dflt, int[] keys, Label[] labels)
	{
		this.dflt = dflt;
		this.keys = keys;
		this.labels = labels;
	}
	
	@Override
	public void visit(MethodVisitor visitor)
	{
		visitor.visitLookupSwitchInsn(dflt, keys, labels);
	}
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName();
	}
}
