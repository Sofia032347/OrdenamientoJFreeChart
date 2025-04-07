package ordenamiento;

import org.jfree.chart.*;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.xy.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class InsercionVsIntercalacion {

    public static void main(String[] args) throws FileNotFoundException {
        Scanner sc = new Scanner(System.in);
        int cant;
        do {
            System.out.print("Ingrese la cantidad maxima de datos a procesar (minimo 100): ");
            cant = sc.nextInt();
            if (cant < 100) System.out.println(" El minimo permitido es 100. Intente nuevamente.\n");
        } while (cant < 100);

        long simIni, simFin, simTot;
        long tInsercion, tIntercalacion;
        PrintStream archivo = new PrintStream("insercion_vs_intercalacion.dat");
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        System.out.println("\n------------------------------");
        System.out.println("INICIO DE SIMULACIÓN: " + dateFormat.format(new Date()));
        simIni = System.nanoTime();

        XYSeries serieInsercion = new XYSeries("Inserción");
        XYSeries serieIntercalacion = new XYSeries("Intercalación");

        for (int i = 100; i <= cant; i += 1000) {
            int[] arr1 = generarDatos(i);
            int[] arr2 = arr1.clone();

            long ini = System.nanoTime();
            insercion(arr1);
            long fin = System.nanoTime();
            tInsercion = fin - ini;

            ini = System.nanoTime();
            intercalacion(arr2, 0, arr2.length - 1);
            fin = System.nanoTime();
            tIntercalacion = fin - ini;

            serieInsercion.add(i, tInsercion);
            serieIntercalacion.add(i, tIntercalacion);
            archivo.println(i + " " + tInsercion + " " + tIntercalacion);
        }

        archivo.close();
        simFin = System.nanoTime();
        simTot = simFin - simIni;

        System.out.println("FIN DE LA SIMULACIÓN: " + dateFormat.format(new Date()));
        System.out.println("TIEMPO TOTAL DE SIMULACIÓN: " + TimeUnit.NANOSECONDS.toMinutes(simTot) + " min");
        System.out.println("------------------------------\n");

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(serieInsercion);
        dataset.addSeries(serieIntercalacion);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Inserción vs Intercalación",
                "Cantidad de Datos",
                "Tiempo (ns)",
                dataset
        );

        XYPlot plot = chart.getXYPlot();
        XYSplineRenderer renderer = new XYSplineRenderer();
        renderer.setSeriesStroke(0, new BasicStroke(3.0f));
        renderer.setSeriesStroke(1, new BasicStroke(3.0f));
        renderer.setSeriesShapesVisible(0, false);
        renderer.setSeriesShapesVisible(1, false);
        plot.setRenderer(renderer);

        JFrame frame = new JFrame("Gráfica - Inserción vs Intercalación");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new ChartPanel(chart));
        frame.pack();
        frame.setVisible(true);
    }

    public static int[] generarDatos(int n) {
        int[] datos = new int[n];
        for (int i = 0; i < n; i++) {
            datos[i] = (int)(Math.random() * 1000) + 1;
        }
        return datos;
    }
    
    public static void insercion(int[] A) {
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
        }
        System.out.printf("Inserción: Comparaciones = %d%n", comparaciones);
    }
    public static void intercalacion(int[] A, int low, int high) {
        if (low < high) {
            int mid = (low + high) / 2;
            intercalacion(A, low, mid);
            intercalacion(A, mid + 1, high);
            mezclar(A, low, mid, high);
        }
    }

    public static void mezclar(int[] A, int low, int mid, int high) {
        int[] B = new int[A.length];
        int i = low, j = mid + 1, k = low;
        while (i <= mid && j <= high) {
            if (A[i] <= A[j]) B[k++] = A[i++];
            else B[k++] = A[j++];
        }
        while (i <= mid) B[k++] = A[i++];
        while (j <= high) B[k++] = A[j++];
        for (i = low; i <= high; i++) A[i] = B[i];
    }
}