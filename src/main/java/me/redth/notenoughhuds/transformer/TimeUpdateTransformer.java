package me.redth.notenoughhuds.transformer;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;

public class TimeUpdateTransformer extends NehTransformer {

    public TimeUpdateTransformer() {
        super("net.minecraft.client.network.NetHandlerPlayClient", "func_147285_a");
    }

    @Override
    public boolean transformMethod(InsnList methodInsns) {
        for (AbstractInsnNode insn : methodInsns.toArray()) {
            if (insn.getOpcode() != Opcodes.RETURN) continue;
            MethodInsnNode m = new MethodInsnNode(Opcodes.INVOKESTATIC, "me/redth/notenoughhuds/NotEnoughHUDs", "onTimeUpdate", "()V", false);
            methodInsns.insertBefore(insn, m);
            return true;
        }
        return false;
    }

}
