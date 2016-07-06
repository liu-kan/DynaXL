package org.liukan.DynaXL.io;

import java.util.Map;

/**
 * Created by liuk on 16-7-6.
 */
public class PdbWrapper {
    private rwPDB  data;
    private String name;

    public PdbWrapper(String name, rwPDB data) {
        this.name = name;
        this.data = data;
    }

    public rwPDB getData() {
        return data;
    }

    public String getName() {
        return name;
    }
}
