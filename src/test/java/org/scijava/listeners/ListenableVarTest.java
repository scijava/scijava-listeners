package org.scijava.listeners;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Assert;
import org.junit.Test;

public class ListenableVarTest
{
	@Test
	public void testSet()
	{
		ListenableVar< Long, Runnable > var = ListenableVar.simple( 0L );
		Assert.assertEquals( ( long ) var.get(), 0L );
		var.set( 10L );
		Assert.assertEquals( ( long ) var.get(), 10L );
	}

	interface ChangeListener
	{
		void changed();
	}

	@Test
	public void testWithConsumer()
	{
		ListenableVar< Long, ChangeListener > var = new ListenableVar<>( 0L, ChangeListener::changed );
		final AtomicBoolean notified = new AtomicBoolean( false );
		var.listeners().add( () -> notified.set( true ) );
		var.set( 10l );

		Assert.assertTrue( notified.get() );
		Assert.assertEquals( ( long ) var.get(), 10L );
	}

	@Test
	public void testNoNotificationForUnchangedValue()
	{
		ListenableVar< Long, ChangeListener > var = new ListenableVar<>( 10L, ChangeListener::changed );
		final AtomicBoolean notified = new AtomicBoolean( false );
		var.listeners().add( () -> notified.set( true ) );
		var.set( 10L );
		Assert.assertFalse( notified.get() );
	}

	interface ValueListener< T >
	{
		void valueChanged( T value );
	}

	@Test
	public void testWithBiConsumer()
	{
		ListenableVar< Long, ValueListener< Long > > var = new ListenableVar<>( 1L, ValueListener::valueChanged );
		final AtomicLong notified = new AtomicLong();
		var.listeners().add( notified::set );
		var.set( 10L );
		Assert.assertEquals( notified.get(), 10L );
	}

	@Test
	public void testWithRunnable()
	{
		ListenableVar< Long, Runnable > var = ListenableVar.simple( 0L );
		final AtomicBoolean notified = new AtomicBoolean( false );
		var.listeners().add( () -> notified.set( true ) );
		var.set( 10L );
		Assert.assertTrue( notified.get() );
	}
}
