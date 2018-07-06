package net.unit8.jackson.dataformat.serverlog;

import com.fasterxml.jackson.core.FormatSchema;

import java.io.Serializable;

public class ServerLogSchema implements FormatSchema, Serializable {
    @Override
    public String getSchemaType() {
        return "serverLog";
    }
}
