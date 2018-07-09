package net.unit8.jackson.dataformat.serverlog;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.base.ParserMinimalBase;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.json.DupDetector;
import com.fasterxml.jackson.core.json.JsonReadContext;
import com.fasterxml.jackson.core.json.PackageVersion;
import com.fasterxml.jackson.core.util.TextBuffer;
import net.unit8.jackson.dataformat.serverlog.impl.ServerLogDecoder;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.AbstractMap;

public class ServerLogParser extends ParserMinimalBase {
    private enum ParseState {
        DOC_START,
        RECORD_START,
        NEXT_ENTRY,
        NAMED_VALUE,
        DOC_END,
    }

    protected ParseState _state = ParseState.DOC_START;
    protected final ServerLogDecoder _reader;
    protected JsonReadContext _parsingContext;
    protected final TextBuffer _textBuffer;
    protected String _currentName;
    protected String _currentValue;
    protected ObjectCodec _objectCodec;

    public ServerLogParser(IOContext ctxt, Reader reader, int stdFeatures, ObjectCodec codec) {
        _textBuffer = ctxt.constructTextBuffer();
        String logformat = "%h %l %u %t \"%r\" %>s %b \"%{Referer}i\" \"%{User-Agent}i\"";
        _reader = new ServerLogDecoder(this, ctxt, reader, logformat);
        DupDetector dups = JsonParser.Feature.STRICT_DUPLICATE_DETECTION.enabledIn(stdFeatures)
                ? DupDetector.rootDetector(this) : null;
        _parsingContext = JsonReadContext.createRootContext(dups);
        _objectCodec = codec;
    }

    @Override
    public JsonToken nextToken() throws IOException {
        switch (_state) {
            case DOC_START:
                return (_currToken = _handleStartDoc());
            case RECORD_START:
                return (_currToken = _handleRecordStart());
            case NEXT_ENTRY:
                return (_currToken = _handleNextEntry());
            case NAMED_VALUE:
                return (_currToken = _handleNamedValue());
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public boolean nextFieldName(SerializableString str) throws IOException {
        // Optimize for expected case of getting FIELD_NAME:
        if (_state == ParseState.NEXT_ENTRY) {
            JsonToken t = _handleNextEntry();
            _currToken = t;
            if (t == JsonToken.FIELD_NAME) {
                return str.getValue().equals(_currentName);
            }
            return false;
        }
        // unlikely, but verify just in case
        return (nextToken() == JsonToken.FIELD_NAME) && str.getValue().equals(getCurrentName());
    }

    @Override
    public String nextFieldName() throws IOException
    {
        // Optimize for expected case of getting FIELD_NAME:
        if (_state == ParseState.NEXT_ENTRY) {
            JsonToken t = _handleNextEntry();
            _currToken = t;
            if (t == JsonToken.FIELD_NAME) {
                return _currentName;
            }
            return null;
        }
        // unlikely, but verify just in case
        return (nextToken() == JsonToken.FIELD_NAME) ? getCurrentName() : null;
    }

    @Override
    public String nextTextValue() throws IOException
    {
        JsonToken t;
        if (_state == ParseState.NAMED_VALUE) {
            _currToken = t = _handleNamedValue();
            if (t == JsonToken.VALUE_STRING) {
                return _currentValue;
            }
        } else {
            t = nextToken();
            if (t == JsonToken.VALUE_STRING) {
                return getText();
            }
        }
        return null;
    }

    @Override
    protected void _handleEOF() throws JsonParseException {

    }

    @Override
    public String getCurrentName() throws IOException {
        return _currentName;
    }

    @Override
    public ObjectCodec getCodec() {
        return _objectCodec;
    }

    @Override
    public void setCodec(ObjectCodec objectCodec) {
        _objectCodec = objectCodec;
    }

    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }

    @Override
    public void close() throws IOException {
        _reader.close();
    }

    @Override
    public boolean isClosed() {
        return _reader.isClosed();
    }

    @Override
    public JsonStreamContext getParsingContext() {
        return _parsingContext;
    }

    @Override
    public JsonLocation getTokenLocation() {
        return _reader.getTokenLocation();
    }

    @Override
    public JsonLocation getCurrentLocation() {
        return _reader.getCurrentLocaion();
    }

    @Override
    public String currentName() throws IOException {
        return _currentName;
    }

    @Override
    public void overrideCurrentName(String name) {
        _currentName = name;
    }

    @Override
    public String getText() throws IOException {
        if (_currToken == JsonToken.FIELD_NAME) {
            return _currentName;
        }
        return _currentValue;
    }

    @Override
    public char[] getTextCharacters() throws IOException {
        if (_currToken == JsonToken.FIELD_NAME) {
            return _currentName.toCharArray();
        }
        return _textBuffer.contentsAsArray();
    }

    @Override
    public boolean hasTextCharacters() {
        return false;
    }

    @Override
    public Number getNumberValue() throws IOException {
        return null;
    }

    @Override
    public NumberType getNumberType() throws IOException {
        return null;
    }

    @Override
    public int getIntValue() throws IOException {
        return 0;
    }

    @Override
    public long getLongValue() throws IOException {
        return 0;
    }

    @Override
    public BigInteger getBigIntegerValue() throws IOException {
        return null;
    }

    @Override
    public float getFloatValue() throws IOException {
        return 0;
    }

    @Override
    public double getDoubleValue() throws IOException {
        return 0;
    }

    @Override
    public BigDecimal getDecimalValue() throws IOException {
        return null;
    }

    @Override
    public int getTextLength() throws IOException {
        if (_currToken == JsonToken.FIELD_NAME) {
            return _currentName.length();
        }
        return _textBuffer.size();
    }

    @Override
    public int getTextOffset() throws IOException {
        return 0;
    }

    @Override
    public byte[] getBinaryValue(Base64Variant base64Variant) throws IOException {
        return new byte[0];
    }

    public JsonToken _handleStartDoc() {
        return _handleRecordStart();
    }

    public JsonToken _handleRecordStart() {
        _state = ParseState.NEXT_ENTRY;
        return JsonToken.START_OBJECT;
    }

    public JsonToken _handleNextEntry() throws IOException {
        AbstractMap.SimpleEntry<String, String> next;
        try {
            next = _reader.nextEntry();
        } catch (IOException e) {
            // 12-Oct-2015, tatu: Need to resync here as well...
            throw e;
        }

        if (next == null) { // end of record or input...
            return _handleRecordEnd();
        }

        _currentName = next.getKey();
        _currentValue = next.getValue();
        _state = ParseState.NAMED_VALUE;
        return JsonToken.FIELD_NAME;
    }

    protected JsonToken _handleNamedValue() throws IOException {
        _state = ParseState.NEXT_ENTRY;
        if (_currentValue == null) {
            return JsonToken.VALUE_NULL;
        }
        return JsonToken.VALUE_STRING;
    }


    protected final JsonToken _handleRecordEnd() throws IOException {
        _parsingContext = _parsingContext.getParent();
        if (!_reader.startNewLine()) {
            _state = ParseState.DOC_END;
        } else {
            _state = ParseState.RECORD_START;
        }
        return JsonToken.END_OBJECT;
    }
}
