function initializeCoreMod()
{
	return {
		'coremodone': {
			'target': { 'type': 'CLASS', 'name': 'net.minecraft.resources.SimpleReloadableResourceManager' },
			'transformer': function(classNode)
			{
				var Opcodes = Java.type('org.objectweb.asm.Opcodes');
				print("Transforming SimpleReloadableResourceManager!");
				var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
				var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
				var api = Java.type('net.minecraftforge.coremod.api.ASMAPI');
				var methods = classNode.methods;
				for(m in methods)
				{
					var method = methods[m];
					if(method.name.equals("reloadResources") && method.desc.startsWith("(Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/concurrent/CompletableFuture;Ljava/util/List;)"))
					{
						var code = method.instructions;
						code.insertBefore(code.get(5), new MethodInsnNode(Opcodes.INVOKESTATIC, "tk/zeitheron/solarflux/client/SolarFluxResourcePack", "addResourcePack", "(L" + classNode.name + ";)V", false));
						code.insertBefore(code.get(5), new VarInsnNode(Opcodes.ALOAD, 0));
					} else if(method.name.equals("initialReload") && method.desc.startsWith("(Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/concurrent/CompletableFuture;)"))
					{
						var code = method.instructions;
						code.insertBefore(code.getFirst(), new MethodInsnNode(Opcodes.INVOKESTATIC, "tk/zeitheron/solarflux/client/SolarFluxResourcePack", "addResourcePack", "(L" + classNode.name + ";)V", false));
						code.insertBefore(code.getFirst(), new VarInsnNode(Opcodes.ALOAD, 0));
					}
				}

				return classNode;
			}
		}
	}
}