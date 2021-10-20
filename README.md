[![](https://github.com/scijava/scijava-listeners/actions/workflows/build-main.yml/badge.svg)](https://github.com/scijava/scijava-listeners/actions/workflows/build-main.yml)

# scijava-listeners

Helper class for maintaining lists of listeners.

Usage example:
```java
public interface MyListener
{
    void somethingChanged();
}

public class Listenable
{
    // Create a variant of Listeners.List
    private final Listeners.List< MyListener > listeners = new Listeners.SynchronizedList<>();

    // Use Listeners.List.list to call registered listeners
    private void notifyListeners() {
        listeners.list.forEach( MyListener::somethingChanged );
    }

    // expose only Listeners (not Listeners.List) to allow un/registering listeners
    public Listeners< MyListener > myListeners() {
        return listeners;
    }
}

public class Listening
{
    public Listening( Listenable l ) {
        l.myListeners().add( this::notifyMe );
    }

    void notifyMe() {
        System.out.println( "something changed!");
    }
}
```
