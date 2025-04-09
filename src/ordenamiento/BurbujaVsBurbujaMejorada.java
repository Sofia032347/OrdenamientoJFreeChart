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

public class BurbujaVsBurbujaMejorada {

    static class Resultado {

        int n;
        long tiempoBurbuja;
        long tiempoMejorada;

        /**
         * *********************************************************************************************************
         * Nombre Metodo: Resultado Proposito: Constructor para almacenar
         * resultados de tiempo por algoritmo Variables utilizadas: n,
         * tiempoBurbuja, tiempoMejorada Precondicion: Valores enteros validos
         * Postcondicion: Objeto Resultado inicializado con datos
         * ********************************************************************************************************
         */
        public Resultado(int n, long tiempoBurbuja, long tiempoMejorada) {
            this.n = n;
            this.tiempoBurbuja = tiempoBurbuja;
            this.tiempoMejorada = tiempoMejorada;
        }
    }

    /************************************************************************************************************
     * Nombre Método: main Propósito: Ejecutar la simulación de rendimiento de
     * los algoritmos Burbuja y Burbuja Mejorada, graficar resultados Variables
     * utilizadas: - cant: cantidad de datos ingresados por el usuario -
     * resultados: lista de objetos Resultado con información de tiempo por
     * algoritmo - serieBurbuja, serieMejorada: series para la gráfica -
     * statsBurbuja, statsMejorada: métricas de rendimiento (pasadas y
     * comparaciones) Precondición: El usuario debe ingresar un valor entero
     * mayor o igual a 1 Postcondición: Se muestran en consola los tiempos y
     * métricas de ambos algoritmos, se genera archivo y gráfica
    **********************************************************************************************************/
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.print("Ingrese la cantidad de datos (minimo 1): ");
        int cant = sc.nextInt();
        if (cant < 1) {
            System.out.println("La cantidad minima permitida es 1.");
            return;
        }

        long simIni, simFin, simTot;
        PrintStream archivo = new PrintStream("burbuja_vs_mejorada.dat");
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        System.out.println("\n------------------------------");
        System.out.println("INICIO DE SIMULACION: " + dateFormat.format(new Date()));
        simIni = System.nanoTime();

        XYSeries serieBurbuja = new XYSeries("Burbuja");
        XYSeries serieMejorada = new XYSeries("Burbuja Mejorada");
        ArrayList<Resultado> resultados = new ArrayList<>();

        if (cant >= 3) {
            int n1 = cant / 3;
            int n2 = 2 * cant / 3;

            int[] arr1 = generarDatos(n1);
            int[] arr2 = arr1.clone();
            long ini = System.nanoTime();
            burbuja(arr1);
            long fin = System.nanoTime();
            long tBurbuja1 = fin - ini;
            ini = System.nanoTime();
            burbujaMejorada(arr2);
            fin = System.nanoTime();
            long tMejorada1 = fin - ini;
            resultados.add(new Resultado(n1, tBurbuja1, tMejorada1));

            arr1 = generarDatos(n2);
            arr2 = arr1.clone();
            ini = System.nanoTime();
            burbuja(arr1);
            fin = System.nanoTime();
            long tBurbuja2 = fin - ini;
            ini = System.nanoTime();
            burbujaMejorada(arr2);
            fin = System.nanoTime();
            long tMejorada2 = fin - ini;
            resultados.add(new Resultado(n2, tBurbuja2, tMejorada2));
        }

        int[] arr1 = generarDatos(cant);
        int[] arr2 = arr1.clone();
        long ini = System.nanoTime();
        int[] statsBurbuja = burbuja(arr1);
        long fin = System.nanoTime();
        long tBurbuja = fin - ini;

        ini = System.nanoTime();
        int[] statsMejorada = burbujaMejorada(arr2);
        fin = System.nanoTime();
        long tMejorada = fin - ini;

        System.out.println("\n--- Comparacion para n = " + cant + " ---");
        System.out.printf("%-20s %-15s %-20s%n", "Algoritmo", "Pasadas", "Comparaciones");
        System.out.printf("%-20s %-15d %-20d%n", "Burbuja", statsBurbuja[0], statsBurbuja[1]);
        System.out.printf("%-20s %-15d %-20d%n", "Burbuja Mejorada", statsMejorada[0], statsMejorada[1]);
        System.out.printf("%nTiempo Burbuja: %d ns%n", tBurbuja);
        System.out.printf("Tiempo Mejorada: %d ns%n", tMejorada);

        if (tBurbuja < tMejorada) {
            System.out.println("Resultado: Burbuja fue mas rapida.\n");
        } else if (tBurbuja > tMejorada) {
            System.out.println("Resultado: Burbuja Mejorada fue mas rapida.\n");
        } else {
            System.out.println("Resultado: Ambos algoritmos tomaron el mismo tiempo.\n");
        }

        resultados.add(new Resultado(cant, tBurbuja, tMejorada));

        serieBurbuja.add(0, 0);
        serieMejorada.add(0, 0);
        for (Resultado r : resultados) {
            serieBurbuja.add(r.n, r.tiempoBurbuja);
            serieMejorada.add(r.n, r.tiempoMejorada);
            archivo.println(r.n + " " + r.tiempoBurbuja + " " + r.tiempoMejorada);
        }
        archivo.close();

        simFin = System.nanoTime();
        simTot = simFin - simIni;

        System.out.println("FIN DE LA SIMULACION: " + dateFormat.format(new Date()));
        System.out.println("TIEMPO TOTAL DE SIMULACION: " + TimeUnit.NANOSECONDS.toSeconds(simTot) + " s");
        System.out.println("------------------------------\n");

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(serieBurbuja);
        dataset.addSeries(serieMejorada);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Burbuja vs Burbuja Mejorada",
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

        JFrame frame = new JFrame("Grafica - Burbuja vs Mejorada");
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
     * ********************************************************************************************************
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
     * Nombre Metodo: burbuja Proposito: Ordenar un arreglo usando el algoritmo
     * burbuja Variables utilizadas: pasadas, comparaciones, i, j Precondicion:
     * Arreglo A no nulo Postcondicion: Arreglo A ordenado; retorna estadisticas
     * ********************************************************************************************************
     */
    public static int[] burbuja(int[] A) {
        int pasadas = 0, comparaciones = 0;
        for (int i = 0; i < A.length; i++) {
            pasadas++;
            for (int j = 0; j < A.length - 1; j++) {
                comparaciones++;
                if (A[j] > A[j + 1]) {
                    int tmp = A[j];
                    A[j] = A[j + 1];
                    A[j + 1] = tmp;
                }
            }
        }
        return new int[]{pasadas, comparaciones};
    }

    /**
     * *********************************************************************************************************
     * Nombre Metodo: burbujaMejorada Proposito: Ordenar un arreglo usando el
     * algoritmo burbuja con optimizacion Variables utilizadas: pasadas,
     * comparaciones, ordenado, i, j Precondicion: Arreglo A no nulo
     * Postcondicion: Arreglo A ordenado; retorna estadisticas
     * ********************************************************************************************************
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
}
