package com.equix.ordersimulator.infras.persistence;

import com.equix.ordersimulator.domain.repository.SymbolRepository;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

@Repository
public class SymbolRepositoryImpl implements SymbolRepository {
    private static final Set<String> allSymbols = Set.of("FPT", "VIC", "VCB", "VNINDEX");

    @Override
    public Set<String> getAllSymbols() {
        return new HashSet<>(allSymbols);
    }
}
