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
