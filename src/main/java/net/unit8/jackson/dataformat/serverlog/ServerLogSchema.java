package net.unit8.jackson.dataformat.serverlog;

import com.fasterxml.jackson.core.FormatSchema;

import java.io.Serializable;
import java.util.Objects;

public class ServerLogSchema implements FormatSchema, Serializable {
    protected final static ServerLogSchema EMPTY = new ServerLogSchema();

    protected String _logFormat;

    public ServerLogSchema() {

    }

    public ServerLogSchema(ServerLogSchema base) {
        this._logFormat = base._logFormat;
    }

    @Override
    public String getSchemaType() {
        return "serverLog";
    }

    public ServerLogSchema withLogFormat(String logFormat) {
        if (logFormat.equals(_logFormat)) {
            return this;
        }
        ServerLogSchema s = new ServerLogSchema(this);
        s._logFormat = logFormat;
        return s;
    }

    public String logFormat() {
        return _logFormat;
    }

    public static ServerLogSchema emptySchema() {
        return EMPTY;
    }
}
