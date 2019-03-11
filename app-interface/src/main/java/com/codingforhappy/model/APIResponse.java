package com.codingforhappy.model;

import com.codingforhappy.sms.service.SMSResponese;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class APIResponse<T> {

    private String event;    // 返回码，1为成功
    private String msg;      // 返回信息
    private T obj;           // 单个对象
    private T objList;       // 数组对象
    private Integer currentPage; // 当前页数
    private Integer pageSize;    // 每页显示数量
    private Integer maxCount;    // 总条数
    private Integer maxPage;     // 总页数

    // 构造函数，初始化code和msg
    public APIResponse(String event, String msg) {
        this.event = event;
        this.msg = msg;
    }

    public APIResponse() {
    }

    public static APIResponse fromSMSResponse(SMSResponese response) {
        return new APIResponse(response.getEvent(), response.getMessage());
    }

    // Getter 和 Setter 方法
    public String getEvent() {
        return event;
    }

    public APIResponse setEvent(String event) {
        this.event = event;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public APIResponse setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public T getObj() {
        return obj;
    }

    public APIResponse setObj(T obj) {
        this.obj = obj;
        return this;
    }

    public T getObjList() {
        return objList;
    }

    public APIResponse setObjList(T objList) {
        this.objList = objList;
        return this;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public APIResponse setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
        return this;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public APIResponse setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public Integer getMaxCount() {
        return maxCount;
    }

    public APIResponse setMaxCount(int maxCount) {
        this.maxCount = maxCount;
        return this;
    }

    public Integer getMaxPage() {
        return maxPage;
    }

    public APIResponse setMaxPage(int maxPage) {
        this.maxPage = maxPage;
        return this;
    }

    public interface Event {
        String OK = "200";
        String WRONG_FORMAT = "701";
        String USER_HAS_EXISTED = "702";
        String INVALID_REQUEST = "400";
        String UNAUTHORIZED = "401";
        String NOT_FOUND = "404";

        String UNSUBSCRIBED = "450";
        String PUSH_FAILED = "451";

        String NOTIMPLEMENTED = "501";
    }

    public interface Message {
        String OK = "OK!";
        String WRONG_FORMAT = "The data format is wrong!";
        String USER_HAS_EXISTED = "User has existed!";
        String INVALID_REQUEST = "Invalid request";
        String NOT_MATCH = "Unauthorized, phone number and password is not matched";
        String UNAUTHORIZED = "Unauthorized.";
        String POSITION_NOT_FOUND = "Position not found.";
        String COUNTERPART_NOT_FOUND = "Counterpart not found.";
        String NOT_FOUND = "not found.";
        String UNSUBSCRIBED = "push service unsubscribed.";
        String PUSH_FAILED = "push failed.";
        String NOTIMPLEMENTED = "unhandled.";
    }
}
