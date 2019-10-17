package com.highgreat.sven.hgglide;

import java.io.File;

public interface ModelTypes<T> {
    T load(String string);

    T load(File file);
}
