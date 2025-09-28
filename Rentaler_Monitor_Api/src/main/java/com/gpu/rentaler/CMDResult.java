package com.gpu.rentaler;

import java.io.Serializable;

public class CMDResult implements Serializable {

    String output;

    String error;

    int exitCode;

    public CMDResult(String output, String error, int exitCode) {
        this.output = output;
        this.error = error;
        this.exitCode = exitCode;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getExitCode() {
        return exitCode;
    }

    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }
}
