package com.web.my.common.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Response<T> {
    @JsonProperty("status")
    private String status;

    @JsonProperty("message")
    private String message;

    @JsonProperty("redirect")
    private String redirect = "";

    @JsonProperty("data")
    private T t;

    public Response(ResponseStatus status) {
        this.status = status.getDescEng();
    }

    public Response(ResponseStatus status, String message) {
        this.status = status.getDescEng();
        this.message = message;
    }

    public Response(ResponseStatus status, String message, T t) {
        this.status = status.getDescEng();
        this.message = message;
        this.t = t;
    }

    public static Response ok() {
        return new Response(ResponseStatus.SUCCESS, "ok");
    }

    public static Response ok(Object object) {
        return new Response(ResponseStatus.SUCCESS, "ok", object);
    }

    public static Response error(String message) {
        return new Response(ResponseStatus.FAILURE, message);
    }

    public static Response none_auth() {
        return new Response(ResponseStatus.NONEAUTH, "noneAuth");
    }


}
