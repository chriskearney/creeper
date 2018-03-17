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
        out.writeUTF(GSON.toJson(value, Item.class));
    }

    @Override
    public Item deserialize(@NotNull DataInput2 input, int available) throws IOException {
        return GSON.fromJson(input.readUTF(), Item.class);
    }

    @Override
    public int fixedSize() {
        return -1;
    }
}
