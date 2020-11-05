package com.example.mobileappws.ui.model.response;

public enum ResponseMessages {
    DELETE("Delete was performing successful");
    private String message;

    ResponseMessages(String message) {
        this.message = message;
    }

    public enum ResponseStatuses {
        SUCCESS("Success"),
        ERROR("Error");

        private String message;

        ResponseStatuses(String message) {
            this.message = message;
        }
    }
}

