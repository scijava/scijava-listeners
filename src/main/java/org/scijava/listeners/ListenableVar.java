package org.scijava.listeners;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A variable of type {@code T}. Accessible via {@link #get()} /
 * {@link #set(Object)}. Listeners of type {@code L} are notified when the
 * variable is changed (or is set, alternatively).
 *
 * @author Tobias Pietzsch
 */
public class ListenableVar< T, L >
{
	private final AtomicReference< T > ref;

	private final Listeners.List< L > listeners;

	private final BiConsumer< L, T > notify;

	private final boolean notifyWhenSet;

	public ListenableVar( final T value, final Consumer< L > notify )
	{
		this( value, notify, false );
	}

	public ListenableVar( final T value, final BiConsumer< L, T > notify )
	{
		this( value, notify, false );
	}

	public ListenableVar( final T value, final Consumer< L > notify, final boolean notifyWhenSet )
	{
		this( value, ( l, t ) -> notify.accept( l ), notifyWhenSet );
	}

	public ListenableVar( final T value, final BiConsumer< L, T > notify, final boolean notifyWhenSet )
	{
		this.ref = new AtomicReference<>( value );
		this.notify = notify;
		this.notifyWhenSet = notifyWhenSet;
		this.listeners = new Listeners.SynchronizedList<>();
	}

	public void set( final T value )
	{
		final T previous = this.ref.getAndSet( value );
		if ( notifyWhenSet || !previous.equals( value ) )
			listeners.list.forEach( l -> notify.accept( l, value ) );
	}

	public T get()
	{
		return ref.get();
	}

	public Listeners< L > listeners()
	{
		return listeners;
	}

	public static < T > ListenableVar< T, Runnable > simple( final T value )
	{
		return new ListenableVar<>( value, Runnable::run );
	}

	public static < T > ListenableVar< T, Runnable > simple( final T value, final boolean notifyWhenSet )
	{
		return new ListenableVar<>( value, Runnable::run, notifyWhenSet );
	}
}
