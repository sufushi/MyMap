package com.rdc.mymap.model;

public class StepInfo {

    private String mInstructions;
    private int mDirection;

    public StepInfo(String instructions, int direction) {
        this.mInstructions = instructions;
        this.mDirection = direction;
    }

    public String getInstructions() {
        return mInstructions;
    }

    public void setInstructions(String instructions) {
        this.mInstructions = mInstructions;
    }

    public int getDirection() {
        return mDirection;
    }

    public void setDirection(int direction) {
        this.mDirection = mDirection;
    }
}
