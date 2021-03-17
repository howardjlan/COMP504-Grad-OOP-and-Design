package edu.rice.comp504.service;

import java.util.List;

public class AGenericService<T> implements IGenericService<T> {

    @Override
    public T get(int id) {
        return null;
    }

    @Override
    public T get(String name) {
        return null;
    }

    @Override
    public List<T> getAll() {
        return null;
    }

}
