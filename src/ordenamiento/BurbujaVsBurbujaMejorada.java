package ordenamiento;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import javax.swing.*;
import java.awt.*;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class BurbujaVsBurbujaMejorada {

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        int cant;
        do {
            System.out.print("Ingrese la cantidad maxima de datos a procesar (minimo 100): ");
            cant = sc.nextInt();
            if (cant < 100) {
                System.out.println("El minimo permitido es 100. Intente nuevamente.\n");
            }
        } while (cant < 100);

        long simIni, simFin, simTot;
        long tBurbuja, tMejorada;
        PrintStream archivo = new PrintStream("burbuja_vs_mejorada.dat");
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        System.out.println("\n------------------------------");
        System.out.println("INICIO DE SIMULACION: " + dateFormat.format(new Date()));
        simIni = System.nanoTime();

        XYSeries serieBurbuja = new XYSeries("Burbuja");
        XYSeries serieMejorada = new XYSeries("Burbuja Mejorada");

        for (int i = 100; i <= cant; i += 100) {
            int[] arr1 = generarDatos(i);
            int[] arr2 = arr1.clone();

            long ini = System.nanoTime();
            burbuja(arr1);
            long fin = System.nanoTime();
            tBurbuja = fin - ini;

            ini = System.nanoTime();
            burbujaMejorada(arr2);
            fin = System.nanoTime();
            tMejorada = fin - ini;

            serieBurbuja.add(i, tBurbuja);
            serieMejorada.add(i, tMejorada);

            archivo.println(i + " " + tBurbuja + " " + tMejorada);
        }

        archivo.close();
        simFin = System.nanoTime();
        simTot = simFin - simIni;

        System.out.println("FIN DE LA SIMULACIÓN: " + dateFormat.format(new Date()));
        System.out.println("TIEMPO TOTAL DE SIMULACIÓN: " + TimeUnit.NANOSECONDS.toMinutes(simTot) + " min");
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
        renderer.setSeriesStroke(0, new BasicStroke(3.0f));
        renderer.setSeriesStroke(1, new BasicStroke(3.0f));
        renderer.setSeriesShapesVisible(0, false);
        renderer.setSeriesShapesVisible(1, false);
        plot.setRenderer(renderer);

        JFrame frame = new JFrame("Gráfica - Burbuja vs Mejorada");
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

    public static void burbuja(int[] A) {
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
        System.out.printf("Burbuja: Pasadas = %d | Comparaciones = %d%n", pasadas, comparaciones);
    }

    public static void burbujaMejorada(int[] A) {
        int pasadas = 0, comparaciones = 0;
        boolean ordenado = false;
        for (int i = 0; i < A.length && !ordenado; i++) {
            ordenado = true; // Bandera para detectar si ya está ordenado
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
}