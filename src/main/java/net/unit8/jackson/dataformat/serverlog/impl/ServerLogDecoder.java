package net.unit8.jackson.dataformat.serverlog.impl;

import net.unit8.jackson.dataformat.serverlog.ServerLogParser;
import nl.basjes.parse.httpdlog.HttpdLoglineParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

public class ServerLogDecoder {
    private ServerLogParser owner;
    private BufferedReader inputSource;
    private final HttpdLoglineParser<LogEntry> parser;
    private LinkedList<String> keys = null;
    private Map<String, String> entries = null;

    public ServerLogDecoder(ServerLogParser owner,
                            Reader r,
                            String logFormat) {
        this.owner = owner;
        if (r instanceof BufferedReader) {
            inputSource = (BufferedReader) r;
        } else {
            inputSource = new BufferedReader(r);
        }
        parser = new HttpdLoglineParser<>(LogEntry.class, logFormat);
    }

    public AbstractMap.SimpleEntry<String, String> nextEntry() throws IOException {
        if (entries == null || entries.isEmpty()) {
            String line = inputSource.readLine();
            if (line == null) return null;

            try {
                LogEntry entry = parser.parse(line);
                entries = entry.getEntries();
                keys = new LinkedList<>(entries.keySet());
            } catch (Exception e) {
                throw new IOException("Parse line error", e);
            }
        }

        String key = keys.pop();
        String value = entries.remove(key);
        return new AbstractMap.SimpleEntry<>(key, value);
    }

    public boolean startNewLine() throws IOException {
        return false;
    }
}
