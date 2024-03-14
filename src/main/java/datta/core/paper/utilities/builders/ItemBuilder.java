package datta.core.paper.utilities.builders;


import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static datta.core.paper.utilities.Color.format;
import static datta.core.paper.utilities.Color.formatList;


@SuppressWarnings("ALL")
public class ItemBuilder {
    private String displayName;
    private List<String> lore;
    private Material material;

    public ItemBuilder(Material material, String displayName, String... lore) {
        this.displayName = displayName;
        this.lore = Arrays.asList(lore);
        this.material = material;
    }

    public ItemBuilder(Material material, String displayName, ArrayList<String> lore) {
        this.displayName = displayName;
        this.lore = new ArrayList<>(lore);
        this.material = material;
    }

    public ItemBuilder(Material material, String displayName, List<String> lore) {
        this.displayName = displayName;
        this.lore = new ArrayList<>(lore);
        this.material = material;
    }

    private void setupItemMeta(ItemStack itemStack, ItemMeta itemMeta) {
        itemMeta.setDisplayName(format(displayName));
        itemMeta.setLore(formatList(lore));
        itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DYE);
        itemMeta.setUnbreakable(true);
        itemStack.setItemMeta(itemMeta);
    }

    public ItemStack build() {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        setupItemMeta(itemStack, itemMeta);
        return itemStack;
    }

    public ItemStack buildEnchants(int power, Enchantment... enchantments) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        setupItemMeta(itemStack, itemMeta);
        for (Enchantment enchant : enchantments) {
            itemMeta.addEnchant(enchant, power, true);
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public ItemStack buildCustomModelData(int customModelData) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setCustomModelData(customModelData);
        setupItemMeta(itemStack, itemMeta);
        return itemStack;
    }

    public ItemStack build(int count) {
        ItemStack itemStack = new ItemStack(material, count);
        ItemMeta itemMeta = itemStack.getItemMeta();
        setupItemMeta(itemStack, itemMeta);
        return itemStack;
    }

    public ItemStack buildLeather(Color color) {
        ItemStack itemStack = new ItemStack(material);
        LeatherArmorMeta itemMeta = (LeatherArmorMeta) itemStack.getItemMeta();
        setupItemMeta(itemStack, itemMeta);
        itemMeta.setColor(color);
        return itemStack;
    }

    public ItemStack buildPlayerHead(String playerName) {
        ItemStack itemStack = SkullBuilder.itemFromName(playerName);
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        setupItemMeta(itemStack, skullMeta);
        return itemStack;
    }

    public ItemStack buildFireworkStar(Color fireworkColor) {
        if (fireworkColor == null) {
            throw new IllegalArgumentException("Firework color cannot be null.");
        }

        ItemStack itemStack = new ItemStack(Material.FIREWORK_STAR);
        FireworkEffectMeta itemMeta = (FireworkEffectMeta) itemStack.getItemMeta();
        setupItemMeta(itemStack, itemMeta);

        FireworkEffect.Builder builder = FireworkEffect.builder();
        builder.withColor(fireworkColor);

        FireworkEffect effect = builder.build();
        itemMeta.setEffect(effect);

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }


    public ItemStack buildTexture(String textureUrl) {
        ItemStack itemStack = SkullBuilder.itemFromUrl(textureUrl);
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        setupItemMeta(itemStack, skullMeta);
        return itemStack;
    }
}