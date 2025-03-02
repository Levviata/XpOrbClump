package surreal.fixeroo.core.transformers;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.*;
import surreal.fixeroo.FixerooConfig;

import java.util.Iterator;

// Adds a way to color shulker entities.
public class ShulkerColoringTransformer extends TypicalTransformer {

    public static byte[] transformEntityShulker(String transformedName, byte[] basicClass) {
        if (!FixerooConfig.shulkerColoring.enable) return basicClass;
        ClassNode cls = read(transformedName, basicClass);
        {
            MethodVisitor setColor = cls.visitMethod(ACC_PUBLIC, "setColor", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/EnumHand;Lnet/minecraft/item/EnumDyeColor;)Z", null, null);
            setColor.visitVarInsn(ALOAD, 3);
            Label l_con = new Label();
            setColor.visitJumpInsn(IFNULL, l_con);
            setColor.visitLabel(new Label());

            setColor.visitVarInsn(ALOAD, 0);
            setColor.visitFieldInsn(GETFIELD, cls.name, getName("dataManager", "field_70180_af"), "Lnet/minecraft/network/datasync/EntityDataManager;");
            setColor.visitFieldInsn(GETSTATIC, cls.name, getName("COLOR", "field_190770_bw"), "Lnet/minecraft/network/datasync/DataParameter;");
            setColor.visitVarInsn(ALOAD, 3);
            setColor.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/item/EnumDyeColor", getName("getMetadata", "func_176765_a"), "()I", false);
            setColor.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
            setColor.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/network/datasync/EntityDataManager", getName("set", "func_187227_b"), "(Lnet/minecraft/network/datasync/DataParameter;Ljava/lang/Object;)V", false);

            setColor.visitVarInsn(ALOAD, 1);
            setColor.visitVarInsn(ALOAD, 2);
            setColor.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/entity/player/EntityPlayer", getName("swingArm", "func_184609_a"), "(Lnet/minecraft/util/EnumHand;)V", false);

            setColor.visitInsn(ICONST_1);
            setColor.visitInsn(IRETURN);

            setColor.visitLabel(l_con);
            setColor.visitFrame(F_SAME, 0, null, 0, null);
            setColor.visitInsn(ICONST_0);
            setColor.visitInsn(IRETURN);
        }
        {
            MethodVisitor dyeInteract = cls.visitMethod(ACC_PROTECTED, getName("processInteract", "func_184645_a"), "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/EnumHand;)Z", null, null);
            dyeInteract.visitVarInsn(ALOAD, 1);
            dyeInteract.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/entity/player/EntityPlayer", getName("isSneaking", "func_70093_af"), "()Z", false);
            Label l_con = new Label();
            dyeInteract.visitJumpInsn(IFEQ, l_con);
            dyeInteract.visitLabel(new Label());
            dyeInteract.visitInsn(ICONST_0);
            dyeInteract.visitInsn(IRETURN);

            dyeInteract.visitLabel(l_con);
            dyeInteract.visitFrame(F_SAME, 0, null, 0, null);
            dyeInteract.visitVarInsn(ALOAD, 1);
            dyeInteract.visitVarInsn(ALOAD, 2);
            dyeInteract.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/entity/player/EntityPlayer", getName("getHeldItem", "func_184586_b"), "(Lnet/minecraft/util/EnumHand;)Lnet/minecraft/item/ItemStack;", false);
            dyeInteract.visitVarInsn(ASTORE, 3); // HeldStack

            dyeInteract.visitVarInsn(ALOAD, 0);
            dyeInteract.visitVarInsn(ALOAD, 1);
            dyeInteract.visitVarInsn(ALOAD, 2);
            dyeInteract.visitVarInsn(ALOAD, 3);
            dyeInteract.visitMethodInsn(INVOKESTATIC, "surreal/fixeroo/core/FixerooHooks", "EntityShulker$getColorFromStack", "(Lnet/minecraft/entity/monster/EntityShulker;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/EnumHand;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/EnumDyeColor;", false);
            dyeInteract.visitVarInsn(ASTORE, 4);

            dyeInteract.visitVarInsn(ALOAD, 0);
            dyeInteract.visitVarInsn(ALOAD, 1);
            dyeInteract.visitVarInsn(ALOAD, 2);
            dyeInteract.visitVarInsn(ALOAD, 4);
            dyeInteract.visitMethodInsn(INVOKEVIRTUAL, cls.name, "setColor", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/EnumHand;Lnet/minecraft/item/EnumDyeColor;)Z", false);
            dyeInteract.visitInsn(IRETURN);
        }
        return write(cls);
    }

    public static byte[] transformFeatureShulkerPearlItem(String transformedName, byte[] basicClass) {
        if (!FixerooConfig.shulkerColoring.enable) return basicClass;
        ClassNode cls = read(transformedName, basicClass);
        MethodNode method = cls.methods.get(cls.methods.size() - 2);
        Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
        while (iterator.hasNext()) {
            AbstractInsnNode node = iterator.next();
            if (node.getOpcode() == IFGT) {
                String entityInteract = "net/minecraftforge/event/entity/player/PlayerInteractEvent$EntityInteract";

                InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, entityInteract, "getTarget", "()Lnet/minecraft/entity/Entity;", false));
                list.add(new TypeInsnNode(CHECKCAST, "net/minecraft/entity/monster/EntityShulker"));
                list.add(new VarInsnNode(ASTORE, 3));

                list.add(new VarInsnNode(ALOAD, 3));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, entityInteract, "getEntityPlayer", "()Lnet/minecraft/entity/player/EntityPlayer;", false));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, entityInteract, "getHand", "()Lnet/minecraft/util/EnumHand;", false));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, entityInteract, "getItemStack", "()Lnet/minecraft/item/ItemStack;", false));
                list.add(hook("EntityShulker$getColorFromStack", "(Lnet/minecraft/entity/monster/EntityShulker;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/EnumHand;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/EnumDyeColor;"));
                list.add(new VarInsnNode(ASTORE, 4));

                list.add(new VarInsnNode(ALOAD, 3));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, entityInteract, "getEntityPlayer", "()Lnet/minecraft/entity/player/EntityPlayer;", false));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, entityInteract, "getHand", "()Lnet/minecraft/util/EnumHand;", false));
                list.add(new VarInsnNode(ALOAD, 4));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/entity/monster/EntityShulker", "setColor", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/EnumHand;Lnet/minecraft/item/EnumDyeColor;)Z", false));
                LabelNode l_con = new LabelNode();
                list.add(new JumpInsnNode(IFEQ, l_con));
                list.add(new LabelNode());
                list.add(new InsnNode(RETURN));

                list.add(l_con);

                method.instructions.insert(node, list);
                break;
            }
        }
        return write(cls, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
    }
}