package net.unit8.jackson.dataformat.serverlog.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import nl.basjes.parse.core.Field;

import java.io.Serializable;
import java.util.Date;

public class Bean1 implements Serializable {
    private String abc;
    private String gih;
    private Date date;
    private Long hour;

    @JsonProperty("STRING:request.firstline.uri.query.abc")
    public String getAbc() {
        return abc;
    }

    public void setAbc(String abc) {
        this.abc = abc;
    }

    @JsonProperty("STRING:request.firstline.uri.query.hij")
    public String getGih() {
        return gih;
    }

    public void setGih(String gih) {
        this.gih = gih;
    }

    @JsonProperty("TIME.STAMP:request.receive.time")
    @JsonFormat(locale = "en", shape = JsonFormat.Shape.STRING, pattern = "dd/MMM/yyyy:hh:mm:ss zzz")
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @JsonProperty("TIME.HOUR:request.receive.time.hour")
    public Long getHour() {
        return hour;
    }

    public void setHour(Long hour) {
        this.hour = hour;
    }

    @Override
    public String toString() {
        return "Bean1{" +
                "abc='" + abc + '\'' +
                ", gih='" + gih + '\'' +
                ", date=" + date +
                ", hour=" + hour +
                '}';
    }
}
