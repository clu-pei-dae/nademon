package net.jmhering.nademon.models;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by clupeidae on 10.06.15.
 */
public class NagiosHostCollection implements Collection<NagiosHost> {
    HashSet<NagiosHost> hosts;

    public NagiosHostCollection() {
        hosts = new HashSet<NagiosHost>();
    }

    public int size() {
        return hosts.size();
    }

    public boolean isEmpty() {
        return hosts.isEmpty();
    }

    public boolean contains(Object o) {
        return hosts.contains(o);
    }

    public Iterator<NagiosHost> iterator() {
        return hosts.iterator();
    }

    public Object[] toArray() {
        return hosts.toArray();
    }

    public <T> T[] toArray(T[] ts) {
        return hosts.toArray(ts);
    }

    public boolean add(NagiosHost nagiosHost) {
        return hosts.add(nagiosHost);
    }

    public boolean remove(Object o) {
        return hosts.remove(o);
    }

    public boolean containsAll(Collection<?> collection) {
        return hosts.containsAll(collection);
    }

    public boolean addAll(Collection<? extends NagiosHost> collection) {
        return hosts.addAll(collection);
    }

    public boolean removeAll(Collection<?> collection) {
        return hosts.removeAll(collection);
    }

    public boolean retainAll(Collection<?> collection) {
        return hosts.retainAll(collection);
    }

    public void clear() {
        hosts.clear();
    }
}
