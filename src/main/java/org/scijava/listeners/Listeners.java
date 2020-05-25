package org.scijava.listeners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * An ordered set of listeners of type {@code T}.
 *
 * @param <T>
 *            listener type
 */
public interface Listeners< T >
{
	/**
	 * Add a listener to the end of this set.
	 *
	 * @param listener
	 *            the listener to add.
	 * @return {@code true} if the listener was added. {@code false} if it was
	 *         already present.
	 */
	boolean add( final T listener );

	/**
	 * Insert a listener at the specified position in this set.
	 * The position {@code index} is clamped to the range [0..size].
	 *
	 * @param index
	 *            index at which to add the specified listener
	 * @param listener
	 *            the listener to add
	 * @return {@code true} if the listener was added. {@code false} if it was
	 *         already present.
	 */
	boolean add( final int index, final T listener );

	/**
	 * Removes a listener from this set.
	 *
	 * @param listener
	 *            the listener to remove.
	 * @return {@code true} if the listener was successfully removed.
	 *         {@code false} if the listener was not present.
	 */
	boolean remove( final T listener );

	default boolean addAll( final Collection< ? extends T > listeners )
	{
		if ( listeners.isEmpty() )
			return false;
		return listeners.stream().map( this::add ).reduce( Boolean::logicalOr ).get();
	}

	default boolean removeAll( final Collection< ? extends T > listeners )
	{
		if ( listeners.isEmpty() )
			return false;
		return listeners.stream().map( this::remove ).reduce( Boolean::logicalOr ).get();
	}

	/**
	 * Implements {@link Listeners} using an {@link ArrayList}.
	 */
	class List< T > implements Listeners< T >
	{
		private final Consumer< T > onAdd;

		public List( final Consumer< T > onAdd )
		{
			this( onAdd, new ArrayList< T >() );
		}

		public List()
		{
			this( o -> {} );
		}

		public final java.util.List< T > list;

		protected List( final Consumer< T > onAdd, final java.util.List< T > list )
		{
			this.onAdd = onAdd;
			this.list = list;
		}

		@Override
		public boolean add( final T listener )
		{
			if ( !list.contains( listener ) )
			{
				list.add( listener );
				onAdd.accept( listener );
				return true;
			}
			return false;
		}

		@Override
		public boolean add( final int index, final T listener )
		{
			if ( !list.contains( listener ) )
			{
				list.add( clamp( index, 0, list.size() ), listener );
				onAdd.accept( listener );
				return true;
			}
			return false;
		}

		@Override
		public boolean remove( final T listener )
		{
			return list.remove( listener );
		}

		public ArrayList< T > listCopy()
		{
			return new ArrayList<>( list );
		}

		static int clamp( int value, int min, int max )
		{
			return Math.min( max, Math.max( min, value ) );
		}
	}

	/**
	 * Extends {@link Listeners.List}, making {@code add} and {@code remove}
	 * methods synchronized.
	 * <p>
	 * The list is maintained as a {@link CopyOnWriteArrayList} to allow unsynchronized traversal.
	 */
	class SynchronizedList< T > extends List< T >
	{
		public SynchronizedList( final Consumer< T > onAdd )
		{
			super( onAdd, new CopyOnWriteArrayList<>() );
		}

		public SynchronizedList()
		{
			super( o -> {}, new CopyOnWriteArrayList<>() );
		}

		@Override
		public synchronized boolean add( final T listener )
		{
			return super.add( listener );
		}

		@Override
		public synchronized boolean add( final int index, final T listener )
		{
			return super.add( index, listener );
		}

		@Override
		public synchronized boolean remove( final T listener )
		{
			return super.remove( listener );
		}

		@Override
		public synchronized ArrayList< T > listCopy()
		{
			return super.listCopy();
		}
	}
}

