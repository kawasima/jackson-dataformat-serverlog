package net.unit8.jackson.dataformat.serverlog.impl;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.json.JsonReadContext;
import net.unit8.jackson.dataformat.serverlog.ServerLogParser;
import net.unit8.jackson.dataformat.serverlog.ServerLogSchema;
import nl.basjes.parse.core.Parser;
import nl.basjes.parse.httpdlog.HttpdLoglineParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.AbstractMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

public class ServerLogDecoder {
    private static final String DEFAULT_LOG_FORMAT = "%h %l %u %t \"%r\" %>s %b \"%{Referer}i\" \"%{User-Agent}i\"";

    private ServerLogParser owner;
    private BufferedReader inputSource;
    private Parser<LogEntry> _parser;
    private LinkedList<String> keys = null;
    private Map<String, String> entries = null;
    protected String _logFormat;

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
    protected int _currInputRow = 1;

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
                            ServerLogSchema schema) {
        this.owner = owner;
        _ioContext = ctxt;
        if (r instanceof BufferedReader) {
            inputSource = (BufferedReader) r;
        } else {
            inputSource = new BufferedReader(r);
        }
        setSchema(schema);
    }

    public void setSchema(ServerLogSchema schema) {
        _logFormat = schema.logFormat();
        _parser = new HttpdLoglineParser<>(LogEntry.class, Objects.toString(_logFormat, DEFAULT_LOG_FORMAT))
                .ignoreMissingDissectors()
        ;
    }

    protected final boolean loadMore() throws IOException {
        if (entries != null && !entries.isEmpty()) {
            return true;
        }
        if (inputSource != null) {
            String line = inputSource.readLine();

            if (line == null) {
                _closeInput();
                return false;
            }
            try {
                LogEntry entry = _parser.parse(line);
                entries = entry.getEntries();
                keys = new LinkedList<>(entries.keySet());
                return true;
            } catch (Exception e) {
                throw new IOException("Parse line error", e);
            }
        }
        return false;
    }


    public AbstractMap.SimpleEntry<String, String> nextEntry() throws IOException {
        if (entries != null && entries.isEmpty()) {
            loadMore();
            return null;
        }

        if (!loadMore()) {
            return null;
        }

        String key = keys.pop();
        String value = entries.remove(key);
        return new AbstractMap.SimpleEntry<>(key, value);
    }

    public boolean startNewLine() throws IOException {
        if (inputSource == null) {
            return false;
        }

        if (!loadMore()) {
            return false;
        }

        return true;
    }

    public Object getInputSource() {
        return inputSource;
    }

    protected void _closeInput() throws IOException {
        if (inputSource != null) {
            if (_ioContext.isResourceManaged()) {
                inputSource.close();
            }
            inputSource = null;
        }
    }

    public void close() throws IOException {
        if (!_closed) {
            _closed = true;
            _closeInput();
        }
    }

    public boolean isClosed() {
        return _closed;
    }

    public JsonReadContext childObjectContext(JsonReadContext context) {
        return context.createChildObjectContext(_currInputRow, _tokenInputCol);
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
