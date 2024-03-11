import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface TaskBagInterface extends Remote{

    public void pairOut(int key, int[] value) throws RemoteException;
    // public void pairOut(String key, int[] value) throws RemoteException;
    public void pairOut(String key, int[] value, String description) throws RemoteException;
    public void pairOut(String key, int id) throws RemoteException;
    public int[] pairIn(int id) throws RemoteException;
    public int pairIn(String key) throws RemoteException;
    public List<Integer> readPair(String key) throws RemoteException;
    public void setCurrentWorkerDetails (String details) throws RemoteException;
    public String getCurrentWorkerDetails () throws RemoteException;
}