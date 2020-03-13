package ru.itmo.java;

import java.util.function.Predicate;

public class HashTable {

    private static final int INITIAL_CAPACITY = 1000;
    private static final double LOAD_FACTOR = 0.5;
    private static final double CAPACITY_MULTIPLIER = 2.0;
    private static final int NOT_EXISTS = -1;

    private Entity[] mEntities;
    private int mCapacity;
    private int mSize;
    private double mLoadFactor;

    HashTable(int initialCapacity, double loadFactor) {
        mLoadFactor = loadFactor;
        mCapacity = initialCapacity;
        mEntities = new Entity[mCapacity];
    }

    HashTable(int initialCapacity) {
        mLoadFactor = LOAD_FACTOR;
        mCapacity = initialCapacity;
        mEntities = new Entity[mCapacity];
    }

    HashTable() {
        mLoadFactor = LOAD_FACTOR;
        mCapacity = INITIAL_CAPACITY;
        mEntities = new Entity[mCapacity];
    }

    Object put(Object key, Object value) {
        int pos = findPos(key);
        if (pos == NOT_EXISTS) {
            pos = findNewPos(key);
        }

        Object valueBefore = null;
        if (mEntities[pos] != null) {
            valueBefore = mEntities[pos].getValue();
        }

        mEntities[pos] = new Entity(key, value);
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

        Entity entity = mEntities[pos];

        return entity.getValue();
    }

    Object remove(Object key) {
        int pos = findPos(key);
        if (pos == NOT_EXISTS) {
            return null;
        }

        Object result = mEntities[pos].getValue();

        mEntities[pos] = Entity.createTombstone();
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
        return find(key, entity -> entity == null || entity.isTombstone());
    }

    private int findPos(Object key) {
        int pos = find(key, entity ->
                entity == null || !entity.isTombstone() && key.equals(entity.key));

        if (mEntities[pos] == null) {
            return NOT_EXISTS;
        }

        return pos;
    }

    private int find(Object key, Predicate<Entity> searchPredicate) {
        int hash = index(key, mEntities.length);

        for (int i = hash; i < mEntities.length; i++) {
            if (searchPredicate.test(mEntities[i])) {
                return i;
            }
        }

        for (int i = 0; i < hash; i++) {
            if (searchPredicate.test(mEntities[i])) {
                return i;
            }
        }

        return NOT_EXISTS;
    }

    private void updateCapacity() {
        if (mSize >= threshold()) {
            mCapacity = (int) (mCapacity * CAPACITY_MULTIPLIER);

            Entity[] oldEntities = mEntities;
            mEntities = new Entity[mCapacity];

            int prevSize = mSize;

            for (Entity oldEntity : oldEntities) {
                if (oldEntity != null && !oldEntity.isTombstone()) {
                    mEntities[findNewPos(oldEntity.getKey())] =
                            new Entity(oldEntity.getKey(), oldEntity.getValue());
                }
            }

            mSize = prevSize;
        }
    }

    private int index(Object object, int length) {
        return Math.abs(object.hashCode() % length);
    }

    private static class Entity {
        private final Object key;
        private final Object value;

        public static Entity createTombstone() {
            return new Entity();
        }

        private Entity() {
            key = null;
            value = null;
        }

        public Entity(Object key, Object value) {
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
            return key == null;
        }
    }

}
