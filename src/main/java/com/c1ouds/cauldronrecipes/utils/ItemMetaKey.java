package com.c1ouds.cauldronrecipes.utils;

import net.minecraft.item.Item;
import java.util.Objects;

public class ItemMetaKey {
    public final Item item;
    public final int meta;

    public ItemMetaKey(Item item) {
        this.item = item;
        this.meta = 0;
    }

    public ItemMetaKey(net.minecraft.item.ItemStack stack) {
        this.item = stack.getItem();
        this.meta = stack.getItemDamage();
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
