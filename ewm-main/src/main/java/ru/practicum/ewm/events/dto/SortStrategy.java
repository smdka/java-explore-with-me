package ru.practicum.ewm.events.dto;

import java.util.Collection;

@FunctionalInterface
public interface SortStrategy<T> {
    Collection<T> sort(Collection<T> collection);
}
