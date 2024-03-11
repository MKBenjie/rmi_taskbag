import java.net.Inet4Address;
import java.rmi.Naming;
import java.util.ArrayList;
import java.util.List;


public class WorkerProcess {
    public static void main(String args[]) {
        try {
            System.out.println("Worker is booting......");

            // Declare variables to hold task and result arrays, and the task ID
            int[] task,result;
            int taskId;
            String ip = Inet4Address.getLocalHost().getHostAddress();

            // Determine the host address for the RMI registry
            String hostAddress;
            if (args.length >= 1) {
                hostAddress = args[0];
            } else {
                System.out.println("***Using Default localhost***");
                hostAddress = "localhost";
            }

            // Look up the TaskBagInterface object from the RMI registry
            TaskBagInterface taskBag = (TaskBagInterface) Naming.lookup("//"+hostAddress+"/taskBag");

            // Continuously retrieve and process tasks from the Task Bag
            while (true) {
                // Retrieve the task ID for the next task to be executed
                taskId = taskBag.pairIn("NextTask");
            
                // Retrieve/Withdraw the task data corresponding to the task ID
                task = taskBag.pairIn(taskId);

                // Calculate prime numbers from the task data
                result = getPrime(task);

                // Store the calculated prime numbers as the result
                taskBag.setCurrentWorkerDetails(ip);
                taskBag.pairOut("result", result);

                // Sleep for 2 seconds to simulate task processing time
                Thread.sleep(2000);

                // Print a message indicating that the worker has finished processing a task
                System.out.println("Worker has finished a Task......");
            }
            
        } catch (Exception e) {
            // Print a message indicating that no more tasks are available or an exception occurred
            System.out.println(e.getMessage());
            System.out.println("No more tasks");
        }
    }


    // Method to calculate prime numbers from an array of integers
    public static int[] getPrime(int[] task) {
        // Create a list to store prime numbers
        List<Integer> primeNumbersList = new ArrayList<>();

        // Iterate over each integer in the task array
        for (int i = 0; i < task.length; i++) {
            // Check if the integer is prime
            if (isPrime(task[i])) {
                // If prime, add it to the list of prime numbers
                primeNumbersList.add(task[i]);
            }
        }

        // Convert the list of prime numbers to an array
        int[] primeNumbersArray = new int[primeNumbersList.size()];

        for (int i = 0; i < primeNumbersList.size(); i++) {
            primeNumbersArray[i] = primeNumbersList.get(i);
        }
        return primeNumbersArray;
    }


    // Method to check if a number is prime
    public static boolean isPrime(int num) {
        if (num <= 1) {                             /* what of case when one is the num */
            return false;
        }
        for (int i = 2; i <= Math.sqrt(num); i++) {         
            if (num % i == 0) {                            
                return false;
            }
        }
        return true;
    }
}
