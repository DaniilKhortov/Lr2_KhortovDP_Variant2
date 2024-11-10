# LR2_Async_Khortov

# Завдання
<p><b>Варіант-2</b></p>
Напишіть Callable, який приймає число N і знаходить всі прості
числа до N. Створіть кілька потоків, кожен з яких обробляє свій
діапазон. Використовуйте Future, щоб зібрати результати обчислень з
усіх потоків.
Діапазон [0; 1000] – цілі числа, число N задає користувач.
Використати CopyOnWriteArrayList.

# Загальний опис рішення
<p><b>Код складається з двох частин:</b></p>
<ul>
  <li>1.Програма створює масив CopyOnWriteArrayList за допомогою асинхронних потоків.</li>
  <li>2.Після ініціалізації, відбувається перебір комірок масиву та запис необхідних значень у новий.</li>

</ul>

# Опис ініціалізації масиву
<p>На початку, програма просить користувача встановити діапазон генерованих значень.<\n></p>
 
Основні положення вводу:
<ul>
  <li>1.Згідно задачі, мінімальна можлива межа: 0, максимальна можлива межа: 1000.</li>
  <li>2.При введені невідповідних значень, програма попросить заново задати межі діапозону.</li>
</ul>



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
   Для генерації масиву використовується функція <b>askKaiserToInitArray</b>. Ось його структура:
   

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

<p>Для заповнення масиву використовуються Collable, що редагують оголошену колекцію CopyOnWriteArrayList. Їх кількість обмежена кількістю ядер процесора. Крім того, задля економії ресурсу пристрою, якщо він має понад 5 ядер, то 4 з них не будуть задіяні. Не рекомендується виділяти всі ресурси пристрою для Java, оскільки це може призвести до зависання через брак потужностей системи. </p>
<p>Після визначення кількості ядер, програма розділить масив порівну для кожного потоку. Після заповнення масиву, функція його повертає</p>

# Опис перевірки масиву
<p>На початку, програма просить користувача встановити число N. Якщо при переборі значень, число буде більше за N, то воно ігноруватиметься програмою.<\n></p>
<p>Програма для перевірки застосовує обхід масиву за потоками Collable, кожен з яких обробляє свої межі. Процедура розподілу масиву такий самий, що й при його ініціалізації. <\n></p>
<p>Для перевірки використовується функція <b>askKaiserToCheckWhetherTheNumberIsPrime</b>:</p>

        if (number <= 1){

            return false;
        }

        for (int i = 2; i < number; i++)
            if (number % i == 0){

                return false;
            }

        return true;
Метод ітеративно ділить число на дільники, шо менші за нього. Якщо буде знайдено дільник, що ділить число націло, то функція повертає false, тобто повідомляє, що число не є простим. Метод є простим в реалізації, проте не є найефективнішим.
<p>Усі прості числа записуються у новий масив CopyOnWriteArrayList. Після завершення роботи усіх Collable, значення усіх масивів обєднуються в один спільний <b>allPrimes</b></p>
<p><b>Код обходу масиву:</b></p>


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
                          // Thread.sleep(200);
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
