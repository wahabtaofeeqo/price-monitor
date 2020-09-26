package com.taocoder.pricemonitor.models;

import java.util.List;

public class ApprovalsResult {

    private boolean error;
    private String message;
    private List<Approval> approvals;

    public ApprovalsResult(boolean status) {
        this.error = status;
    }

    public ApprovalsResult(boolean status, String message) {
        this.error = status;
        this.message = message;
    }

    public ApprovalsResult(boolean status, List<Approval> approvals) {
        this.error = status;
        this.approvals = approvals;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Approval> getApprovals() {
        return approvals;
    }

    public void setApprovals(List<Approval> approvals) {
        this.approvals = approvals;
    }
}
