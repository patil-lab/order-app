package com.prive.ordering.util;

import java.util.UUID;

public final class ConstUtil {
    public static final String INSTANCE_ID = getUUID();

    public static String getUUID() {
        return UUID.randomUUID().toString();
    }
}
