package zaggy1024.worldawareblocks.asm.util.instructions;

public abstract class MemberInstruction extends OpcodeInstruction
{
	public final String owner;
	public final String name;
	public final String desc;
	
	public MemberInstruction(int opcode, String owner, String name, String desc)
	{
		super(opcode);
		this.owner = owner;
		this.name = name;
		this.desc = desc;
	}
	
	@Override
	public String getStringBase()
	{
		return super.getStringBase() + ",owner='" + owner + "',name='" + name + "',desc='" + desc + "'";
	}
}
