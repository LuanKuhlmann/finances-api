package io.github.luankuhlmann.myfinances.exception;

public class AuthenticationError extends RuntimeException{

    public AuthenticationError(String msg) {
        super(msg);
    }
}
