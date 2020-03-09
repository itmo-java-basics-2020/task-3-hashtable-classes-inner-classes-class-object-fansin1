package ru.itmo.java;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;

public class HashTable {

    private static final int INITIAL_CAPACITY = 1000;
    private static final double LOAD_FACTOR = 0.5;

    private Pair[] mPairs;
    private int mCapacity;
    private int mSize;
    private double mLoadFactor;

    HashTable(int initialCapacity, double loadFactor) {
        mLoadFactor = loadFactor;
        mCapacity = initialCapacity;
        mPairs = new Pair[mCapacity];
    }

    HashTable(int initialCapacity) {
        mLoadFactor = LOAD_FACTOR;
        mCapacity = initialCapacity;
        mPairs = new Pair[mCapacity];
    }

    HashTable() {
        mLoadFactor = LOAD_FACTOR;
        mCapacity = INITIAL_CAPACITY;
        mPairs = new Pair[mCapacity];
    }

    Object put(Object key, Object value) {
        int pos = findPos(key);
        if (pos == -1) {
            pos = findNewPos(key);
        }

        Object valueBefore = null;
        if (mPairs[pos] != null) {
            valueBefore = mPairs[pos].value;
        }

        mPairs[pos] = new Pair(key, value);
        if (valueBefore == null) {
            mSize++;
        }
        updateCapacity();

        return valueBefore;
    }

    Object get(Object key) {
        int pos = findPos(key);
        if (pos == -1) {
            return null;
        }

        Pair pair = mPairs[findPos(key)];
        return pair.value;
    }

    Object remove(Object key) {
        int pos = findPos(key);
        if (pos == -1) {
            return null;
        }

        Object result = mPairs[pos].value;

        mPairs[pos] = null;
        mSize--;

        return result;
    }

    int size() {
        return mSize;
    }

    private int threshold() {
        return (int) (mCapacity * mLoadFactor);
    }

    private int findNewPos(Object key) {
        return find(key.hashCode(), Objects::isNull);
    }

    private int findPos(Object key) {
        return find(key.hashCode(), pair -> {
            if (pair != null) {
                return key.equals(pair.key);
            } else {
                return false;
            }
        });
    }

    private int find(int hashCode, Predicate<Pair> comparator) {
        for (int i = Math.abs(hashCode) % mPairs.length; i < mPairs.length; i++) {
            if (comparator.test(mPairs[i])) {
                return i;
            }
        }

        for (int i = 0; i < mPairs.length; i++) {
            if (comparator.test(mPairs[i])) {
                return i;
            }
        }

        return -1;
    }

    private void updateCapacity() {
        if (mSize >= threshold()) {
            mPairs = Arrays.copyOf(mPairs, mCapacity * 2);
            mCapacity = mCapacity * 2;
        }
    }

    private static class Pair {
        Object key;
        Object value;

        public Pair(Object key, Object value) {
            this.key = key;
            this.value = value;
        }
    }

}
