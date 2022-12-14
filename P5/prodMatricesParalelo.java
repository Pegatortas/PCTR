import java.util.Random;
import java.util.concurrent.*;
/**
 * Esta clase realiza la multiplicación matriz/matriz de forma concurrente mediante la división de nube de datos
 * @author Álvaro Álvarez Cerviño
 * @version 15/11/22
 */
public class prodMatricesParalelo implements Runnable{
    static int n = 10000;
    int linf, lsup;
    static int [][] matriz1 = new int[n][n];
    static int [][] matriz2 = new int[n][n];
    static int [][] outMatriz = new int[n][n];

    /**
     * Método constructor parametrizado
     * @param linf índice de inicio
     * @param lsup índice de final
     */
    public prodMatricesParalelo(int linf, int lsup) {this.linf = linf; this.lsup = lsup;}

    /**
     * Método de la hebra que realiza la múltiplicación matriz1/matriz2 del rango asignado
     */
    public void run () {
        for (int i = linf; i < lsup; i++) {
            for (int j = 0; j < n; j++){
                for (int k = 0; k < n; k++) {
                    outMatriz[i][j]  += matriz1[i][k] * matriz2[k][j];
                }
            }
        }
    }

    public static void main (String[] args) throws InterruptedException {
        Random generador = new Random();
        int nHilos = 16;
        int nuevoInicioHebra = 0;
        int tamHilos = n / nHilos;
        int linf = 0;
        int lsup = tamHilos;

        //Rellenamos ambas matrices con numeros aleatorios de 0 al 99
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++){
                matriz1[i][j] = generador.nextInt(100);
                matriz2[i][j] = generador.nextInt(100);
                outMatriz[i][j] = 0;
            }
        }

        long tiempoIni = System.nanoTime();
        //Creamos el pool de hilos con capacidad de nHilos
        ExecutorService pool = Executors.newFixedThreadPool(nHilos);
        for (int i = 0; i < nHilos; i++){
            //La ultima tarea se queda con el rango que sobra
            if (i == nHilos - 1) {
                //Ejecutamos la tarea con rango dado
                pool.execute(new prodMatricesParalelo(linf, n));
            }
            else{
                pool.execute(new prodMatricesParalelo(linf, lsup));
                linf = lsup + 1;
                lsup += tamHilos;
            }
        }
    
        pool.shutdown();
        while(!pool.isTerminated());
        long tiempoFin = (System.nanoTime() - tiempoIni) / (long) Math.pow(10, 6);

        System.out.println(tiempoFin + " milisegundos");
    }
}
