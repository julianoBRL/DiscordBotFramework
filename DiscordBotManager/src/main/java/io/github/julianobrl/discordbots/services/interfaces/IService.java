package io.github.julianobrl.discordbots.services.interfaces;

import java.util.List;

public interface IService<T> {
    public List<T> list();
    public List<T> search();
    public T create(T object);
    public T getById(String id);
    public T update(String id, T updated);
    public void delete(String id);
}
