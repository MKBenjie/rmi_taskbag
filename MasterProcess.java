import java.util.*;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;




public class MasterProcess {
    public interface TaskBagInterface extends Remote{

    public void pairOut(int key, int[] value) throws RemoteException;
    public void pairOut(String key, int[] value) throws RemoteException;
    public void pairOut(String key, int id) throws RemoteException;
    public int[] pairIn(int id) throws RemoteException;
    public int pairIn(String key) throws RemoteException;
    public List<Integer> readPair(String key) throws RemoteException;
    public void setCurrentWorkerDetails (String details) throws RemoteException;
    public String getCurrentWorkerDetails () throws RemoteException;
}
    public static void main(String[] args) {
        try {
            // System.setProperty("java.rmi.server.hostname", "127.0.0.1");
            System.out.println("Master is booting ......");
            // Extract the maximum value from command-line arguments
            String maxString = args[0];
            int max = Integer.parseInt(maxString);

            // Determine the host address for RMI registry
            String hostAddress;
            if (args.length >= 2) {
                hostAddress = args[1];
            } else {
                System.out.println("***Using Default localhost***");
                hostAddress = "localhost";
            }

            System.out.println("Master Process Loading ...... ");

            // Create task batches
            List<int[]> batchTasks = getRangeBatches(max);
            List<Integer> results = new ArrayList<>();

            TaskBagImp taskBag = new TaskBagImp();

            // Create and bind the TaskBag object to the RMI registry
            Naming.rebind("//"+hostAddress+"/taskBag", taskBag);

            System.out.println("Master is ready ......");

            // Add descriptions for tasks
            taskBag.pairOut("NextTask", 0);

            // Add key and list of values for task batch
            taskBag.pairOut(0, (int[]) batchTasks.get(0));

            // System.out.println(Arrays.toString((int[]) batchTasks.get(0)));

            // for loop to insert each batch into the taskBag
            for (int i = 1; i < batchTasks.size(); i++) {
                taskBag.pairOut("Task" + i, i);
                taskBag.pairOut(i, (int[]) batchTasks.get(i));
                // System.out.println(Arrays.toString((int[]) batchTasks.get(i)));
            }

            while (true) {
                Thread.sleep(5000);                                         
                // Get calculated results by workers
                List<Integer> result = taskBag.readPair("result");
                results.addAll(result);

                // break the loop if added result is null
                if (result.isEmpty()) {
                    System.out.println("Exiting ......");
                    break;
                }
                
                // System.out.println("Obtained Results From Worker(s):" + result.size());
                // System.out.println(taskBag.getCurrentWorkerDetails() + "\n");
                // for (int num : results) {
                //     System.out.print(num + " ");                                           
                // }
                // System.out.println();

                System.out.println("Obtained Results From Worker(s):");                
                for (int num : results) {
                    System.out.print(num + " ");                }
                System.out.println();

                Map<String, int[]> otherResults = new HashMap<String, int[]>(taskBag.otherResults);

                System.out.println("individual contributions");
                otherResults.forEach((key, value) -> {
                    System.out.print(key+" : "); 
                    
                        // Convert int[] to Integer[]
                    Integer[] integerArray = Arrays.stream(value).boxed().toArray(Integer[]::new);

                    // Convert Integer[] to List<Integer>
                    List<Integer> integerList = Arrays.asList(integerArray);
                    System.err.println();
                    System.err.println();



                    
                    System.out.println(integerList);
                    try {
                        taskBag.deductFromOtherResults(key);
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }); 
            }
        } catch (Exception e) {
            System.out.println("Encountered Exception from Master side" + e);
        }
    }

    // Method for creating ranges and grouping them into batches
    public static List<int[]> getRangeBatches(int max) {
        int min = 1; // minimum number to process
        int batchSize = 10; // size of each batch

        // Compute the number of batches required
        int numBatches = (int) Math.ceil((double) (max - min + 1) / batchSize);    /*SAME AS MAX*/
                                                                                    /* WHATS THE USE OF THE CEIL */

        // Break the range into batches
        List<int[]> batches = new ArrayList<>();
        for (int i = 0; i < numBatches; i++) {
            int start = min + i * batchSize;                                            
            int end = Math.min(start + batchSize - 1, max);
            int[] batch = new int[end - start + 1];
            for (int j = start; j <= end; j++) {
                batch[j - start] = j;
            }
            batches.add(batch);
        }

        

        return batches;
    }
}
