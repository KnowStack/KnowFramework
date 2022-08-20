package com.didiglobal.logi.elasticsearch.client.model.exception;

public class ESIndexTemplateMissingException extends RuntimeException {
    private Throwable t;

    public ESIndexTemplateMissingException(Throwable t) {
        this.t = t;
    }

    @Override
    public String getMessage() {
        return t.getMessage();
    }
    
    public Throwable getT() {
        return t;
    }
    
    public static boolean check(Throwable t) {
        String str = t.getMessage();

        if (str != null && str.contains("index_template_missing_exception")) {
            return true;
        } else {
            return false;
        }
    }
}