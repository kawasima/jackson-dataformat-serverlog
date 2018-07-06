package net.unit8.jackson.dataformat.serverlog;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ServerLogMapper extends ObjectMapper {
    public ServerLogMapper() {
        this(new ServerLogFactory());
    }

    public ServerLogMapper(ServerLogFactory f) {
        super(f);
    }

    protected ServerLogMapper(ServerLogMapper src) {
        super(src);
    }
}
