package net.unit8.jackson.dataformat.serverlog.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Bean1 implements Serializable {
    private String abc;
    private String gih;

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

    @Override
    public String toString() {
        return "Bean1{" +
                "abc='" + abc + '\'' +
                ", gih='" + gih + '\'' +
                '}';
    }
}
