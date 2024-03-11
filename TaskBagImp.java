import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskBagImp extends UnicastRemoteObject implements TaskBagInterface{

    private Map<Integer, int[]> taskData;
    private Map<String, Integer> taskDescriptions;
    private Map<String, List<Integer>> taskResults;
    private String currentWorkerDetails;
    private int maxTaskId;
    Map<String, List<Integer>> otherResults;

    public TaskBagImp() throws RemoteException {
        taskData = new HashMap<>();
        taskDescriptions = new HashMap<>();
        taskResults = new HashMap<>();
        maxTaskId = 0;
        otherResults = new HashMap<>();

    }

    // Master process method for adding a task to the taskData HashMap 
    public synchronized void pairOut(int id, int[] value) throws RemoteException {
        taskData.put(id, value);
    }

    public synchronized void pairOut(String key, int[] value, String description) throws RemoteException {
        List<Integer> results = taskResults.getOrDefault(key, new ArrayList<Integer>());
        for (int num : value) {
            results.add(num);                                     /* results.addAll(results); */
        }
        taskResults.put(key, results);
        // -TODO- 
        getCurrentWorkerDetails();
        otherResults.put(description, results);
    }

    // Master Process Adds tasks description to taskDescriptions HashMap
    public synchronized void pairOut(String key, int id) throws RemoteException {
        taskDescriptions.put(key, id);
        if (id > maxTaskId) {
            maxTaskId = id;
        }
    }

    public synchronized void setCurrentWorkerDetails (String details) throws RemoteException {
        currentWorkerDetails = details;
    }

    public synchronized String getCurrentWorkerDetails () throws RemoteException {
        return currentWorkerDetails;
    }


    // Method to Withdraws a task to be executed and deletes the task from taskData array
    public synchronized int[] pairIn(int id) throws RemoteException {
        return taskData.remove(id);
    }

    // Method that Withdraws next task id from the taskDescriptions array
    public synchronized int pairIn(String key) throws RemoteException {
        while (taskDescriptions.containsKey("NextTask")) {
            try {
                int taskId = taskDescriptions.remove(key);
                if (taskId < maxTaskId) {
                    // change next task key to NextTask
                    taskDescriptions.put("NextTask", taskId + 1);          
                    taskDescriptions.remove("Task" + (taskId + 1));
                }

                return taskId;
               
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }
        }
        return -1;
    }

    // Method to Read the pair from the tasksResults done by workers and returns array of value
    public synchronized List<Integer> readPair(String key) throws RemoteException {
        List<Integer> result = new ArrayList<>();
        for (String taskKey : taskResults.keySet()) {
            if (taskKey.endsWith(key)) {
                result = taskResults.remove(taskKey);
                break;
            }
        }
        return result;
    }

    // @Override
    // public void pairOut(String key, int[] value, int description) throws RemoteException {
    //     // TODO Auto-generated method stub
    //     pairOut(key, description);

    //     throw new UnsupportedOperationException("Unimplemented method 'pairOut'");
    // }

}
