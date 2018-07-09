package net.unit8.jackson.dataformat.serverlog;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.unit8.jackson.dataformat.serverlog.bean.Bean1;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

public class ServerLogMapperTest {
    @Test
    public void test() throws IOException {
        String INPUT = "192.168.0.101 - - [12/May/2014:20:41:48 +0900] \"GET /index.html?abc=def&hij=klm HTTP/1.1\" 200 114 \"-\" \"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0\"";
        ServerLogMapper mapper = new ServerLogMapper();
        Map<String, String> obj = mapper.readValue(INPUT, new TypeReference<Map<String, String>>(){});
        System.out.println(obj);
    }

    @Test
    public void testBean() throws IOException {
        String INPUT = "192.168.0.101 - - [12/May/2014:20:41:48 +0900] \"GET /index.html?abc=def&hij=klm HTTP/1.1\" 200 114 \"-\" \"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0\"";
        ObjectMapper mapper = new ServerLogMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Bean1 res = mapper.readValue(INPUT, Bean1.class);
        System.out.println(res);
    }

}
