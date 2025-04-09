package ordenamiento;

import org.jfree.chart.*;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.xy.*;
import javax.swing.*;
import java.awt.*;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class InsercionVsIntercalacion {

    /**
     * *********************************************************************************************************
     * Nombre Clase: Resultado Propósito: Almacenar los datos de la simulación
     * para un tamaño n específico Variables utilizadas: int n, long
     * tiempoInsercion, long tiempoIntercalacion Precondición: Recibe los
     * tiempos medidos por ambos algoritmos para un mismo n Postcondición:
     * Guarda los valores para su uso posterior (gráfica, impresión, etc.)
     * *********************************************************************************************************
     */
    static class Resultado {

        int n;
        long tiempoInsercion;
        long tiempoIntercalacion;

        public Resultado(int n, long tiempoInsercion, long tiempoIntercalacion) {
            this.n = n;
            this.tiempoInsercion = tiempoInsercion;
            this.tiempoIntercalacion = tiempoIntercalacion;
        }
    }

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.print("Ingrese la cantidad de datos (minimo 1): ");
        int cant = sc.nextInt();
        if (cant < 1) {
            System.out.println("La cantidad minima permitida es 1.");
            return;
        }

        long simIni, simFin, simTot;
        PrintStream archivo = new PrintStream("insercion_vs_intercalacion.dat");
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        System.out.println("\n------------------------------");
        System.out.println("INICIO DE SIMULACION: " + dateFormat.format(new Date()));
        simIni = System.nanoTime();

        XYSeries serieInsercion = new XYSeries("Insercion");
        XYSeries serieIntercalacion = new XYSeries("Intercalacion");
        ArrayList<Resultado> resultados = new ArrayList<>();

        if (cant >= 3) {
            int n1 = cant / 3;
            int n2 = 2 * cant / 3;
            evaluar(n1, resultados);
            evaluar(n2, resultados);
        }

        evaluar(cant, resultados);

        Resultado rFinal = resultados.get(resultados.size() - 1);
        System.out.println("\n--- Comparacion para n = " + cant + " ---");
        System.out.printf("%-20s %-20s%n", "Algoritmo", "Comparaciones");
        System.out.printf("%-20s %-20d%n", "Insercion", comparacionesInsercion);
        System.out.printf("%-20s %-20d%n", "Intercalacion", comparacionesIntercalacion);
        System.out.printf("%nTiempo Insercion: %d ns%n", rFinal.tiempoInsercion);
        System.out.printf("Tiempo Intercalacion: %d ns%n", rFinal.tiempoIntercalacion);

        if (rFinal.tiempoInsercion < rFinal.tiempoIntercalacion) {
            System.out.println("Resultado: Insercion fue mas rapida.\n");
        } else if (rFinal.tiempoInsercion > rFinal.tiempoIntercalacion) {
            System.out.println("Resultado: Intercalacion fue mas rapida.\n");
        } else {
            System.out.println("Resultado: Ambos algoritmos tomaron el mismo tiempo.\n");
        }

        serieInsercion.add(0, 0);
        serieIntercalacion.add(0, 0);
        for (Resultado r : resultados) {
            serieInsercion.add(r.n, r.tiempoInsercion);
            serieIntercalacion.add(r.n, r.tiempoIntercalacion);
            archivo.println(r.n + " " + r.tiempoInsercion + " " + r.tiempoIntercalacion);
        }
        archivo.close();

        simFin = System.nanoTime();
        simTot = simFin - simIni;

        System.out.println("FIN DE LA SIMULACION: " + dateFormat.format(new Date()));
        System.out.println("TIEMPO TOTAL DE SIMULACION: " + TimeUnit.NANOSECONDS.toSeconds(simTot) + " s");
        System.out.println("------------------------------\n");

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(serieInsercion);
        dataset.addSeries(serieIntercalacion);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Insercion vs Intercalacion",
                "Cantidad de Datos",
                "Tiempo (ns)",
                dataset
        );

        XYPlot plot = chart.getXYPlot();
        XYSplineRenderer renderer = new XYSplineRenderer();
        renderer.setPrecision(10);
        renderer.setSeriesStroke(0, new BasicStroke(3.0f));
        renderer.setSeriesStroke(1, new BasicStroke(3.0f));
        renderer.setSeriesShapesVisible(0, false);
        renderer.setSeriesShapesVisible(1, false);

        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);

        JFrame frame = new JFrame("Grafica - Insercion vs Intercalacion");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new ChartPanel(chart));
        frame.pack();
        frame.setVisible(true);
    }

    static int comparacionesInsercion = 0;
    static int comparacionesIntercalacion = 0;

    /**
     * *********************************************************************************************************
     * Nombre Método: evaluar Propósito: Ejecutar ambos algoritmos con el mismo
     * conjunto de datos y medir tiempos/comparaciones Variables utilizadas: n,
     * resultados, arr1, arr2, ini, fin, tiempoInsercion, tiempoIntercalacion
     * Precondición: El valor de n debe ser mayor a 0 Postcondición: Se agrega
     * un objeto Resultado con las métricas medidas a la lista resultados
     * *********************************************************************************************************
     */
    public static void evaluar(int n, ArrayList<Resultado> resultados) {
        int[] arr1 = generarDatos(n);
        int[] arr2 = arr1.clone();

        comparacionesInsercion = 0;
        long ini = System.nanoTime();
        insercion(arr1);
        long fin = System.nanoTime();
        long tiempoInsercion = fin - ini;

        comparacionesIntercalacion = 0;
        ini = System.nanoTime();
        intercalacion(arr2, 0, arr2.length - 1);
        fin = System.nanoTime();
        long tiempoIntercalacion = fin - ini;

        resultados.add(new Resultado(n, tiempoInsercion, tiempoIntercalacion));
    }

    /**
     * *********************************************************************************************************
     * Nombre Método: generarDatos Propósito: Crear un arreglo de enteros
     * aleatorios entre 1 y 1000 Variables utilizadas: n, datos[i] Precondición:
     * n debe ser mayor a 0 Postcondición: Devuelve un arreglo de tamaño n con
     * valores aleatorios
     * *********************************************************************************************************
     */
    public static int[] generarDatos(int n) {
        int[] datos = new int[n];
        for (int i = 0; i < n; i++) {
            datos[i] = (int) (Math.random() * 1000) + 1;
        }
        return datos;
    }

    /**
     * *********************************************************************************************************
     * Nombre Método: insercion Propósito: Ordenar un arreglo usando el
     * algoritmo de Inserción Variables utilizadas: A, i, j, key Precondición:
     * El arreglo debe contener elementos válidos Postcondición: El arreglo
     * queda ordenado de menor a mayor y se cuentan comparaciones
     * *********************************************************************************************************
     */
    public static void insercion(int[] A) {
        for (int i = 1; i < A.length; i++) {
            int key = A[i];
            int j = i - 1;
            while (j >= 0 && A[j] > key) {
                comparacionesInsercion++;
                A[j + 1] = A[j];
                j--;
            }
            A[j + 1] = key;
            if (j >= 0) {
                comparacionesInsercion++;
            }
        }
    }

    /**
     * *********************************************************************************************************
     * Nombre Método: intercalacion Propósito: Ordenar un arreglo usando el
     * algoritmo de Merge Sort (Intercalación) Variables utilizadas: A, inicio,
     * fin, medio Precondición: El arreglo debe contener elementos válidos
     * Postcondición: El arreglo queda ordenado y se cuentan comparaciones
     * *********************************************************************************************************
     */
    public static void intercalacion(int[] A, int inicio, int fin) {
        if (inicio < fin) {
            int medio = (inicio + fin) / 2;
            intercalacion(A, inicio, medio);
            intercalacion(A, medio + 1, fin);
            merge(A, inicio, medio, fin);
        }
    }

    /************************************************************************************************************
     * Nombre Método: merge Propósito: Combinar dos subarreglos ordenados en uno
     * solo Variables utilizadas: A, inicio, medio, fin, izq, der, i, j, k
     * Precondición: Los subarreglos izquierdo y derecho deben estar ordenados
     * Postcondición: El segmento A[inicio..fin] queda ordenado y se cuentan
     * comparaciones
    ***********************************************************************************************************/
    
    public static void merge(int[] A, int inicio, int medio, int fin) {
        int[] izq = Arrays.copyOfRange(A, inicio, medio + 1);
        int[] der = Arrays.copyOfRange(A, medio + 1, fin + 1);

        int i = 0, j = 0, k = inicio;
        while (i < izq.length && j < der.length) {
            comparacionesIntercalacion++;
            if (izq[i] <= der[j]) {
                A[k++] = izq[i++];
            } else {
                A[k++] = der[j++];
            }
        }
        while (i < izq.length) {
            A[k++] = izq[i++];
        }
        while (j < der.length) {
            A[k++] = der[j++];
        }
    }
}
