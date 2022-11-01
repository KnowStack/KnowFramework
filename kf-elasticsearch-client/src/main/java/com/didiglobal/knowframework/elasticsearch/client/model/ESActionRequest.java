package com.didiglobal.knowframework.elasticsearch.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.rest.RestStatus;

public abstract class ESActionRequest<T extends ESActionRequest> extends ActionRequest {
    protected List<String> filter_path;
    public ESActionRequest() {
        super();
    }

    protected ESActionRequest(ESActionRequest request) {
        super(request);
    }
    
    public ESActionRequest filterPath(List<String> filter_path) {
        this.filter_path = filter_path;
        return this;
    }
    
    public List<String> getFilter_path() {
        return filter_path;
    }
    
    abstract public RestRequest toRequest() throws Exception;

    public RestRequest buildRequest(List<Header> headers) throws Exception {
        RestRequest restRequest = toRequest();
        List<Header> newHeaders = new ArrayList<>();
        newHeaders.addAll(headers);
        if (this.headers != null) {
            for (Map.Entry<String, Object> entry : this.headers.entrySet()) {
                Header header = new BasicHeader(entry.getKey(), entry.getValue().toString());
                newHeaders.add(header);
            }
        }

        restRequest.setHeaders(newHeaders);
        return restRequest;
    }

    abstract public ESActionResponse toResponse(RestResponse response) throws Exception;

    public boolean checkResponse(org.elasticsearch.client.Response response) {
        int status = response.getStatusLine().getStatusCode();

        if (status == 200 || status == 202 || status == 201) {
            return true;
        } else {
            return false;
        }
    }

    public ESActionResponse buildResponse(RestResponse response) throws Exception {
        ESActionResponse esActionResponse = toResponse(response);
        for (RestStatus restStatus : RestStatus.values()) {
            if (restStatus.getStatus() == response.getStatusCode()) {
                esActionResponse.setRestStatus(restStatus);
            }
        }
        return esActionResponse;
    }

}