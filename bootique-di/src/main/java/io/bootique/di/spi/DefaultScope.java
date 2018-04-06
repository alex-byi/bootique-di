package io.bootique.di.spi;

import io.bootique.di.BeforeScopeEnd;
import io.bootique.di.Scope;

import javax.inject.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

/**
 * An implementation of a DI scopes with support scope events.
 */
public class DefaultScope implements Scope {

    private static final String SPECIAL_EVENT = AfterScopeEnd.class.getName();
    protected Collection<Class<? extends Annotation>> eventTypes;
    protected ConcurrentMap<String, Collection<ScopeEventBinding>> listeners;

    @SafeVarargs
    public DefaultScope(Class<? extends Annotation>... customEventTypes) {
        this.listeners = new ConcurrentHashMap<>();
        this.eventTypes = new HashSet<>();

        // initialize the event listener data structures in constructor to avoid
        // synchronization concerns on everything but per-event lists.

        // standard event types
        eventTypes.add(BeforeScopeEnd.class);
        eventTypes.add(AfterScopeEnd.class);

        // custom event types
        if (customEventTypes != null) {
            Collections.addAll(eventTypes, customEventTypes);
        }

        for (Class<? extends Annotation> type : eventTypes) {
            listeners.put(type.getName(), new ConcurrentLinkedQueue<>());
        }
    }

    /**
     * Shuts down this scope, posting {@link BeforeScopeEnd} and {@link AfterScopeEnd}
     * events.
     */
    public void shutdown() {
        postScopeEvent(BeforeScopeEnd.class);

        // this will notify providers that they should reset their state and unregister
        // object event listeners that just went out of scope
        postScopeEvent(AfterScopeEnd.class);
    }

    /**
     * Registers annotated methods of an arbitrary object for this scope lifecycle events.
     */
    public void addScopeEventListener(Object object) {

        // TODO: cache metadata for non-singletons scopes for performance

        // 'getMethods' grabs public method from the class and its superclasses...
        for (Method method : object.getClass().getMethods()) {

            for (Class<? extends Annotation> annotationType : eventTypes) {

                if (method.isAnnotationPresent(annotationType)) {
                    String typeName = annotationType.getName();

                    Collection<ScopeEventBinding> eventListeners = listeners.get(typeName);
                    eventListeners.add(new ScopeEventBinding(object, method));
                }
            }
        }
    }

    public void removeScopeEventListener(Object object) {

        // TODO: 2 level-deep full scan will not be very efficient for short scopes. Right
        // now this would only affect the unit test scope, but if we start creating the
        // likes of HTTP request scope, we may need to create a faster listener
        // removal algorithm.

        for (Entry<String, Collection<ScopeEventBinding>> entry : listeners.entrySet()) {

            if (SPECIAL_EVENT.equals(entry.getKey())) {
                // no scanning and removal of Scope providers ...
                // for faster scan skip those
                continue;
            }

            entry.getValue().removeIf(binding -> binding.getObject() == object);
        }
    }

    /**
     * Posts a scope event to all registered listeners. There's no predetermined order of
     * event dispatching. An exception thrown by any of the listeners stops further event
     * processing and is rethrown.
     */
    public void postScopeEvent(Class<? extends Annotation> type, Object... eventParameters) {

        Collection<ScopeEventBinding> eventListeners = listeners.get(type.getName());

        if (eventListeners != null) {
            // remove listeners that were garbage collected
            eventListeners.removeIf(listener -> !listener.onScopeEvent(eventParameters));
        }
    }

    @Override
    public <T> Provider<T> scope(Provider<T> unscoped) {
        return new DefaultScopeProvider<>(this, unscoped);
    }
}
