package zaggy1024.worldawareblocks.asm;

import java.util.Map;

import org.objectweb.asm.*;

import net.minecraft.launchwrapper.IClassTransformer;

public class Transformer implements IClassTransformer
{
	private int total(Map<String, Integer> map)
	{
		int out = 0;
		for (int count : map.values())
			out += count;
		return out;
	}
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass)
	{
		if (basicClass != null)// && name.endsWith("BlockFluidRenderer"))
		{
			ClassReader r = new ClassReader(basicClass);
			ClassWriter w = new ClassWriter(r, ClassWriter.COMPUTE_FRAMES);
			
			MaterialTransformer mt = new MaterialTransformer(name, w);
			
			try
			{
				r.accept(mt, ClassReader.EXPAND_FRAMES);
				if (!mt.injections.isEmpty())
				{
					System.out.println("WorldAwareBlocks"
							+ " replaced " + total(mt.injections) + " dumb material accesses: " + mt.injections + ".");
					return w.toByteArray();
				}
			}
			catch (Exception e)
			{
				if (mt.injections.isEmpty())
					System.out.println("WorldAwareBlocks's transformer caught an exception.");
				else
					System.out.println("WorldAwareBlocks failed to patch " + mt.currentInjection + ". Error is below.");
				e.printStackTrace(System.out);
			}
		}
		
		return basicClass;
	}
}
