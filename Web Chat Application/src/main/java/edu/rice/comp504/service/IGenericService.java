package edu.rice.comp504.service;

import java.util.List;

public interface IGenericService<T> {

    T get(int id);

    T get(String name);

    List<T> getAll();

}
