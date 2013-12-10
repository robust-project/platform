/**
 * Copyright 2013 DERI, National University of Ireland Galway.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package deri.uimr.streamsim;

import java.util.Queue;
import java.util.PriorityQueue;
import java.util.Collection;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

/**
 * Implements a bounded blocking priority queue for holding events to be streamed.
 *
 * @see Event
 *
 * @since 1.0
 */
public class PriorityEventsQueue implements EventsQueue {
    /**
     * Internal {@code PriorityQueue} used as back-end.
     */
    private final Queue<Event> queue = new PriorityQueue<Event>();

    /**
     * Internal capacity of this events queue.
     */
    private final int capacity;

    /**
     * Internal events count for this queue.
     */
    private int count;

    /**
     * Internal lock used to block this queue when needed.
     */
    private final Lock lock = new ReentrantLock();

    /**
     * Internal condition to signal when this queue is not full anymore.
     */
    private final Condition notFull = lock.newCondition();

    /**
     * Internal condition to signal when this queue is not empty anymore.
     */
    private final Condition notEmpty = lock.newCondition();

    /**
     * Creates a new {@code PriorityEventsQueue} with the specified capacity.
     *
     * @param capacity the capacity for the new priority events queue.
     */
    public PriorityEventsQueue(final int capacity) {
        this.capacity = capacity;
        this.count = 0;
    }

    /** {@inheritDoc} */
    @Override
    public Event take() throws InterruptedException {
        lock.lock();
        try {
            while( count == 0 )
                notEmpty.await();
            final Event event = queue.poll();
            --count;
            notFull.signal();
            return event;
        }
        finally {
            lock.unlock();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void put(final Event event) throws InterruptedException {
        lock.lock();
        try {
            while( count == capacity )
                notFull.await();
            queue.add(event);
            ++count;
            notEmpty.signal();
        }
        finally {
            lock.unlock();
        }
    }

    /** {@inheritDoc} */
    @Override
    public int size() {
        return count;
    }

    /** {@inheritDoc} */
    @Override
    public int remainingCapacity() {
        return capacity - count;
    }

    /** {@inheritDoc} */
    @Override
    public int drainTo(final Collection<Event> c) {
        return drainTo(c, Integer.MAX_VALUE);
    }

    /** {@inheritDoc} */
    @Override
    public int drainTo(final Collection<Event> c, final int maxElements) {
        if( c == null )
            throw new NullPointerException();
        if( maxElements <= 0 )
            return 0;
        lock.lock();
        try {
            int n = 0;
            Event e;
            while( n < maxElements && (e = queue.poll()) != null ) {
                c.add(e);
                ++n;
                --count;
            }
            if( n > 0 )
                notFull.signal();
            return n;
        } finally {
            lock.unlock();
        }
    }
}
