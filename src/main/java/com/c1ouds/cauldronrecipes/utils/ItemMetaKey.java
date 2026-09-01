package com.c1ouds.cauldronrecipes.utils;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import net.minecraft.item.Item;
import java.util.Objects;

public class ItemMetaKey {
    // Automatic reduction of identical objects in favor of earliest one
    private static final Interner<ItemMetaKey> POOL = Interners.newWeakInterner();
    public ItemMetaKey intern() {
        return POOL.intern(this);
    }

    public final Item item;
    public final int meta;

    public ItemMetaKey(Item item) {
        this.item = item;
        this.meta = 0;
    }
    public ItemMetaKey(Item item, int meta) {
        this.item = item;
        this.meta = meta;
    }
    public ItemMetaKey(net.minecraft.item.ItemStack stack) {
        this.item = stack.getItem();
        this.meta = stack.getItemDamage();
    }

    public ItemMetaKey withMeta(int newMeta) {
        return new ItemMetaKey(this.item, newMeta).intern();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemMetaKey that = (ItemMetaKey) o;
        return this.meta == that.meta && this.item == that.item;
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, meta);
    }
}
