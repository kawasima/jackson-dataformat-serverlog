package net.unit8.jackson.dataformat.serverlog;

import nl.basjes.parse.core.exceptions.DissectionFailure;
import nl.basjes.parse.core.exceptions.InvalidDissectorException;
import nl.basjes.parse.core.exceptions.MissingDissectorsException;
import nl.basjes.parse.httpdlog.HttpdLoglineParser;
import org.junit.Test;

public class LoglineParserTest {
    String logformat = "%h %l %u %t \"%r\" %>s %b \"%{Referer}i\" \"%{User-Agent}i\"";

    String logline = "192.168.0.101 - - [12/May/2014:20:41:48 +0900] \"GET /index.html?a=b&c=%20D HTTP/1.1\" 200 114 \"-\" \"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0\"";
    @Test
    public void test() throws InvalidDissectorException, MissingDissectorsException, DissectionFailure {
        System.out.println(new HttpdLoglineParser<>(Object.class, logformat).getPossiblePaths());

        HttpdLoglineParser<Record> parser = new HttpdLoglineParser<>(Record.class, logformat);
        Record record = new Record();
        parser.parse(record, logline);
        System.out.println(record);
    }
}
