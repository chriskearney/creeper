package com.comandante.creeper.bot;

import com.comandante.creeper.spells.Effect;
import com.google.gson.GsonBuilder;
import org.mapdb.Serializer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

public class QuoteSerializer implements Serializer<Quote>, Serializable {
    @Override
    public void serialize(DataOutput out, Quote value) throws IOException {
        out.writeUTF(new GsonBuilder().create().toJson(value, Effect.class));
    }

    @Override
    public Quote deserialize(DataInput in, int available) throws IOException {
        return new GsonBuilder().create().fromJson(in.readUTF(), Quote.class);
    }

    @Override
    public int fixedSize() {
        return -1;
    }
}