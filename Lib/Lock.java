public class Lock {

    public static synchronized void foo(int v) {
	LockTest.update(v);
    }
    
}
