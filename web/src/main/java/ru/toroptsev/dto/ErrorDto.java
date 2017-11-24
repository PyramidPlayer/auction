package ru.toroptsev.dto;

import java.io.Serializable;

/**
 * Data transfer object for error message
 */
public class ErrorDto implements Serializable {

    /**
     * Error message
     */
    private String error;

    public ErrorDto() {}

    public ErrorDto(String error) {
        this.error = error;
    }

    public ErrorDto(Exception e) {
        this.error = e.getMessage();
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
