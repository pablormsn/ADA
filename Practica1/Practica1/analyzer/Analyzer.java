package analyzer;

import java.util.Arrays;
import java.util.HashMap;

public class Analyzer implements Runnable {
    Algorithm algorithm;
    long maxExecutionTime;
    String complexity = null;

    public Analyzer(Algorithm algorithm, long maxExecutionTime) {
        this.algorithm = algorithm;
        this.maxExecutionTime = maxExecutionTime;
    }

    public String getComplexity() {
        return complexity;
    }

    @Override
    public void run() {
        complexity = findComplexityOf(algorithm, maxExecutionTime);
    }

    //Método para inicializar los tiempos mínimos y máximos
    public static void inicializar(long[] a){
        for(int i = 0; i < a.length; i++){
            a[i] = Long.MAX_VALUE; //Inicializo a todos los valores a Long.MAX_VALUE, para que luego se vayan sustituyendo por los tiempos mínimos, porque si fuera a 0, no se sustituirían
        }
    }

    public static double medRatio(double[] ratios){
        double sum = 0.0;
        int cnt = 0;
        for(int i = 0; i < ratios.length; i++){
            if(ratios[i] != 0){
                sum += ratios[i];
                cnt++;
            }
        }
        if(cnt > 0){
            return sum / (double) cnt;
        }
        return sum / ratios.length;
    }

    public static double calcRatio(Algorithm algorithm, double[] n1, double[] n2, long tMax) {
        Chronometer tMed = new Chronometer(); //Cronómetro para calcular el tiempo de un algoritmo con tamaño n
        tMed.stop();
        Chronometer tTotal = new Chronometer(); //Cronómetro para calcular el tiempo total de un algoritmo para todos los tamaños
        tTotal.stop();
        int tam = n1.length; //Tamaño del primer array
        double[] ratios = new double[tam]; //Array para guardar los ratios de cada tamaño del primer array. Tamaño TAM;
        long[] tMins = new long[tam]; //Array para guardar los tiempos de ejecución de cada tamaño del primer array. Tamaño TAM;
        long[] tMaxs = new long[tam]; //Array para guardar los tiempos de ejecución de cada tamaño del segundo array. Tamaño TAM;
        double ratio_aux = 0.0; //Ratio que devolveré, inicializado a 0.0
        //Inicializo tiempos mínimos y máximos
        inicializar(tMins);
        inicializar(tMaxs);

        tMax = (long) tMax - (tMax / 4); //Le resto un cuarto del tiempo máximo para  asegurarme que no se pase del tiempo máximo

        tTotal.start(); //Empiezo a contar el tiempo total
        for (int i = 0; tTotal.getElapsedTime() < tMax && i < 5; i++) {
            for (int j = 0; tTotal.getElapsedTime() < tMax && j < tam; j++) {
                //Ejecutamos el algoritmo con los primeros tamaños
                algorithm.init((long) n1[j]); //Inicializo el algoritmo con el tamaño n1[j]
                tMed.start(); //Empiezo a contar el tiempo del algoritmo con tamaño n1[j]
                algorithm.run(); //Ejecuto el algoritmo con tamaño n1[j]
                tMed.stop(); //Paro el cronómetro
                //Guardo el tiempo en la primera lista de tiempos si es menor que el que ya había
                long t1 = tMed.getElapsedTime(); //Guardo el tiempo en una variable
                if(t1 > 0){
                    tMins[j] = Math.min(tMins[j], t1); //Guardo el tiempo en la primera lista de tiempos si es menor que el que ya había
                    if(t1 > 2000){
                        break;
                    }
                }
                //Ejecutamos el algoritmo con los segundos tamaños
                algorithm.init((long) n2[j]); //Inicializo el algoritmo con el tamaño n2[j]
                tMed.start(); //Empiezo a contar el tiempo del algoritmo con tamaño n2[j]
                algorithm.run(); //Ejecuto el algoritmo con tamaño n2[j]
                tMed.stop(); //Paro el cronómetro
                //Guardo el tiempo en la segunda lista de tiempos si es mayor que el que ya había
                long t2 = tMed.getElapsedTime(); //Guardo el tiempo en una variable
                if(t2 > 0){
                    tMaxs[j] = Math.min(tMaxs[j], t2); //Guardo el tiempo en la segunda lista de tiempos si es mayor que el que ya había
                    if(t2 > 2000){
                        break;
                    }
                }
                //Si el ratio es muy grande, no debería seguir la ejecución
                if (tMins[j] != 0) {
                    ratio_aux = (double) tMaxs[j] / (double) tMins[j];
                }

                if (ratio_aux > 5) {
                    System.out.println("Si continúo ejecutando, el tiempo de ejecución acabará superando el límite de tiempo. Por tanto, no se puede analizar el algoritmo para este valor.");
                    break;
                }
            }
        }
        tTotal.stop(); //Termino de contar el tiempo total

        for (int j = 0; j < ratios.length; j++) {
            if (tMins[j] != 0 && tMins[j] != Long.MAX_VALUE && tMaxs[j] != 0 && tMaxs[j] != Long.MAX_VALUE && tMins[j] < tMax + (tMax / 4) && tMaxs[j] < tMax + (tMax / 4)) {
                ratios[j] = (double) tMaxs[j] / (double) tMins[j];
            }
        }
        //Calculo la media de los ratios que no sean 0 y la devuelvo

        return medRatio(ratios);
    }

    static String findComplexityOf(Algorithm algorithm, long maxExecutionTime) {
        //Hago dos arrays con distintos tamaños, uno con el doble de tamaño que el otro para calcular el ratio y a que complejidad se acerca
        double[] n1 = {10, 20, 50, 100, 200, 300, 500};
        double[] n2 = {20, 40, 100, 200, 400, 600, 1000};


        //Calculo el ratio
        double ratio = calcRatio(algorithm, n1, n2, maxExecutionTime);
        System.out.println("Ratio: " + ratio);
        String complexity = null;

        if(ratio >= 0 && ratio <=1.1){
            complexity = "1";
        }else if(ratio >1.1 && ratio <1.26){
            complexity = "log(n)";
        }else if(ratio>=1.26 && ratio <2.15){
            complexity = "n";
        }else if(ratio >=2.15 && ratio <=2.8){
            complexity = "n*log(n)";
        }else if(ratio >2.8 && ratio <=4){
            complexity = "n^2";
        }else if(ratio >4 && ratio <= 11){
            complexity = "n^3";
        }else if(ratio > 11 ){
            complexity = "2^n";
        }

        return complexity;
    }
}