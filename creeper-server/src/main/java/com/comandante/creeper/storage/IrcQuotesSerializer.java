package com.comandante.creeper.storage;

import com.comandante.creeper.bot.command.QuoteManager;
import com.comandante.creeper.player.PlayerMetadata;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;
import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;
import org.mapdb.Serializer;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class IrcQuotesSerializer implements Serializer<List<QuoteManager.IrcQuote>>, Serializable {

    private final static Gson GSON = new GsonBuilder().create();
    private final Type listType = new TypeToken<ArrayList<QuoteManager.IrcQuote>>() {}.getType();


    @Override
    public void serialize(@NotNull DataOutput2 out, @NotNull List<QuoteManager.IrcQuote> value) throws IOException {
        out.writeUTF(GSON.toJson(value, listType));
    }

    @Override
    public ArrayList<QuoteManager.IrcQuote> deserialize(@NotNull DataInput2 input, int i) throws IOException {
        return GSON.fromJson(input.readUTF(), listType);
    }
}
