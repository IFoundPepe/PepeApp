package com.pepedyne.pepe.dispatch;

public interface PepeRunningTaskMacro {
    public void execute(PepeDispatcher dispatcher);
    public boolean isRepeatingTask();
}
