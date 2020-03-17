package ru.itmo.java;

import java.util.function.Predicate;

public class HashTable {

    private static final int INITIAL_CAPACITY = 1000;
    private static final double LOAD_FACTOR = 0.5;
    private static final double CAPACITY_MULTIPLIER = 2.0;
    private static final int NOT_EXISTS = -1;

    private Entity[] entities;
    private int capacity;
    private int size;
    private double loadFactor;

    public HashTable(int initialCapacity, double loadFactor) {
        this.loadFactor = loadFactor;
        capacity = initialCapacity;
        entities = new Entity[capacity];
    }

    public HashTable(int initialCapacity) {
        this(initialCapacity, LOAD_FACTOR);
    }

    public HashTable() {
        this(INITIAL_CAPACITY, LOAD_FACTOR);
    }

    public Object put(Object key, Object value) {
        int pos = findPos(key);
        if (pos == NOT_EXISTS) {
            pos = findNewPos(key);
        }

        Object valueBefore = null;
        if (entities[pos] != null) {
            valueBefore = entities[pos].getValue();
        }

        entities[pos] = new Entity(key, value);
        if (valueBefore == null) {
            size++;
        }
        ensureCapacity();

        return valueBefore;
    }

    public Object get(Object key) {
        int pos = findPos(key);
        if (pos == NOT_EXISTS) {
            return null;
        }

        return entities[pos];
    }

    public Object remove(Object key) {
        int pos = findPos(key);
        if (pos == NOT_EXISTS) {
            return null;
        }

        Object result = entities[pos].getValue();

        entities[pos] = Entity.TOMBSTONE_ENTITY;
        size--;

        return result;
    }

    public int size() {
        return size;
    }

    private int threshold() {
        return (int) (capacity * loadFactor);
    }

    private int findNewPos(Object key) {
        return findByPredicate(key, entity -> entity == null || entity.isTombstone());
    }

    private int findPos(Object key) {
        int pos = findByPredicate(key, entity ->
                entity == null || !entity.isTombstone() && key.equals(entity.key));

        if (entities[pos] == null) {
            return NOT_EXISTS;
        }

        return pos;
    }

    private int findByPredicate(Object key, Predicate<Entity> searchPredicate) {
        int startIndex = index(key, entities.length);

        for (int i = startIndex; i < entities.length; i++) {
            if (searchPredicate.test(entities[i])) {
                return i;
            }
        }

        for (int i = 0; i < startIndex; i++) {
            if (searchPredicate.test(entities[i])) {
                return i;
            }
        }

        return NOT_EXISTS;
    }

    private void ensureCapacity() {
        if (size >= threshold()) {
            capacity = (int) (capacity * CAPACITY_MULTIPLIER);

            Entity[] oldEntities = entities;
            entities = new Entity[capacity];

            int prevSize = size;

            for (Entity oldEntity : oldEntities) {
                if (oldEntity != null && !oldEntity.isTombstone()) {
                    entities[findNewPos(oldEntity.getKey())] = oldEntity;
                }
            }

            size = prevSize;
        }
    }

    private int index(Object object, int length) {
        return Math.abs(object.hashCode() % length);
    }

    private static class Entity {
        private static final Entity TOMBSTONE_ENTITY = new Entity(null, null);

        private final Object key;
        private final Object value;

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
            return this == TOMBSTONE_ENTITY;
        }
    }
}
