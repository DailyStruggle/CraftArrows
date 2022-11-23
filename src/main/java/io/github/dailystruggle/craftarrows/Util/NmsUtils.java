package io.github.dailystruggle.craftarrows.Util;

import org.bukkit.inventory.ItemStack;

public class NmsUtils {
    public static ItemStack RemoveDamageAttribute(ItemStack i) {
        Class<?> itemStackClass = NmsHelper.getNMSClass("ItemStack");
        Class<?> craftItemStack = NmsHelper.getOBClass("inventory.CraftItemStack");
        Class<?> nbtTagCompound = NmsHelper.getNMSClass("NBTTagCompound");
        Class<?> nbtTagList = NmsHelper.getNMSClass("NBTTagList");
        Class<?> nbtTagString = NmsHelper.getNMSClass("NBTTagString");
        Class<?> nbtTagInt = NmsHelper.getNMSClass("NBTTagInt");
        Class<?> nbtBase = NmsHelper.getNMSClass("NBTBase");
        try {
            Object nmsStack = craftItemStack.getDeclaredMethod("asNMSCopy", new Class[]{ItemStack.class}).invoke(null, i);
            Object compound = ((Boolean) itemStackClass.getMethod("hasTag", new Class[0]).invoke(nmsStack, new Object[0])).booleanValue() ? itemStackClass.getMethod("getTag", new Class[0]).invoke(nmsStack) : nbtTagCompound.newInstance();
            Object modifiers = nbtTagList.newInstance();
            Object damage = nbtTagCompound.newInstance();
            nbtTagCompound.getMethod("set", new Class[]{String.class, nbtBase}).invoke(damage, "AttributeName", nbtTagString.getConstructor(new Class[]{String.class}).newInstance("generic.attackDamage"));
            nbtTagCompound.getMethod("set", new Class[]{String.class, nbtBase}).invoke(damage, "Name", nbtTagString.getConstructor(new Class[]{String.class}).newInstance("generic.attackDamage"));
            nbtTagCompound.getMethod("set", new Class[]{String.class, nbtBase}).invoke(damage, "Amount", nbtTagInt.getConstructor(new Class[]{int.class}).newInstance(Integer.valueOf(0)));
            nbtTagCompound.getMethod("set", new Class[]{String.class, nbtBase}).invoke(damage, "Operation", nbtTagInt.getConstructor(new Class[]{int.class}).newInstance(Integer.valueOf(0)));
            nbtTagCompound.getMethod("set", new Class[]{String.class, nbtBase}).invoke(damage, "UUIDLeast", nbtTagInt.getConstructor(new Class[]{int.class}).newInstance(Integer.valueOf(894654)));
            nbtTagCompound.getMethod("set", new Class[]{String.class, nbtBase}).invoke(damage, "UUIDMost", nbtTagInt.getConstructor(new Class[]{int.class}).newInstance(Integer.valueOf(2872)));
            nbtTagCompound.getMethod("set", new Class[]{String.class, nbtBase}).invoke(damage, "Slot", nbtTagString.getConstructor(new Class[]{String.class}).newInstance("mainhand"));
            nbtTagList.getMethod("add", new Class[]{nbtBase}).invoke(modifiers, damage);
            nbtTagCompound.getMethod("set", new Class[]{String.class, nbtBase}).invoke(compound, "AttributeModifiers", modifiers);
            itemStackClass.getMethod("setTag", new Class[]{nbtTagCompound}).invoke(nmsStack, compound);
            i = (ItemStack) craftItemStack.getDeclaredMethod("asBukkitCopy", new Class[]{itemStackClass}).invoke(null, new Object[]{nmsStack});
        } catch (Exception e) {
            System.out.println(e);
        }
        return i;
    }
}
