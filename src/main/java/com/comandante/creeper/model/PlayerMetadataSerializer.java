package com.comandante.creeper.model;

import org.mapdb.Serializer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

public class PlayerMetadataSerializer implements Serializer<PlayerMetadata>, Serializable {

    @Override
    public void serialize(DataOutput out, PlayerMetadata value) throws IOException {
        out.writeUTF(value.getPlayerName());
        out.writeUTF(value.getPassword());
        out.writeUTF(value.getPlayerId());
        out.writeInt(value.getDexterity());
        out.writeInt(value.getHealth());
        out.writeInt(value.getStamina());
        out.writeInt(value.getStrength());
    }

    @Override
    public PlayerMetadata deserialize(DataInput in, int available) throws IOException {
        PlayerMetadata playerMetadata = new PlayerMetadata(in.readUTF(), in.readUTF(), in.readUTF());
        playerMetadata.setDexterity(in.readInt());
        playerMetadata.setHealth(in.readInt());
        playerMetadata.setStamina(in.readInt());
        playerMetadata.setStrength(in.readInt());
        return playerMetadata;
    }

    @Override
    public int fixedSize() {
        return -1;
    }
}

