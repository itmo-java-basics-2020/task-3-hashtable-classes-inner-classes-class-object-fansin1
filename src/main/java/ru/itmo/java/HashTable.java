package ru.itmo.java;

public class HashTable {

    private static final int INITIAL_CAPACITY = 1000;
    private static final double LOAD_FACTOR = 0.5;
    private static final double CAPACITY_MULTIPLIER = 2.0;
    private static final int NOT_EXISTS = -1;

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
        if (pos == NOT_EXISTS) {
            pos = findNewPos(key);
        }

        Object valueBefore = null;
        if (mPairs[pos] != null) {
            valueBefore = mPairs[pos].getValue();
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
        if (pos == NOT_EXISTS) {
            return null;
        }

        Pair pair = mPairs[findPos(key)];

        return pair.getValue();
    }

    Object remove(Object key) {
        int pos = findPos(key);
        if (pos == NOT_EXISTS) {
            return null;
        }

        Object result = mPairs[pos].getValue();

        mPairs[pos] = Pair.createTombstone();
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
        int hash = index(key, mPairs.length);

        for (int i = hash; i < mPairs.length; i++) {
            if (mPairs[i] == null || mPairs[i].isTombstone()) {
                return i;
            }
        }

        for (int i = 0; i < hash; i++) {
            if (mPairs[i] == null || mPairs[i].isTombstone()) {
                return i;
            }
        }

        return NOT_EXISTS;
    }

    private int findPos(Object key) {
        int hash = index(key, mPairs.length);

        for (int i = hash; i < mPairs.length; i++) {
            if (mPairs[i] == null) {
                return NOT_EXISTS;
            }

            if (!mPairs[i].isTombstone() && mPairs[i].key.equals(key)) {
                return i;
            }
        }

        for (int i = 0; i < hash; i++) {
            if (mPairs[i] == null) {
                return NOT_EXISTS;
            }

            if (!mPairs[i].isTombstone() && mPairs[i].key.equals(key)) {
                return i;
            }
        }

        return NOT_EXISTS;
    }

    private void updateCapacity() {
        if (mSize >= threshold()) {
            mCapacity = (int) (mCapacity * CAPACITY_MULTIPLIER);

            Pair[] oldPairs = mPairs;
            mPairs = new Pair[mCapacity];

            int prevSize = mSize;

            for (Pair oldPair : oldPairs) {
                if (oldPair != null && !oldPair.isTombstone()) {
                    mPairs[findNewPos(oldPair.getKey())] =
                            new Pair(oldPair.getKey(), oldPair.getValue());
                }
            }

            mSize = prevSize;
        }
    }

    private int index(Object object, int length) {
        return Math.abs(object.hashCode() % length);
    }

    private static class Pair {
        private final Object key;
        private final Object value;
        private boolean isTombstone;

        public static Pair createTombstone() {
            return new Pair();
        }

        private Pair() {
            key = null;
            value = null;
            isTombstone = true;
        }

        public Pair(Object key, Object value) {
            this.key = key;
            this.value = value;
        }

        public Object getKey() {
            return key;
        }

        public Object getValue() {
            return value;
        }

        public boolean isTombstone() {
            return isTombstone;
        }
    }

}
