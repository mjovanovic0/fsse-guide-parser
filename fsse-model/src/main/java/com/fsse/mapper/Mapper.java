package com.fsse.mapper;

import java.util.List;
import java.util.stream.Collectors;

public interface Mapper<S, D> {

    D from(S source);

    default List<D> from(final List<S> sources) {
        return sources.stream().map(this::from).collect(Collectors.toList());
    }

}
