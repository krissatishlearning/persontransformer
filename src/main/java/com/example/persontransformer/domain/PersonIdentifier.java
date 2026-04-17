package com.example.persontransformer.domain;

public class PersonIdentifier {

    private String value;
    private boolean isValid = false;

    public PersonIdentifier() {
    }

    public PersonIdentifier(String value, boolean isValid) {
        this.value = value;
        this.isValid = isValid;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }
}
