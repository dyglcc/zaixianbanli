/*
 * Copyright (C) 2013 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.adhoc.http.internal;

import com.adhoc.utils.T;

/**
 * Runnable implementation which always sets its thread name.
 */
public abstract class NamedRunnable implements Runnable {
    protected final String name;

    public NamedRunnable(String format, Object... args) {
        this.name = String.format(format, args);
    }

    @Override
    public final void run() {
        String oldName = null;
        try {
            oldName = Thread.currentThread().getName();
            Thread.currentThread().setName(name);
            execute();
        } catch (Throwable e) {
            T.e(e);
        } finally {
            Thread.currentThread().setName(oldName == null ? System.currentTimeMillis() + "" : oldName);
        }
    }

    protected abstract void execute();
}
