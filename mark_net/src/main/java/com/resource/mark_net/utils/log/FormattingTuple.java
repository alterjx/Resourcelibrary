/**
 * Copyright (C) 2006-2014 Tuniu All rights reserved
 */
package com.resource.mark_net.utils.log;

public class FormattingTuple {

    private String message;
    private Throwable throwable;

    public FormattingTuple(String message) {
        this(message, null);
    }

    public FormattingTuple(String message, Throwable throwable) {
        this.message = message;
        this.throwable = throwable;
    }

    public String getMessage() {
        return message;
    }

    public Throwable getThrowable() {
        return throwable;
    }

}