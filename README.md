# Jackson dataformat for Server logs

Jackson (Java) data format module that supports reading apache or nginx log files.

## Usage

When creating a ObjectMapper with `ServerLogFactory`, you can use it as a deserializer for a server log file.

```java
ObjectMapper mapper = new ObjectMapper(new ServerLogFactory())
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

Pojo logObject = mapper.readValue(Pojo.class, logEntry);
```

Note: You must set `false` to `DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES`. So The `ServerLogParser`
generates many attributes per a log entry.

You can set a log format via ServerLogSchema.

```java
ServerLogSchema schema = ServerLogSchema.emptySchema()
    .withLogFormat("time:%{%d/%m/%Y:%H:%M:%S %z}t\tx_forwarded_for:%{X-Forwarded-For}i\tremote_host:%h\trequest:%r\tlast_status:%>s\tsize:%b\treferer:%{Referer}i\tuser_agent:%{User-Agent}i\tv_host:%{Host}i");
ObjectMapper mapper = new ObjectMapper(new ServerLogFactory())
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
Pojo res = mapper.readerFor(Pojo.class)
    .with(schema)
    .readValue(logEntry);
```

### Bean settings

These properties are available in `@JsonProperty`.

|Propety name|Description|
|:------|:------|
|HTTP.URI:request.firstline.uri|A request URI|
|HTTP.QUERYSTRING:request.firstline.uri.query|A raw querystring|
|STRING:request.firstline.uri.query.[param-name]|A value of a query parameter|
|IP:connection.client.host||
|NUMBER:connection.client.logname||
|STRING:connection.client.user||
|BYTESCLF:response.body.bytes||
|HTTP.URI:request.referer||
|HTTP.USERAGENT:request.user-agent||
|TIME.STAMP:request.receive.time||
|TIME.DAY:request.receive.time.day||
|TIME.MONTHNAME:request.receive.time.monthname||
|TIME.MONTH:request.receive.time.month||
|TIME.YEAR:request.receive.time.year||
|TIME.HOUR:request.receive.time.hour||
|TIME.MINUTE:request.receive.time.minute||
|TIME.SECOND:request.receive.time.second||
|TIME.MILLISECOND:request.receive.time.millisecond||
|TIME.ZONE:request.receive.time.zone||
