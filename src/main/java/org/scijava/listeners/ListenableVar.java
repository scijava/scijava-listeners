package org.scijava.listeners;

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
public interface ListenableVar< T, L >
{
	T get();

	void set( T value );

	Listeners< L > listeners();

	public static < T > ListenableVar< T, ChangeListener > create( final T value )
	{
		return new DefaultListenableVar<>( value, ChangeListener::valueChanged );
	}

	public static < T > ListenableVar< T, ChangeListener > create( final T value, final boolean notifyWhenSet )
	{
		return new DefaultListenableVar<>( value, ChangeListener::valueChanged, notifyWhenSet );
	}

	public static < T, L > ListenableVar< T, L > create( final T value, final Consumer< L > notify )
	{
		return new DefaultListenableVar<>( value, notify );
	}

	public static < T, L > ListenableVar< T, L > create( final T value, final BiConsumer< L, T > notify )
	{
		return new DefaultListenableVar<>( value, notify, false );
	}

	public static < T, L > ListenableVar< T, L > create( final T value, final Consumer< L > notify, final boolean notifyWhenSet )
	{
		return new DefaultListenableVar<>( value, notify, notifyWhenSet );
	}

	public static < T, L > ListenableVar< T, L > create( final T value, final BiConsumer< L, T > notify, final boolean notifyWhenSet )
	{
		return new DefaultListenableVar<>( value, notify, notifyWhenSet );
	}
}
