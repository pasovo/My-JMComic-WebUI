package com.example.entity;

import jakarta.ws.rs.GET;
import lombok.Getter;

public abstract class BackendBashTask implements Runnable {
    @Getter
    private Long id;
    @Getter
    private Long pid;
    @Getter
    private boolean isRunning;
    @Getter
    private boolean isManualStop;

    public BackendBashTask(){
        id = System.nanoTime();
        isRunning = false;
        isManualStop = false;
    }

    @Override
    public void run() {
        pid = Thread.currentThread().threadId();
        isRunning = true;

        mainFunc();

        isRunning = false;
    }
    // 主方法
    public abstract void mainFunc();

    public void manualStop(){
        isManualStop = true;
        Thread.currentThread().interrupted();
    }
}
