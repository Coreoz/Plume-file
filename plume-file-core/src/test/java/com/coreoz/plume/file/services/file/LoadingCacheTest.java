package com.coreoz.plume.file.services.file;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;

@SuppressWarnings("rawtypes")
public class LoadingCacheTest<K, V> implements LoadingCache {

	private final Function<K, V> loadingFunction;

	public LoadingCacheTest(Function<K, V> loadingFunction) {
		this.loadingFunction = loadingFunction;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getIfPresent(Object key) {
		return loadingFunction.apply((K) key);
	}


	@Override
	public Object get(Object key) throws ExecutionException {
		return getIfPresent(key);
	}

	// unused

	@Override
	public Object get(Object key, Callable valueLoader) throws ExecutionException {
		return null;
	}

	@Override
	public ImmutableMap getAllPresent(Iterable keys) {
		return null;
	}

	@Override
	public void put(Object key, Object value) {

	}

	@Override
	public void putAll(Map m) {
	}

	@Override
	public void invalidate(Object key) {
	}

	@Override
	public void invalidateAll(Iterable keys) {
	}

	@Override
	public void invalidateAll() {
	}

	@Override
	public long size() {
		return 0;
	}

	@Override
	public CacheStats stats() {
		return null;
	}

	@Override
	public void cleanUp() {
	}

	@Override
	public Object getUnchecked(Object key) {
		return null;
	}

	@Override
	public ImmutableMap getAll(Iterable keys) throws ExecutionException {
		return null;
	}

	@Override
	public Object apply(Object key) {
		return null;
	}

	@Override
	public void refresh(Object key) {
	}

	@Override
	public ConcurrentMap asMap() {
		return null;
	}

}
