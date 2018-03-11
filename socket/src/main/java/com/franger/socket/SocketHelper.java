package com.franger.socket;

/**
 * Created by Vignesh Ramachandra.
 * <p/>
 * Contains the errors related to a Socket.
 */
public enum SocketHelper {

    MALFORMED_URI(0, "Cannot establish connection at the given address"),

    SOCKET_ALREADY_EXISTS(1, "Socket with TAG Already Exists"),

    SOCKET_NOT_FOUND(2, "Cannot find the socket with the given TAG"),

    CANNOT_CREATE_RETRIES_EXCEEDED(3, "Maximum retries reached"),

    UNKNOWN_ERROR_WHILE_CONNECTING(4, "Failed to connect");

    private int code;

    private String message;

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    SocketHelper(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
