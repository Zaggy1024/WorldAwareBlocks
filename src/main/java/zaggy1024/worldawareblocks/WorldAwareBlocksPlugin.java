package zaggy1024.worldawareblocks;

import java.util.Map;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import zaggy1024.worldawareblocks.asm.Members;

@IFMLLoadingPlugin.SortingIndex(1001)
@IFMLLoadingPlugin.TransformerExclusions("zaggy1024.worldawareblocks.")
@IFMLLoadingPlugin.MCVersion("1.8.8")
public class WorldAwareBlocksPlugin implements IFMLLoadingPlugin
{
	@Override
	public void injectData(Map<String, Object> data)
	{
		Members.init((Boolean) data.get("runtimeDeobfuscationEnabled"));
	}
	
	@Override
	public String[] getASMTransformerClass()
	{
		return new String[]{"zaggy1024.worldawareblocks.asm.Transformer"};
	}
	
	@Override
	public String getModContainerClass()
	{
		return null;
	}
	
	@Override
	public String getSetupClass()
	{
		return null;
	}
	
	@Override
	public String getAccessTransformerClass()
	{
		return null;
	}
}
