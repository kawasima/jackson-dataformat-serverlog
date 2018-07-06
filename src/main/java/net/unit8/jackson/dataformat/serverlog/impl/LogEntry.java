package net.unit8.jackson.dataformat.serverlog.impl;

import nl.basjes.parse.core.Field;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class LogEntry implements Serializable {
    private final Map<String, String> entries = new HashMap<>(32);

    @Field("STRING:request.firstline.uri.query.*")
    public void setQueryDeepMany(final String name, final String value) {
        entries.put(name, value);
    }

    @Field("STRING:request.firstline.uri.query.img")
    public void setQueryImg(final String name, final String value) {
        entries.put(name, value);
    }

    @Field("IP:connection.client.host")
    public void setIP(final String value) {
        entries.put("IP:connection.client.host", value);
    }

    @Field({
            "HTTP.QUERYSTRING:request.firstline.uri.query",
            "NUMBER:connection.client.logname",
            "STRING:connection.client.user",
            "TIME.STAMP:request.receive.time",
            "HTTP.URI:request.firstline.uri",
            "BYTESCLF:response.body.bytes",
            "HTTP.URI:request.referer",
            "HTTP.USERAGENT:request.user-agent",
            "TIME.DAY:request.receive.time.day",
            "TIME.HOUR:request.receive.time.hour",
            "TIME.MONTHNAME:request.receive.time.monthname"
    })
    public void setValue(final String name, final String value) {
        entries.put(name, value);
    }

    public Map<String, String> getEntries() {
        return entries;
    }
}
