package ordenamiento;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.util.Random;
import java.util.Scanner;
import java.awt.BasicStroke;

public class BurbujaVsBurbujaMejoradaJFreeChart {

    public static void burbuja(int[] arreglo) {
        int n = arreglo.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1 - i; j++) {
                if (arreglo[j] > arreglo[j + 1]) {
                    int temp = arreglo[j];
                    arreglo[j] = arreglo[j + 1];
                    arreglo[j + 1] = temp;
                }
            }
        }
    }

    public static void burbujaMejorada(int[] arreglo) {
        int n = arreglo.length;
        boolean intercambio;
        for (int i = 0; i < n - 1; i++) {
            intercambio = false;
            for (int j = 0; j < n - 1 - i; j++) {
                if (arreglo[j] > arreglo[j + 1]) {
                    int temp = arreglo[j];
                    arreglo[j] = arreglo[j + 1];
                    arreglo[j + 1] = temp;
                    intercambio = true;
                }
            }
            if (!intercambio) break; // Si no hubo intercambios, el arreglo ya está ordenado
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int cant;

        while (true) {
            try {
                System.out.print("Ingrese la cantidad de elementos para ordenar (mínimo 100): ");
                cant = Integer.parseInt(scanner.nextLine());
                if (cant >= 100) break;
                else System.out.println("Por favor, ingrese un número mayor o igual a 100.");
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número válido.");
            }
        }

        long inicio, fin, tiempoBurbuja, tiempoBurbujaMejorada;
        System.out.println("\nN° de elementos | Burbuja (ns) | Burbuja Mejorada (ns)");

        XYSeries serieBurbuja = new XYSeries("Burbuja");
        XYSeries serieBurbujaMejorada = new XYSeries("Burbuja Mejorada");

        for (int i = 100; i <= cant; i += 50) { // Se aumenta de 50 en 50 para notar diferencias
            int[] arreglo1 = new int[i];
            int[] arreglo2 = new int[i];

            llenarDatos(arreglo1);
            System.arraycopy(arreglo1, 0, arreglo2, 0, i);

            inicio = System.nanoTime();
            burbuja(arreglo1);
            fin = System.nanoTime();
            tiempoBurbuja = fin - inicio;

            inicio = System.nanoTime();
            burbujaMejorada(arreglo2);
            fin = System.nanoTime();
            tiempoBurbujaMejorada = fin - inicio;

            serieBurbuja.add(i, tiempoBurbuja);
            serieBurbujaMejorada.add(i, tiempoBurbujaMejorada);

            System.out.printf("%14d | %13d | %20d %n", i, tiempoBurbuja, tiempoBurbujaMejorada);
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(serieBurbuja);
        dataset.addSeries(serieBurbujaMejorada);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Comparacion: Burbuja vs Burbuja Mejorada",
                "Cantidad de Datos",
                "Tiempo en nanosegundos",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        // Se asegura que solo se muestren las líneas y sean gruesas
        renderer.setSeriesLinesVisible(0, true);
        renderer.setSeriesShapesVisible(0, false);  // Deshabilitar puntos
        renderer.setSeriesLinesVisible(1, true);
        renderer.setSeriesShapesVisible(1, false);  // Deshabilitar puntos

        // Ajustar el grosor de las líneas para que sean gruesas
        renderer.setSeriesStroke(0, new BasicStroke(3.0f)); // Línea más gruesa
        renderer.setSeriesStroke(1, new BasicStroke(3.0f)); // Línea más gruesa

        plot.setRenderer(renderer);
        NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
        domainAxis.setAutoRangeIncludesZero(false);
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setAutoRangeIncludesZero(false);

        JFrame frame = new JFrame("Gráfico de Comparación");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new ChartPanel(chart));
        frame.pack();
        frame.setVisible(true);
    }

    public static void llenarDatos(int[] A) {
        Random rand = new Random();
        for (int i = 0; i < A.length; i++) {
            A[i] = rand.nextInt(1000) + 1;
        }
    }
}
