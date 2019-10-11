package com.pepedyne.pepe.dispatch;

public interface PepeRunningTaskMacro {
    void execute(PepeDispatcher dispatcher);
    boolean isRepeatingTask();
}
