package net.jmhering.nademon.models;

import java.util.*;

/**
 * Created by clupeidae on 10.06.15.
 */
public class NagiosHostCollection implements Collection<NagiosHost> {
    //HashSet<NagiosHost> hosts;
    List<NagiosHost> hosts;
    boolean sorted = false;

    public NagiosHostCollection() {
        hosts = new ArrayList<>();
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
        sort();
        return hosts.iterator();
    }

    public Object[] toArray() {
        sort();
        return hosts.toArray();
    }

    public <T> T[] toArray(T[] ts) {
        return hosts.toArray(ts);
    }

    public boolean add(NagiosHost nagiosHost) {
        sorted = false;
        return hosts.add(nagiosHost);
    }

    public boolean remove(Object o) {
        return hosts.remove(o);
    }

    public boolean containsAll(Collection<?> collection) {
        return hosts.containsAll(collection);
    }

    public boolean addAll(Collection<? extends NagiosHost> collection) {
        sorted = false;
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

    private void sort() {
        if (!sorted) {
            hosts.sort(new HostSorter());
        }
    }

    class HostSorter implements Comparator<NagiosHost> {
        @Override
        public int compare(NagiosHost host, NagiosHost t1) {
            return host.compareTo(t1);
        }
    }


}
