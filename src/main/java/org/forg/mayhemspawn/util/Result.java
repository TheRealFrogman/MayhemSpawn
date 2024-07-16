package org.forg.mayhemspawn.util;

public class Result<T> {
    private boolean success;
    private T value;
    private String errorMessage;

    private Result(boolean success, T value, String errorMessage) {
        this.success = success;
        this.value = value;
        this.errorMessage = errorMessage;
    }

    public static <T> Result<T> success(T value) {
        return new Result<>(true, value, null);
    }

    public static <T> Result<T> failure(String errorMessage) {
        return new Result<>(false, null, errorMessage);
    }

    public boolean isSuccess() {
        return success;
    }

    public T getValue() {
        if (!success) {
            throw new IllegalStateException("Result is not successful.");
        }
        return value;
    }

    public String getErrorMessage() {
        if (success) {
            throw new IllegalStateException("Result is successful.");
        }
        return errorMessage;
    }
}
