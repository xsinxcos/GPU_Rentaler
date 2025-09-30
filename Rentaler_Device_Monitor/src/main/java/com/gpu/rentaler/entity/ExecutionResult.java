package com.gpu.rentaler.entity;

public class ExecutionResult {
    private final int exitValue;
    private final String output;
    private final String error;

    public ExecutionResult(int exitValue, String output, String error) {
        this.exitValue = exitValue;
        this.output = output;
        this.error = error;
    }

    public int getExitValue() {
        return exitValue;
    }

    public String getOutput() {
        return output;
    }

    public String getError() {
        return error;
    }

    public boolean isSuccess() {
        return exitValue == 0;
    }

    @Override
    public String toString() {
        return "Exit Value: " + exitValue + "\n" +
               "Output: " + output + "\n" +
               "Error: " + error;
    }
}
