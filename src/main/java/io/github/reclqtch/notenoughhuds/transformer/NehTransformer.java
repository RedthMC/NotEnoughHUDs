package io.github.reclqtch.notenoughhuds.transformer;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;


public abstract class NehTransformer implements IClassTransformer {
    protected static final Logger logger = LogManager.getLogger("NotEnoughHUDs");
    private final String clazz;
    private final String methodName;

    public NehTransformer(String clazz, String method) {
        this.clazz = clazz;
        this.methodName = method;
    }

    protected static String mapMethodName(ClassNode classNode, MethodNode methodNode) {
        return FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(classNode.name, methodNode.name, methodNode.desc);
    }

    public abstract boolean transformMethod(InsnList methodInsns);

    @Override
    public byte[] transform(String name, String transName, byte[] transClass) {
        if (clazz.equals(transName)) {
            boolean success = false;
            try {
                ClassNode cn = new ClassNode();
                ClassReader cr = new ClassReader(transClass);
                cr.accept(cn, ClassReader.EXPAND_FRAMES);

                for (MethodNode method : cn.methods) {
                    String mapped = mapMethodName(cn, method);
                    if (!methodName.equals(mapped)) continue;
                    success = transformMethod(method.instructions);
                    break;
                }

                ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
                cn.accept(cw);
                return cw.toByteArray();
            } catch (Throwable e) {
                e.printStackTrace();
            } finally {
                logger.info((success ? "Succeed Transforming " : "Failed Transforming ") + transName);
            }
        }
        return transClass;
    }
}
