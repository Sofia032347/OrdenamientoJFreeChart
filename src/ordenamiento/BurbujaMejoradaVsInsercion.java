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

public class BurbujaMejoradaVsInsercion {

    public static void main(String[] args) throws FileNotFoundException {
        Scanner sc = new Scanner(System.in);
        int cant;
        do {
            System.out.print("Ingrese la cantidad maxima de datos a procesar (minimo 10000): ");
            cant = sc.nextInt();
            if (cant < 10000) System.out.println("El minimo permitido es 10000. Intente nuevamente.\n");
        } while (cant < 10000);

        long simIni, simFin, simTot;
        long tMejorada, tInsercion;
        PrintStream archivo = new PrintStream("mejorada_vs_insercion.dat");
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        System.out.println("\n------------------------------");
        System.out.println("INICIO DE SIMULACION: " + dateFormat.format(new Date()));
        simIni = System.nanoTime();

        XYSeries serieMejorada = new XYSeries("Burbuja Mejorada");
        XYSeries serieInsercion = new XYSeries("Inserción");

        for (int i = 100; i <= cant; i += 1000) {
            int[] arr1 = generarDatos(i);
            int[] arr2 = arr1.clone();

            long ini = System.nanoTime();
            burbujaMejorada(arr1);
            long fin = System.nanoTime();
            tMejorada = fin - ini;

            ini = System.nanoTime();
            insercion(arr2);
            fin = System.nanoTime();
            tInsercion = fin - ini;

            serieMejorada.add(i, tMejorada);
            serieInsercion.add(i, tInsercion);
            archivo.println(i + " " + tMejorada + " " + tInsercion);
        }

        archivo.close();
        simFin = System.nanoTime();
        simTot = simFin - simIni;

        System.out.println("FIN DE LA SIMULACION: " + dateFormat.format(new Date()));
        System.out.println("TIEMPO TOTAL DE SIMULACION: " + TimeUnit.NANOSECONDS.toMinutes(simTot) + " min");
        System.out.println("------------------------------\n");

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(serieMejorada);
        dataset.addSeries(serieInsercion);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Burbuja Mejorada vs Inserción",
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

        JFrame frame = new JFrame("Gráfica - Burbuja Mejorada vs Inserción");
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

    public static void burbujaMejorada(int[] A) {
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
        System.out.printf("Mejorada: Pasadas = %d | Comparaciones = %d%n", pasadas, comparaciones);
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
        System.out.printf("Insercion: Comparaciones = %d%n", comparaciones);
    }
}