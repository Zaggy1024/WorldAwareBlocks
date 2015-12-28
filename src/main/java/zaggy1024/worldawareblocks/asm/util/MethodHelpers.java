package zaggy1024.worldawareblocks.asm.util;

import java.util.Set;

import org.objectweb.asm.*;

import com.google.common.collect.ImmutableSet;

public class MethodHelpers
{
	public static int getArgumentIndex(String desc, int start, String... types)
	{
		Set<String> typeSet = ImmutableSet.copyOf(types);
		
		Type type = Type.getMethodType(desc);
		int i = 0;
		
		for (Type arg : type.getArgumentTypes())
		{
			if (arg.getSort() == Type.OBJECT)
			{
				String name = arg.getInternalName();
				
				if (i >= start && typeSet.contains(name))
				{
					return i;
				}
			}
			
			i++;
		}
		
		return -1;
	}
	
	public static int getArgumentInStack(String desc, String... types)
	{
		return getArgumentIndex(desc, 0, types);
	}
}
