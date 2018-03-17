package com.comandante.creeper.storage;

import com.comandante.creeper.items.Item;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;
import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;
import org.mapdb.Serializer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

public class ItemSerializer implements Serializer<Item>, Serializable {

    private final static Gson GSON = new GsonBuilder().create();

    @Override
    public void serialize(@NotNull DataOutput2 out, @NotNull Item value) throws IOException {
        String s = GSON.toJson(value, Item.class);
        if (value.getInternalItemName().equals("basic wood chest")) {
            System.out.println("serializer hi");
        }
        out.writeUTF(s);
    }

    @Override
    public Item deserialize(@NotNull DataInput2 input, int available) throws IOException {
        String json = input.readUTF();
        Item item = GSON.fromJson(json, Item.class);
        if (item.getInternalItemName().equals("basic wood chest")) {
            System.out.println("deserializer hi");
        }
        return item;
    }

    @Override
    public int fixedSize() {
        return -1;
    }
}
