package net.unit8.jackson.dataformat.serverlog.impl;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.io.IOContext;
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

    private boolean _closed;
    private IOContext _ioContext;

    /**
     * Number of characters/bytes that were contained in previous blocks
     * (blocks that were already processed prior to the current buffer)
     */
    protected long _currInputProcessed = 0L;

    /**
     * Current row location of current point in input buffer, starting
     * from 1, if available.
     */
    protected int _currentInputRow = 1;

    /**
     * Current index of the first character of current row in input buffer.
     */
    protected int _currInputRowStart = 0;

    /**
     * Total number of bytes/characters read before start of current token.
     */
    protected long _tokenInputTotal = 0L;

    /**
     * Input row on which current token starts, 1-based.
     */
    protected int _tokenInputRow = 0;

    /**
     * Column on input row that current token starts; 0-based (although
     * in the end it'll be converted to 1-based)
     */
    protected int _tokenInputCol = 0;


    public ServerLogDecoder(ServerLogParser owner,
                            IOContext ctxt,
                            Reader r,
                            String logFormat) {
        this.owner = owner;
        _ioContext = ctxt;
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

    public Object getInputSource() {
        return inputSource;
    }

    public void close() throws IOException {
        if (!_closed) {
            _closed = true;
            if (inputSource != null) {
                if (_ioContext.isResourceManaged()) {
                    inputSource.close();
                }
                inputSource = null;
            }
        }
    }

    public boolean isClosed() {
        return _closed;
    }

    public JsonLocation getTokenLocation() {
        //return new JsonLocation(inputSource,);
        return null;
    }

    public JsonLocation getCurrentLocaion() {
        //return new JsonLocation(inputSource, );
        return null;
    }
}
