package com.taocoder.pricemonitor.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseInfo<T> {

    private boolean error;

    private String message;

    private T data;

    private List<T> list;

    public ResponseInfo(boolean status) {
        this.error = status;
    }

    public ResponseInfo(boolean status, String message) {
        this.error = status;
        this.message = message;
    }

    public ResponseInfo(boolean status, T data) {
        this.data = data;
    }

    public ResponseInfo(List<T> list) {
        this.list = list;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
