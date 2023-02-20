package org.brick.lib.importflow;

public interface Action<R,T>{
    R run(ActionInfo<T> info);
}
