package com.ramy.rec.core;

import java.util.List;

public interface DataSource<T> {
    List<T> getData();
}