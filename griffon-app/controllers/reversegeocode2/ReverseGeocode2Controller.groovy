package reversegeocode2

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.concurrent.DelayQueue

class ReverseGeocode2Controller {
    // these will be injected by Griffon
    def model
    def view

    // Will be used for persistence of previously queried coordinates
    //      would help to reduce the number of requests
    // SQL or NoSQL solution both possible; how much to cache will
    //      depend on retrieval stats when the method is multi-threaded
    void newAction(evt) { }
    void saveAction(evt) { }
    void deleteAction(evt) { }

    //RequestThrottle throttle = new RequestThrottle();

    //NON-FUNCTIONING CLASS
    // Class designed to prevent Google API from IP blocking:
    //      We are limited to 15000 requests/day or 1 request/5.76 seconds
    //      We can probably exceed the request/second ratio over a short span
    //          Throttle determines how often we can request based on stats
    //
    // LOGIC:
    //      THROTTLE_RATIO * REQUEST_TIMER = Duration of queue
    //      BEFORE trying to add an element to queue, clean the queue
    //
    //      What do we do with denied requests? Queue the parameters or deny?
    static class RequestThrottle{
        static final REQUEST_TIMER = 5.76; // Seconds
        static final CAP_SPAN = 180;
        static final CAP_VALUE = 50;

        Boolean QUEUE_OPEN;
        Double THROTTLE_RATIO;
        Integer CUR_POOL_CT;
        Queue<Date> REQUEST_QUEUE;

        private RequestThrottle() {
            QUEUE_OPEN = true;
            THROTTLE_RATIO = 1.00;
            REQUEST_QUEUE = new Queue<Date>() {
                @Override
                boolean add(Date e) {
                    return false  //To change body of implemented methods use File | Settings | File Templates.
                }

                @Override
                boolean offer(Date e) {
                    return false  //To change body of implemented methods use File | Settings | File Templates.
                }

                @Override
                Date remove() {
                    return null  //To change body of implemented methods use File | Settings | File Templates.
                }

                @Override
                Date poll() {
                    return null  //To change body of implemented methods use File | Settings | File Templates.
                }

                @Override
                Date element() {
                    return null  //To change body of implemented methods use File | Settings | File Templates.
                }

                @Override
                Date peek() {
                    return null  //To change body of implemented methods use File | Settings | File Templates.
                }

                @Override
                int size() {
                    return 0  //To change body of implemented methods use File | Settings | File Templates.
                }

                @Override
                boolean isEmpty() {
                    return false  //To change body of implemented methods use File | Settings | File Templates.
                }

                @Override
                boolean contains(Object o) {
                    return false  //To change body of implemented methods use File | Settings | File Templates.
                }

                @Override
                Iterator<Date> iterator() {
                    return null  //To change body of implemented methods use File | Settings | File Templates.
                }

                @Override
                Object[] toArray() {
                    return new Object[0]  //To change body of implemented methods use File | Settings | File Templates.
                }

                @Override
                def <T> T[] toArray(T[] a) {
                    return null  //To change body of implemented methods use File | Settings | File Templates.
                }

                @Override
                boolean remove(Object o) {
                    return false  //To change body of implemented methods use File | Settings | File Templates.
                }

                @Override
                boolean containsAll(Collection<?> c) {
                    return false  //To change body of implemented methods use File | Settings | File Templates.
                }

                @Override
                boolean addAll(Collection<? extends Date> c) {
                    return false  //To change body of implemented methods use File | Settings | File Templates.
                }

                @Override
                boolean removeAll(Collection<?> c) {
                    return false  //To change body of implemented methods use File | Settings | File Templates.
                }

                @Override
                boolean retainAll(Collection<?> c) {
                    return false  //To change body of implemented methods use File | Settings | File Templates.
                }

                @Override
                void clear() {
                    //To change body of implemented methods use File | Settings | File Templates.
                }
            }
        }

        public Boolean requestPlace(Date requestTime) {
            if (REQUEST_QUEUE.size() >= CAP_VALUE) QUEUE_OPEN = false
            if (!QUEUE_OPEN) return false

            if (REQUEST_QUEUE.offer(requestTime)) {
                return true;
            }
            return false;
        }

        private void cleanQueue() {
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            Date TestTime = Date();
            while (((TestTime - REQUEST_QUEUE.peek()) / 1000 % 60) >= CAP_SPAN) {
                REQUEST_QUEUE.remove();
            }
            if (REQUEST_QUEUE.size() < CAP_VALUE) QUEUE_OPEN = true;
        }
    }

    // void mvcGroupInit(Map args) {
    //    // this method is called after model and view are injected
    // }

    // void mvcGroupDestroy() {
    //    // this method is called when the group is destroyed
    // }

    /*
        Remember that actions will be called outside of the UI thread
        by default. You can change this setting of course.
        Please read chapter 9 of the Griffon Guide to know more.
       
    def action = { evt = null ->
    }
    */
}
