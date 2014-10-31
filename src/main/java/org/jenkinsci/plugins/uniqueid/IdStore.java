package org.jenkinsci.plugins.uniqueid;

import hudson.ExtensionPoint;
import jenkins.model.Jenkins;

import javax.annotation.Nullable;

/**
 * An abstraction to persistently store and retrieve unique id's
 * for various Jenkins model objects.
 *
 * These keys are guaranteed to be unique with a Jenkins
 * and immutable across the lifetime of the given object.
 *
 * @param <T>
 */
public abstract class IdStore<T> implements ExtensionPoint {

    private final Class<T> type;

    public IdStore (Class<T> forType) {
        this.type = forType;
    }

    /**
     * Creates an unique id for the given object.
     * Subsequent calls are idempotent.
     *
     * @param object
     */
    public abstract void make(T object);

    /**
     * Get the id for this given object.
     * @param object
     * @return the id or null if none assigned.
     */
    @Nullable
    public abstract String get(T object);

    public boolean supports(Class clazz) {
        return type.isAssignableFrom(clazz);
    }

    /**
     * Retrieve an {@link IdStore} for the given type
     * @param clazz
     * @param <C>
     * @return the store which supports the type, or null if none
     */
    @Nullable
    public static <C> IdStore<C> forClass(Class<C> clazz) {
        for (IdStore store : Jenkins.getInstance().getExtensionList(IdStore.class)) {
            if (store.supports(clazz)) {
                return store;
            }
        }
        return null;
    }

    /**
     * Convenience method which makes the id for the given object.
     *
     * @throws java.lang.IllegalArgumentException if the type is not supported.
     */
    public static void makeId(Object object) {
        IdStore store = forClass(object.getClass());
        if (store == null) {
            throw new IllegalArgumentException("Unsupported type: " + object.getClass().getName());
        } else {
            store.make(object);
        }
    }

    /**
     * Convenience method which retrieves the id for the given object.
     *
     * @throws java.lang.IllegalArgumentException if the type is not supported.
     */
    public static String getId(Object object) {
        IdStore store = forClass(object.getClass());
        if (store == null) {
            throw new IllegalArgumentException("Unsupported type: " + object.getClass().getName());
        } else {
            return store.get(object);
        }
    }

}
