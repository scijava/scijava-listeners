package org.scijava.listeners;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A variable of type {@code T}. Accessible via {@link #get()} /
 * {@link #set(Object)}. Listeners of type {@code L} are notified when the
 * variable is changed (or is set, alternatively).
 *
 * @param <T>
 *            value type
 * @param <L>
 *            listener type
 *
 * @author Tobias Pietzsch
 */
public class DefaultListenableVar< T, L > implements ListenableVar< T, L >
{
	private final AtomicReference< T > ref;

	private final Listeners.List< L > listeners;

	private final BiConsumer< L, T > notify;

	private final boolean notifyWhenSet;

	public DefaultListenableVar( final T value, final Consumer< L > notify )
	{
		this( value, notify, false );
	}

	public DefaultListenableVar( final T value, final BiConsumer< L, T > notify )
	{
		this( value, notify, false );
	}

	public DefaultListenableVar( final T value, final Consumer< L > notify, final boolean notifyWhenSet )
	{
		this( value, ( l, t ) -> notify.accept( l ), notifyWhenSet );
	}

	public DefaultListenableVar( final T value, final BiConsumer< L, T > notify, final boolean notifyWhenSet )
	{
		this.ref = new AtomicReference<>( value );
		this.notify = notify;
		this.notifyWhenSet = notifyWhenSet;
		this.listeners = new Listeners.SynchronizedList<>();
	}

	@Override
	public void set( final T value )
	{
		final T previous = this.ref.getAndSet( value );
		if ( notifyWhenSet || !previous.equals( value ) )
			listeners.list.forEach( l -> notify.accept( l, value ) );
	}

	@Override
	public T get()
	{
		return ref.get();
	}

	@Override
	public Listeners< L > listeners()
	{
		return listeners;
	}
}
