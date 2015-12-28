package zaggy1024.worldawareblocks.asm.util.instructions;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class TableSwitchInstruction implements Instruction
{
	public final int min;
	public final int max;
	public final Label dflt;
	public final Label[] labels;
	
	public TableSwitchInstruction(int min, int max, Label dflt, Label... labels)
	{
		this.min = min;
		this.max = max;
		this.dflt = dflt;
		this.labels = labels;
	}
	
	@Override
	public void visit(MethodVisitor visitor)
	{
		visitor.visitTableSwitchInsn(min, max, dflt, labels);
	}
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName();
	}
}
