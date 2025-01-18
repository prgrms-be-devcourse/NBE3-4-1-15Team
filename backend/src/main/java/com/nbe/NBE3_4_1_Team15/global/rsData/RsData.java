package com.nbe.NBE3_4_1_Team15.global.rsData;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@Getter
public class RsData<T> {
    private String resultCode;
    private String msg;
    private T data;

    public RsData(String resultCode, String msg) {
        this.resultCode = resultCode;
        this.msg = msg;
        this.data = null;
    }

    @JsonIgnore
    public int getStatusCode() {
        return Integer.parseInt(resultCode.split("_")[0]);
    }
}
