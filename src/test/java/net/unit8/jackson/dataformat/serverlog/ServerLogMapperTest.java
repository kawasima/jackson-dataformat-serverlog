package net.unit8.jackson.dataformat.serverlog;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.unit8.jackson.dataformat.serverlog.bean.Bean1;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import static org.junit.Assert.*;

public class ServerLogMapperTest {
    @Test
    public void deserializeToMap() throws IOException {
        String INPUT = "192.168.0.101 - - [12/May/2014:20:41:48 +0900] \"GET /index.html?abc=def&hij=klm HTTP/1.1\" 200 114 \"-\" \"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0\"";
        ServerLogMapper mapper = new ServerLogMapper();
        Map<String, String> obj = mapper.readValue(INPUT, new TypeReference<Map<String, String>>(){});
        System.out.println(obj);
    }

    @Test
    public void deserializeToBean() throws IOException {
        String INPUT = "192.168.0.101 - - [12/May/2014:20:41:48 +0900] \"GET /index.html?abc=def&hij=klm HTTP/1.1\" 200 114 \"-\" \"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0\"";
        ObjectMapper mapper = new ServerLogMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Bean1 res = mapper.readValue(INPUT, Bean1.class);
        System.out.println(res);
    }

    @Test
    public void readMultipleLines() throws IOException {
        String INPUT = "192.168.0.101 - - [12/May/2014:20:41:48 +0900] \"GET /index.html?abc=def&hij=klm HTTP/1.1\" 200 114 \"-\" \"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0\"\n" +
                "192.168.0.101 - - [12/May/2014:20:41:48 +0900] \"GET /index.html?abc=ABCABC&hij=KLMKLM HTTP/1.1\" 200 114 \"-\" \"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0\"\n";
        ObjectMapper mapper = new ObjectMapper(new ServerLogFactory())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MappingIterator<Bean1> iterator = mapper.readerFor(Bean1.class).readValues(INPUT);
        Bean1 record1 = iterator.next();
        assertNotNull(record1);
        assertEquals(record1.getAbc(), "def");
        Bean1 record2 = iterator.next();
        assertNotNull(record2);
        assertEquals(record2.getGih(), "KLMKLM");
        assertFalse(iterator.hasNextValue());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void write() throws IOException {
        ObjectMapper mapper = new ObjectMapper(new ServerLogFactory())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        StringWriter sw = new StringWriter();
        Bean1 bean = new Bean1();
        mapper.writeValue(sw, bean);
        System.out.println(sw);
    }

    @Test
    public void logFormat() throws IOException {
        String INPUT = "time:22/05/2018:16:40:33 +0900\tx_forwarded_for:-\tremote_host:66.249.XXX.YYY\trequest:GET /wordpress/?p=1548 HTTP/1.1\tlast_status:200\tsize:42160\treferer:-\tuser_agent:Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_1 like Mac OS X; en-us) AppleWebKit/532.9 (KHTML, like Gecko) Version/4.0.5 Mobile/8B117 Safari/6531.22.7 (compatible; Googlebot-Mobile/2.1; +http://www.google.com/bot.html)\tv_host:www.s-quad.com";
        ServerLogSchema schema = ServerLogSchema.emptySchema()
                .withLogFormat("time:%{%d/%m/%Y:%H:%M:%S %z}t\tx_forwarded_for:%{X-Forwarded-For}i\tremote_host:%h\trequest:%r\tlast_status:%>s\tsize:%b\treferer:%{Referer}i\tuser_agent:%{User-Agent}i\tv_host:%{Host}i");
        ObjectMapper mapper = new ObjectMapper(new ServerLogFactory())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Bean1 res = mapper.readerFor(Bean1.class)
                .with(schema)
                .readValue(INPUT);
        assertNotNull(res);
        assertEquals(16L, (long)res.getHour());
    }
}
