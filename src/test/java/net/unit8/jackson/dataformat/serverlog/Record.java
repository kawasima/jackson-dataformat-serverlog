package net.unit8.jackson.dataformat.serverlog;

import nl.basjes.parse.core.Field;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Record implements Serializable {
    private final Map<String, String> results = new HashMap<>(32);
    private String a;
    private long timestamp;

    @Field("STRING:request.firstline.uri.query.*")
    public void setQueryDeepMany(final String name, final String value) {
        results.put(name, value);
    }

    @Field("IP:connection.client.host")
    public void setIP(final String value) {
        results.put("IP:connection.client.host", value);
    }

    @Field("STRING:request.firstline.uri.query.a")
    public void setA(final String value) {
        this.a = value;
    }

    @Field("TIME.YEAR:request.receive.time.last.year")
    public void setTimestamp(final Long value) {
        this.timestamp = value;
    }

    @Override
    public String toString() {
        return results.toString();
    }
}
