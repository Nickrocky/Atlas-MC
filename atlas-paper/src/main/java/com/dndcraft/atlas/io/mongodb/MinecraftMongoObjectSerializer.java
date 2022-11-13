package com.dndcraft.atlas.io.mongodb;

import com.dndcraft.atlas.io.mongodb.exceptions.AtlasSerializationException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bson.Document;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MinecraftMongoObjectSerializer extends AtlasDocumentSerializer {
    @Override
    public Document toDocument(Object o) throws AtlasSerializationException {
        Document document = null;
        if (o instanceof ItemStack) {
            ItemStack i = (ItemStack) o;
            document = new Document()
                    .append("Material", i.getType().name())
                    .append("Amount", i.getAmount())
                    .append("ItemMeta", toDocument(i.getItemMeta()));
        }
        if (o instanceof ItemMeta) {
            ItemMeta m = (ItemMeta) o;
            document = new Document();
            if (m.hasDisplayName())
                document.append("Display_Name", GsonComponentSerializer.gson().serialize(m.displayName()));
            if (m.hasLore()) {
                List<Component> lore = m.lore();
                List<String> jsonizedLore = new ArrayList<>();
                lore.stream().forEach(component -> jsonizedLore.add(GsonComponentSerializer.gson().serialize(component)));
                document.append("Lore", jsonizedLore);
            }
            if (m.hasCustomModelData()) document.append("CustomModelData", m.getCustomModelData());
            if (m.hasEnchants()) {
                List<Document> enchantmentDocuments = new ArrayList<>();
                for (Map.Entry<Enchantment, Integer> entry : m.getEnchants().entrySet()) {
                    Document enchantmentShard = new Document();
                    enchantmentShard.append("Enchantment", toDocument(entry.getKey()));
                    enchantmentShard.append("Level", entry.getValue());
                    enchantmentDocuments.add(enchantmentShard);
                }
                document.append("Enchantments", enchantmentDocuments);
            }
            if (m.hasAttributeModifiers()) {
                List<Document> attributeDocuments = new ArrayList<>();
                for (Map.Entry<Attribute, AttributeModifier> entry : m.getAttributeModifiers().entries()) {
                    Document attributeShard = new Document();
                    attributeShard.append("Attribute", entry.getKey().getKey().toString())
                            .append("AttributeModifier", toDocument(entry.getValue()));
                    attributeDocuments.add(attributeShard);
                }
                document.append("Attribute_Modifiers", attributeDocuments);
            }
        }
        if (o instanceof Enchantment) {
            Enchantment e = (Enchantment) o;
            document = new Document()
                    .append("Enchantment_Key", e.getKey().toString());
        }
        if (o instanceof NamespacedKey) { //Idk wtf you would use this for but go off -nickrocky
            NamespacedKey namespacedKey = (NamespacedKey) o;
            document = new Document()
                    .append("Namespaced_Key", namespacedKey.toString());
        }
        if (o instanceof AttributeModifier) {
            AttributeModifier a = (AttributeModifier) o;
            document = new Document()
                    .append("UUID", a.getUniqueId().toString())
                    .append("Name", a.getName())
                    .append("Amount", a.getAmount())
                    .append("Operation", a.getOperation().name());
            if (a.getSlot() != null) {
                document.append("Slot", a.getSlot().name());
            }
        }
        if (o instanceof Component) {
            Component c = (Component) o;
            document = new Document()
                    .append("Component", GsonComponentSerializer.gson().serialize(c));
        }
        if (document == null) {
            throw new AtlasSerializationException(o);
        }

        return document;
    }
}
