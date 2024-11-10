package Lr2;

import java.util.*;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) {

        System.out.printf("Main Thread: begin\n");

        int lowestMeasure = 0;
        int highestMeasure = 0;

        //Ввід діапозону генерованих чисел
        while (true){
            Scanner gloriousNImport = new Scanner(System.in);
            System.out.println("Enter lowest measure in range [0, 1000]:");
            try{
                lowestMeasure = gloriousNImport.nextInt();
                if (lowestMeasure>1000 || lowestMeasure<0){
                    throw new InputMismatchException("Input error!");
                }

            }catch (InputMismatchException e){
                System.out.println("Proposed measure is out of range!");
                System.out.println("Try again!");

            }
            System.out.println("Enter highest measure in range [0, 1000]:");
            try{
                highestMeasure = gloriousNImport.nextInt();
                if (highestMeasure>1000 || highestMeasure<0 || highestMeasure<lowestMeasure){
                    throw new InputMismatchException("Input error!");
                }

                System.out.println();
                break;

            }catch (InputMismatchException e){
                System.out.println("Proposed measure is out of range!");
                System.out.println("Try again!");

            }
        }

        long startTime = System.currentTimeMillis();

        //Встановлюємо кількість потоків (всі ядра окрім 4-х, або всі що є в наявності)
        int availableResource = (Runtime.getRuntime().availableProcessors() - 4 > 0) ? Runtime.getRuntime().availableProcessors() - 4 : Runtime.getRuntime().availableProcessors();
        System.out.printf("Available CPU resource: %s core(s)\n", availableResource);

        //Кланяємось та просимо Герр Кайзера згенерувати масив з 60-ти значень у вказаному діапазоні
        CopyOnWriteArrayList<Integer> array = ArrayUtilsDesKaiser.askKaiserToInitArray(60, availableResource, lowestMeasure, highestMeasure);
        long resultTime1 = System.currentTimeMillis()-startTime;
        System.out.printf("-----------------------------------------------------------------------------------------\n");
        System.out.printf("Initialization complete!\n", resultTime1);
        System.out.printf("Array init time: %s ms\n", resultTime1);
        System.out.println("Starting Array: " + array + "\n");

        //Ввід числа N
        int N;
        while (true){

            System.out.println("Enter N in range [0, 1000]:");
            try{
                Scanner gloriousNImport = new Scanner(System.in);
                N = gloriousNImport.nextInt();


                if (N>1000 || N<0){
                    throw new InputMismatchException("Input error!");
                }
                gloriousNImport.close();
                break;
            }catch (InputMismatchException e){
                System.out.println("N is out of range!");
                System.out.println("Try again!");

            }

        }


        long secondStartTime = System.currentTimeMillis();
        int sectionSize = array.size() / availableResource;
        System.out.printf("Section size: %s elements\n", sectionSize);

        ExecutorService executor = Executors.newFixedThreadPool(availableResource);
        List<Future<List<Integer>>> futures = new ArrayList<>();

        //Асинхроно перевіряємо масив
        try {
            final int userN = N;
            for (int i = 0; i < availableResource; i++) {
                //Встановлення меж кожного потоку
                final int start = i * sectionSize;
                final int end = (i == availableResource - 1) ? array.size() : start + sectionSize;

                //Ініціалізація потоку
                Callable<List<Integer>> task = () -> {
                    CopyOnWriteArrayList<Integer> arrayResult = new CopyOnWriteArrayList<>();
                    System.out.printf("Init %s: begin\n", Thread.currentThread().getName());
                    //Перебираємо значення частини масиву
                    for (int j = start; j < end; j++) {
                        int testedNumber = array.get(j);
                        //Кланяємось та просимо Герр Кайзера перевірити число в масиві. Хто казав, що бути Кайзером легко?
                        if (ArrayUtilsDesKaiser.askKaiserToCheckWhetherTheNumberIsPrime(testedNumber) && testedNumber<userN) {

                            arrayResult.add(testedNumber);
//                            Thread.sleep(200);
                        }
                    }

                    System.out.printf("Init %s: end\n", Thread.currentThread().getName());
                    System.out.printf(Thread.currentThread().getName() + " result: "+arrayResult+"\n");


                    return arrayResult;
                };
                Future<List<Integer>> future = executor.submit(task);
                futures.add(future);

                //Виділення часу на завершення роботи алгоритму
                Thread.sleep(20);

                if (!future.isDone()) {
                    future.cancel(true);
                }

            }

            //Обробка результатів потоків
            CopyOnWriteArrayList<Integer> allPrimes = new CopyOnWriteArrayList<>();
            for (Future<List<Integer>> future : futures) {
                try {
                    //Отриання результатів завершених потоків та скасування незавершених
                    if (future.isCancelled()) {
                        System.out.printf("Thread was cancelled!\n", Thread.currentThread().getName());
                    } else {
                        allPrimes.addAll(future.get());
                    }

                } catch (CancellationException e) {
                    System.out.printf("Task %s was cancelled due to inexpectation!\n", Thread.currentThread().getName());
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.printf("-----------------------------------------------------------------------------------------\n");
            System.out.printf("Task finished!\n");
            System.out.println("All prime numbers: " + allPrimes);
            System.out.printf("Main Thread: end\n");



        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }

        long resultTime2 = System.currentTimeMillis()-secondStartTime;
        System.out.printf("Computing time: %s ms\n",resultTime2);
        System.out.printf("Total completion time: %s ms\n",resultTime2+resultTime1);
        System.out.printf("Total session time: %s ms\n",System.currentTimeMillis()-startTime);
    }
}




