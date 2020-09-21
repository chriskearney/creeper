package com.comandante.creeper.cclient;

import com.terminal.Questioner;
import com.terminal.TtyConnector;

import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class SimpleTtyConnector implements TtyConnector {

    private final PipedInputStream inputStream;
    private final PipedOutputStream outputStream;
    private final String name =  UUID.randomUUID().toString();

    private final InputStreamReader inputStreamReader;

    public SimpleTtyConnector() {
        this.inputStream = new PipedInputStream();
        try {
            this.inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            this.outputStream = new PipedOutputStream(inputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean init(Questioner q) {
        return true;
    }

    @Override
    public void close() {
        try {
            outputStream.close();
            inputStream.close();
        } catch (Exception e) {
            throw new RuntimeException("Unable to close streams.", e);
        }
    }

    @Override
    public void resize(Dimension termSize, Dimension pixelSize) {

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int read(char[] buf, int offset, int length) throws IOException {
        return inputStreamReader.read(buf, offset, length);
    }

    @Override
    public void write(byte[] bytes) throws IOException {
        outputStream.write(bytes);
        outputStream.flush();
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public void write(String string) throws IOException {
        write(string.getBytes(Charset.defaultCharset()));
    }

    @Override
    public int waitFor() throws InterruptedException {
        return 0;
    }
}
