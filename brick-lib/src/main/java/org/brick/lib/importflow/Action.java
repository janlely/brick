package org.brick.lib.importflow;

public interface Action<R>{
    R run(ActionInfo info);
}
