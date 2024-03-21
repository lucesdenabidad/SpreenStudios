package datta.core.content.builders;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static datta.core.content.builders.ColorBuilder.color;
import static datta.core.content.builders.ColorBuilder.colorList;

@SuppressWarnings("ALL")
public class ItemBuilder {
    private String displayName;
    private Material material;
    private List<String> lore = new ArrayList<>();

    private Map<Enchantment, Integer> enchantmentIntegerMap = new HashMap<>();
    private Color leatherColor;
    private int customModelData;
    private String headPlayerName;
    private String headUrl;

    boolean hideAll;

    public ItemBuilder(Material material, String displayName) {
        this.material = material;
        this.displayName = displayName;
    }

    public ItemBuilder addEnchant(Enchantment enchantment, int power) {
        enchantmentIntegerMap.put(enchantment, power);
        return this;
    }

    public ItemBuilder setHeadPlayer(String name) {
        this.headPlayerName = name;
        return this;
    }

    public ItemBuilder setHeadUrl(String url) {
        this.headUrl = url;
        return this;
    }


    public ItemBuilder setLeatherColor(Color color) {
        this.leatherColor = color;
        return this;
    }

    public ItemBuilder setCustomModelData(int data) {
        this.customModelData = data;
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        this.lore = List.of(lore);
        return this;
    }

    public ItemBuilder addLore(String... addLore) {
        lore.addAll(List.of(addLore));

        return this;
    }

    public ItemBuilder addLore(List<String> addLore) {
        lore.addAll(addLore);
        return this;
    }

    public ItemBuilder hideAll(boolean v) {
        this.hideAll = v;
        return this;
    }


    public ItemStack build() {

        ItemStack itemStack = new ItemStack(material);

        if (headUrl != null) {
            itemStack = SkullBuilder.itemFromUrl(headUrl);
        }

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(color(displayName));
        if (!lore.isEmpty() || lore != null) {
            itemMeta.setLore(colorList(lore));
        }

        for (Map.Entry<Enchantment, Integer> entry : enchantmentIntegerMap.entrySet()) {
            itemMeta.addEnchant(entry.getKey(), entry.getValue(), true);
        }

        if (hideAll) {
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            itemMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            itemMeta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
            itemMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        }

        if (material == Material.LEATHER_HELMET || material == Material.LEATHER_CHESTPLATE ||
                material == Material.LEATHER_LEGGINGS || material == Material.LEATHER_BOOTS) {
            LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemMeta;
            if (leatherColor != null) {
                leatherArmorMeta.setColor(leatherColor);
            }
            itemStack.setItemMeta(leatherArmorMeta);

        } else if (material == Material.PLAYER_HEAD) {
            SkullMeta skullMeta = (SkullMeta) itemMeta;
            if (headPlayerName != null) {
                skullMeta.setOwner(headPlayerName);
            }


            itemStack.setItemMeta(skullMeta);
        } else {
            itemStack.setItemMeta(itemMeta);
        }

        if (customModelData > 0) {
            itemMeta.setCustomModelData(customModelData);
        }

        return itemStack;
    }
}
