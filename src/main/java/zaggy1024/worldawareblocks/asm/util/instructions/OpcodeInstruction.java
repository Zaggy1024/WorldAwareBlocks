package zaggy1024.worldawareblocks.asm.util.instructions;

import org.objectweb.asm.util.Printer;

public abstract class OpcodeInstruction implements Instruction
{
	public final int opcode;
	
	public OpcodeInstruction(int opcode)
	{
		this.opcode = opcode;
	}
	
	protected String getStringBase()
	{
		return getClass().getSimpleName() + "[opcode=" + Printer.OPCODES[opcode];
	}
	
	@Override
	public String toString()
	{
		return getStringBase() + "]";
	}
}
