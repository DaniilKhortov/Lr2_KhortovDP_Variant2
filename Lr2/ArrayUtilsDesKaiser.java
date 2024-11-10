package Lr2;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;
import java.util.Random;
public class ArrayUtilsDesKaiser {
    //Імперська служба генерації масивів за параметрами
    public static synchronized CopyOnWriteArrayList<Integer> askKaiserToInitArray(int arraySize, int availableResource, int low, int high){
        Random kaiserRandom = new Random();

        //Встановлення розміру підмасивів
        int sectionSize = arraySize / availableResource;
        System.out.printf("Section size: %s elements\n", sectionSize);
        //Ініціалізація масиву
        CopyOnWriteArrayList<Integer> resultArray = new CopyOnWriteArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(availableResource);

        List<Callable<Void>> tasks = new ArrayList<>();

        //Заповнення масиву у кілька потоків
        for (int i = 0; i < availableResource; i++) {
            int start = i * sectionSize;
            int end = (i == availableResource - 1) ? arraySize : start + sectionSize;

            tasks.add(() -> {
                System.out.printf("Init %s: begin\n", Thread.currentThread().getName());
                for (int j = start; j < end; j++) {
                    resultArray.add(kaiserRandom.nextInt(high+1-low)+low);
                }
                System.out.printf("Init %s: end\n", Thread.currentThread().getName());
                return null;
            });
        }

        //Отримання масиву
        try {
            List<Future<Void>> futures = executor.invokeAll(tasks);
            for (Future<Void> future : futures) {
                future.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    return resultArray;

    }



    //Імперська служба перевірки простих чисел
    public static boolean askKaiserToCheckWhetherTheNumberIsPrime(int number){

        if (number <= 1){

            return false;
        }

        for (int i = 2; i < number; i++)
            if (number % i == 0){

                return false;
            }

        return true;
    }
}
