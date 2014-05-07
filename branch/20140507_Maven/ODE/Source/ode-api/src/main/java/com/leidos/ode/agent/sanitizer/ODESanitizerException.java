package com.leidos.ode.agent.sanitizer;

public class ODESanitizerException extends Exception {

    private static final long serialVersionUID = 1L;

    public ODESanitizerException() {
        super();
    }

    public ODESanitizerException(String message) {
        super(message);
    }

    public ODESanitizerException(String message, Throwable throwable) {
        super(message, throwable);
    }

}