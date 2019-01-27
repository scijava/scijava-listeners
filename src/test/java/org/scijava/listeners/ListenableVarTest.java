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
		final ListenableVar< Long, ChangeListener > var = ListenableVar.create( 0L );
		Assert.assertEquals( ( long ) var.get(), 0L );
		var.set( 10L );
		Assert.assertEquals( ( long ) var.get(), 10L );
	}

	interface MyChangeListener
	{
		void changed();
	}

	@Test
	public void testWithConsumer()
	{
		final ListenableVar< Long, MyChangeListener > var = ListenableVar.create( 0L, MyChangeListener::changed );
		final AtomicBoolean notified = new AtomicBoolean( false );
		var.listeners().add( () -> notified.set( true ) );
		var.set( 10l );

		Assert.assertTrue( notified.get() );
		Assert.assertEquals( ( long ) var.get(), 10L );
	}

	@Test
	public void testNoNotificationForUnchangedValue()
	{
		final ListenableVar< Long, MyChangeListener > var = ListenableVar.create( 10L, MyChangeListener::changed );
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
		final ListenableVar< Long, ValueListener< Long > > var = ListenableVar.create( 1L, ValueListener::valueChanged );
		final AtomicLong notified = new AtomicLong();
		var.listeners().add( notified::set );
		var.set( 10L );
		Assert.assertEquals( notified.get(), 10L );
	}

	@Test
	public void testWithRunnable()
	{
		final ListenableVar< Long, ChangeListener > var = ListenableVar.create( 0L );
		final AtomicBoolean notified = new AtomicBoolean( false );
		var.listeners().add( () -> notified.set( true ) );
		var.set( 10L );
		Assert.assertTrue( notified.get() );
	}
}
