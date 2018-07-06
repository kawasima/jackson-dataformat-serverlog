package net.unit8.jackson.dataformat.serverlog;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.io.IOContext;
import net.unit8.jackson.dataformat.serverlog.impl.ServerLogDecoder;

import java.io.*;
import java.net.URL;

public class ServerLogFactory extends JsonFactory {
    @Override
    public JsonParser createParser(File f) throws IOException {
        return _createParser(new FileInputStream(f), _createContext(f, true));
    }

    @Override
    public JsonParser createParser(URL url) throws IOException {
        return _createParser(_optimizedStreamFromURL(url), _createContext(url, true));
    }

    @Override
    public JsonParser createParser(InputStream in) throws IOException {
        return _createParser(in, _createContext(in, false));
    }

    @Override
    public JsonParser createParser(byte[] data) throws IOException {
        return _createParser(data, 0, data.length, _createContext(data, true));
    }

    @Override
    public JsonParser createParser(byte[] data, int offset, int len) throws IOException {
        return _createParser(data, offset, len, _createContext(data, true));
    }

    protected JsonParser _createParser(char[] data, int offset, int len, IOContext ctxt,
                                       boolean recyclable) throws IOException {
        return new ServerLogParser(ctxt,
                new CharArrayReader(data, offset, len),
                _parserFeatures,
                _objectCodec);
    }
}
