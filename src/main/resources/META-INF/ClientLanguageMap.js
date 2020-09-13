function initializeCoreMod()
{
	return {
		'coremodone': {
			'target': { 'type': 'CLASS', 'name': 'net.minecraft.client.resources.ClientLanguageMap' },
			'transformer': function(classNode)
			{
				var Opcodes = Java.type('org.objectweb.asm.Opcodes');
				print("Transforming ClientLanguageMap!");
				var fields = classNode.fields;
				for(m in fields)
				{
					var field = fields[m];

					if((field.access & Opcodes.ACC_FINAL) != 0 && field.desc == "Ljava/util/Map;")
					{
					    field.access = field.access & ~Opcodes.ACC_FINAL;
					    print("  -- Remove final modifier from " + field.desc + " " + field.name);
					}
				}

				return classNode;
			}
		}
	}
}