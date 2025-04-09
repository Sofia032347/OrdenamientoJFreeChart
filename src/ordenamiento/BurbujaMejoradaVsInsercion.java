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

public class BurbujaMejoradaVsInsercion {

    static class Resultado {

        int n;
        long tiempoMejorada;
        long tiempoInsercion;

        /**
         * *********************************************************************************************************
         * Nombre Metodo: Resultado Proposito: Constructor para almacenar
         * resultados de tiempo por algoritmo Variables utilizadas: n,
         * tiempoMejorada, tiempoInsercion Precondicion: Valores enteros validos
         * Postcondicion: Objeto Resultado inicializado con datos
        *********************************************************************************************************
         */
        public Resultado(int n, long tiempoMejorada, long tiempoInsercion) {
            this.n = n;
            this.tiempoMejorada = tiempoMejorada;
            this.tiempoInsercion = tiempoInsercion;
        }
    }

    /**
     * *********************************************************************************************************
     * Nombre Metodo: main Proposito: Ejecutar comparacion de Burbuja Mejorada
     * vs Insercion, graficar y mostrar resultados Variables utilizadas: cant,
     * resultados, tiempoMejorada, tiempoInsercion, serieMejorada,
     * serieInsercion Precondicion: Usuario debe ingresar un entero >= 1
     * Postcondicion: Se muestra comparacion, archivo de resultados y grafico
    *********************************************************************************************************
     */
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.print("Ingrese la cantidad de datos (minimo 1): ");
        int cant = sc.nextInt();
        if (cant < 1) {
            System.out.println("La cantidad minima permitida es 1.");
            return;
        }

        long simIni, simFin, simTot;
        PrintStream archivo = new PrintStream("mejorada_vs_insercion.dat");
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        System.out.println("\n------------------------------");
        System.out.println("INICIO DE SIMULACION: " + dateFormat.format(new Date()));
        simIni = System.nanoTime();

        XYSeries serieMejorada = new XYSeries("Burbuja Mejorada");
        XYSeries serieInsercion = new XYSeries("Insercion");
        ArrayList<Resultado> resultados = new ArrayList<>();

        if (cant >= 3) {
            int n1 = cant / 3;
            int n2 = 2 * cant / 3;

            int[] arr1 = generarDatos(n1);
            int[] arr2 = arr1.clone();
            long ini = System.nanoTime();
            burbujaMejorada(arr1);
            long fin = System.nanoTime();
            long tMejorada1 = fin - ini;
            ini = System.nanoTime();
            insercion(arr2);
            fin = System.nanoTime();
            long tInsercion1 = fin - ini;
            resultados.add(new Resultado(n1, tMejorada1, tInsercion1));

            arr1 = generarDatos(n2);
            arr2 = arr1.clone();
            ini = System.nanoTime();
            burbujaMejorada(arr1);
            fin = System.nanoTime();
            long tMejorada2 = fin - ini;
            ini = System.nanoTime();
            insercion(arr2);
            fin = System.nanoTime();
            long tInsercion2 = fin - ini;
            resultados.add(new Resultado(n2, tMejorada2, tInsercion2));
        }

        int[] arr1 = generarDatos(cant);
        int[] arr2 = arr1.clone();
        long ini = System.nanoTime();
        int[] statsMejorada = burbujaMejorada(arr1);
        long fin = System.nanoTime();
        long tMejorada = fin - ini;
        ini = System.nanoTime();
        int[] statsInsercion = insercion(arr2);
        fin = System.nanoTime();
        long tInsercion = fin - ini;

        System.out.println("\n--- Comparacion para n = " + cant + " ---");
        System.out.printf("%-20s %-15s %-20s%n", "Algoritmo", "Pasadas", "Comparaciones");
        System.out.printf("%-20s %-15d %-20d%n", "Burbuja Mejorada", statsMejorada[0], statsMejorada[1]);
        System.out.printf("%-20s %-15s %-20d%n", "Insercion", "N/A", statsInsercion[1]);
        System.out.printf("%nTiempo Mejorada: %d ns%n", tMejorada);
        System.out.printf("Tiempo Insercion: %d ns%n", tInsercion);

        if (tMejorada < tInsercion) {
            System.out.println("Resultado: Burbuja Mejorada fue mas rapida.\n");
        } else if (tMejorada > tInsercion) {
            System.out.println("Resultado: Insercion fue mas rapida.\n");
        } else {
            System.out.println("Resultado: Ambos algoritmos tomaron el mismo tiempo.\n");
        }

        resultados.add(new Resultado(cant, tMejorada, tInsercion));

        serieMejorada.add(0, 0);
        serieInsercion.add(0, 0);
        for (Resultado r : resultados) {
            serieMejorada.add(r.n, r.tiempoMejorada);
            serieInsercion.add(r.n, r.tiempoInsercion);
            archivo.println(r.n + " " + r.tiempoMejorada + " " + r.tiempoInsercion);
        }

        archivo.close();
        simFin = System.nanoTime();
        simTot = simFin - simIni;
        System.out.println("FIN DE LA SIMULACION: " + dateFormat.format(new Date()));
        System.out.println("TIEMPO TOTAL DE SIMULACION: " + TimeUnit.NANOSECONDS.toSeconds(simTot) + " s");
        System.out.println("------------------------------\n");

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(serieMejorada);
        dataset.addSeries(serieInsercion);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Burbuja Mejorada vs Insercion",
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

        JFrame frame = new JFrame("Grafica - Burbuja Mejorada vs Insercion");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new ChartPanel(chart));
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * *********************************************************************************************************
     * Nombre Metodo: generarDatos Proposito: Crear un arreglo de enteros
     * aleatorios Variables utilizadas: n, datos[i] Precondicion: n >= 0
     * Postcondicion: Arreglo lleno con valores aleatorios entre 1 y 1000
    *********************************************************************************************************
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
     * Nombre Metodo: burbujaMejorada Proposito: Ordenar un arreglo usando el
     * algoritmo burbuja optimizado Variables utilizadas: pasadas, comparaciones
     * Precondicion: Arreglo A no nulo Postcondicion: Arreglo ordenado; retorna
     * estadisticas
    *********************************************************************************************************
     */
    public static int[] burbujaMejorada(int[] A) {
        int pasadas = 0, comparaciones = 0;
        boolean ordenado = false;
        for (int i = 0; i < A.length && !ordenado; i++) {
            ordenado = true;
            pasadas++;
            for (int j = 0; j < A.length - i - 1; j++) {
                comparaciones++;
                if (A[j] > A[j + 1]) {
                    int tmp = A[j];
                    A[j] = A[j + 1];
                    A[j + 1] = tmp;
                    ordenado = false;
                }
            }
        }
        return new int[]{pasadas, comparaciones};
    }

    /**
     * *********************************************************************************************************
     * Nombre Metodo: insercion Proposito: Ordenar un arreglo usando el
     * algoritmo de insercion Variables utilizadas: comparaciones, i, j
     * Precondicion: Arreglo A no nulo Postcondicion: Arreglo ordenado; retorna
     * comparaciones
    *********************************************************************************************************
     */
    public static int[] insercion(int[] A) {
        int comparaciones = 0;
        for (int i = 1; i < A.length; i++) {
            int key = A[i];
            int j = i - 1;
            while (j >= 0 && A[j] > key) {
                comparaciones++;
                A[j + 1] = A[j];
                j--;
            }
            A[j + 1] = key;
            if (j >= 0) {
                comparaciones++;
            }
        }
        return new int[]{-1, comparaciones};
    }
}
