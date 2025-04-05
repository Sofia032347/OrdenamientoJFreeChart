package ordenamiento;

import org.jfree.chart.*;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.xy.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class InsercionVsIntercalacion {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Ingrese la cantidad máxima de datos a procesar: ");
        int cant = sc.nextInt();

        long simIni, simFin, simTot;
        long tInsercion, tMerge;
        PrintStream archivo = new PrintStream("insercion_vs_intercalacion.dat");
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        System.out.println("\n------------------------------");
        System.out.println("INICIO DE SIMULACION: " + dateFormat.format(new Date()));
        simIni = System.nanoTime();

        XYSeries serieInsercion = new XYSeries("Inserción");
        XYSeries serieMerge = new XYSeries("Intercalación");

        for (int i = 1000; i <= cant; i += 1000) {
            int[] arr1 = generarDatos(i);
            int[] arr2 = arr1.clone();

            long ini = System.nanoTime();
            insercion(arr1);
            long fin = System.nanoTime();
            tInsercion = fin - ini;

            ini = System.nanoTime();
            mergeSort(arr2, 0, arr2.length - 1);
            fin = System.nanoTime();
            tMerge = fin - ini;

            serieInsercion.add(i, tInsercion);
            serieMerge.add(i, tMerge);

            archivo.println(i + " " + tInsercion + " " + tMerge);
        }

        archivo.close();
        simFin = System.nanoTime();
        simTot = simFin - simIni;

        System.out.println("FIN DE LA SIMULACION: " + dateFormat.format(new Date()));
        System.out.println("TIEMPO TOTAL DE SIMULACION: " + java.util.concurrent.TimeUnit.NANOSECONDS.toMinutes(simTot) + " min");
        System.out.println("------------------------------\n");

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(serieInsercion);
        dataset.addSeries(serieMerge);

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
        int pasadas = 0, comparaciones = 0;
        for (int i = 1; i < A.length; i++) {
            int key = A[i];
            int j = i - 1;
            pasadas++;
            while (j >= 0 && A[j] > key) {
                comparaciones++;
                A[j + 1] = A[j];
                j--;
            }
            comparaciones++;
            A[j + 1] = key;
        }
        System.out.printf("Inserción: Pasadas = %d | Comparaciones = %d%n", pasadas, comparaciones);
    }

    public static void mergeSort(int[] A, int izq, int der) {
        if (izq < der) {
            int mid = (izq + der) / 2;
            mergeSort(A, izq, mid);
            mergeSort(A, mid + 1, der);
            intercalar(A, izq, mid, der);
        }
    }

    public static void intercalar(int[] A, int izq, int mid, int der) {
        int n1 = mid - izq + 1;
        int n2 = der - mid;

        int[] L = new int[n1];
        int[] R = new int[n2];

        System.arraycopy(A, izq, L, 0, n1);
        System.arraycopy(A, mid + 1, R, 0, n2);

        int i = 0, j = 0, k = izq;
        while (i < n1 && j < n2) {
            if (L[i] <= R[j]) {
                A[k++] = L[i++];
            } else {
                A[k++] = R[j++];
            }
        }

        while (i < n1) {
            A[k++] = L[i++];
        }
        while (j < n2) {
            A[k++] = R[j++];
        }
    }
}
