
### Escuela Colombiana de Ingeniería
### Arquitecturas de Software - ARSW
## Ejercicio Fórmula BBP - Parcial Practico


**Ejercicio Fórmula BBP**

La fórmula [BBP](https://en.wikipedia.org/wiki/Bailey%E2%80%93Borwein%E2%80%93Plouffe_formula) (Bailey–Borwein–Plouffe formula) es un algoritmo que permite calcular el enésimo dígito de PI en base 16, con la particularidad de no necesitar calcular nos n-1 dígitos anteriores. Esta característica permite convertir el problema de calcular un número masivo de dígitos de PI (en base 16) a uno [vergonzosamente paralelo](https://en.wikipedia.org/wiki/Embarrassingly_parallel). En este repositorio encontrará la implementación, junto con un conjunto de pruebas. 

Para este ejercicio se quiere calcular, en el menor tiempo posible, y en una sola máquina (aprovechando las características multi-core de la mismas) al menos el primer millón de dígitos de PI (en base 16). Para esto

1. Cree una clase de tipo Thread que represente el ciclo de vida de un hilo que calcule una parte de los dígitos requeridos.
//
```text
Creamos una clase PiDigitThread que extiende Thread.
 Esta clase se encarga de calcular una porción específica de los dígitos de Pi, usando la lógica de la fórmula BBP.
```
```python
public class PiDigitThread extends Thread {
    private int start;
    private int count;
    private byte[] digits;
    private int threadId;

    public PiDigitThread(int start, int count, int threadId) {
        this.start = start;
        this.count = count;
        this.threadId = threadId;
        this.digits = new byte[count];
    }

    @Override
    public void run() {
        double sum = 0;
        int digitsProcessed = 0;

        for (int i = 0; i < count; i++) {
            if (i % PiDigits.DigitsPerSum == 0) {
                sum = 4 * PiDigits.sum(1, start)
                        - 2 * PiDigits.sum(4, start)
                        - PiDigits.sum(5, start)
                        - PiDigits.sum(6, start);
                start += PiDigits.DigitsPerSum;
            }

            sum = 16 * (sum - Math.floor(sum));
            digits[i] = (byte) sum;
            digitsProcessed++;
        }
    }

    public byte[] getDigits() {
        return digits;
    }
}

```
```text
```


2. Haga que la función PiDigits.getDigits() reciba como parámetro adicional un valor N, correspondiente al número de hilos entre los que se va a paralelizar la solución. Haga que dicha función espere hasta que los N hilos terminen de resolver el problema para combinar las respuestas y entonces retornar el resultado. Para esto, puede utilizar el método Join() del API de concurrencia de Java.
```text
Se modificó PiDigits.getDigits(int start, int count, int numThreads)
para dividir el cálculo entre N hilos (PiDigitThread), iniciar cada hilo, y luego hacer join() a
todos los hilos para esperar a que terminen y así combinar sus resultados.
```
```python
public static byte[] getDigits(int start, int count, int numThreads) {
    byte[] digits = new byte[count];
    int digitsPerThread = count / numThreads;

    PiDigitThread[] threads = new PiDigitThread[numThreads];
    int currentStart = start;

    for (int i = 0; i < numThreads; i++) {
        int threadCount = (i == numThreads - 1) ? (count - i * digitsPerThread) : digitsPerThread;
        threads[i] = new PiDigitThread(currentStart, threadCount, i);
        threads[i].start();
        currentStart += threadCount;
    }

    try {
        for (int i = 0; i < numThreads; i++) {
            threads[i].join();
        }
    } catch (InterruptedException e) {
        e.printStackTrace();
    }

    int currentIndex = 0;
    for (PiDigitThread thread : threads) {
        byte[] threadDigits = thread.getDigits();
        System.arraycopy(threadDigits, 0, digits, currentIndex, threadDigits.length);
        currentIndex += threadDigits.length;
    }

    return digits;
}
```
 
3. Ajuste la implementación para que cada 5 segundos los hilos se detengan e impriman el número de digitos que han procesado y una vez se presione la tecla enter que los hilos continúen su proceso.
```text
Dentro del método run() de PiDigitThread, cada vez que el hilo ha procesado 100 dígitos (o lo que decidas),
 se imprime el progreso y se espera a que el usuario presione Enter para continuar. Se sincroniza el acceso con synchronized
 para evitar conflictos entre hilos.
```
```python
private static final Object LOCK = new Object();

@Override
public void run() {
    double sum = 0;
    int digitsProcessed = 0;

    for (int i = 0; i < count; i++) {
        if (i % PiDigits.DigitsPerSum == 0) {
            sum = 4 * PiDigits.sum(1, start)
                    - 2 * PiDigits.sum(4, start)
                    - PiDigits.sum(5, start)
                    - PiDigits.sum(6, start);
            start += PiDigits.DigitsPerSum;
        }

        sum = 16 * (sum - Math.floor(sum));
        digits[i] = (byte) sum;
        digitsProcessed++;

        if (digitsProcessed % 100 == 0) {
            synchronized (LOCK) {
                try {
                    System.out.println("Thread " + threadId + " processed " + digitsProcessed + " digits. Press Enter to continue.");
                    System.in.read();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
```
-  <img width="1044" height="203" alt="image" src="https://github.com/user-attachments/assets/d7d2bd2a-125a-47ad-be0c-e2b0bb24b208" />
```text
El programa calcula los dígitos hexadecimales de Pi usando dos métodos:
Secuencial: calcula los dígitos uno tras otro.
Paralelo: divide el cálculo entre varios hilos que trabajan al mismo tiempo.
La salida muestra los dígitos obtenidos en cada método para comparar y confirmar que ambos dan el mismo resultado correcto.
```
