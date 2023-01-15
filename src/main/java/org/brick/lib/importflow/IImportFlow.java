package org.brick.lib.importflow;

import org.brick.lib.IFlow;

import java.util.List;

public interface IImportFlow<T,O,C> extends IFlow<byte[],O,C> {

    List<T> parseData(byte[] data);

}
