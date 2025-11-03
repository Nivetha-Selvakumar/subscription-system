package com.subscription.subscription_system.validator;

import java.util.List;

public class RequestValidationItem {

    List<RequestComponent> body;
    List<RequestComponent> header;
    List<RequestComponent> pathVariable;
    List<RequestComponent> queryParam;

    public RequestValidationItem() {
        super();
    }

    public List<RequestComponent> getBody() {
        return body;
    }

    public void setBody(List<RequestComponent> body) {
        this.body = body;
    }

    public List<RequestComponent> getHeader() {
        return header;
    }

    public void setHeader(List<RequestComponent> header) {
        this.header = header;
    }

    public List<RequestComponent> getPathVariable() {
        return pathVariable;
    }

    public void setPathVariable(List<RequestComponent> pathVariable) {
        this.pathVariable = pathVariable;
    }

    public List<RequestComponent> getQueryParam() {
        return queryParam;
    }

    public void setQueryParam(List<RequestComponent> queryParam) {
        this.queryParam = queryParam;
    }
}
