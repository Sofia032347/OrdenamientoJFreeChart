package ordenamiento;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import javax.swing.*;
import java.util.*;

public class BurbujaMejoradaVsInsercion {

    public static void burbujaMejorada(int[] arreglo) {
        boolean intercambio;
        for (int i = 0; i < arreglo.length - 1; i++) {
            intercambio = false;
            for (int j = 0; j < arreglo.length - 1 - i; j++) {
                if (arreglo[j] > arreglo[j + 1]) {
                    int temp = arreglo[j];
                    arreglo[j] = arreglo[j + 1];
                    arreglo[j + 1] = temp;
                    intercambio = true;
                }
            }
            if (!intercambio) {
                break;
            }
        }
    }

    public static void insercion(int[] arreglo) {
        for (int i = 1; i < arreglo.length; i++) {
            int key = arreglo[i];
            int j = i - 1;
            while (j >= 0 && arreglo[j] > key) {
                arreglo[j + 1] = arreglo[j];
                j--;
            }
            arreglo[j + 1] = key;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int cant;

        while (true) {
            try {
                System.out.print("Ingrese la cantidad de elementos para ordenar: ");
                cant = Integer.parseInt(scanner.nextLine());
                if (cant > 0) {
                    break;
                } else {
                    System.out.println("Debe ingresar un número mayor a 0.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingrese un número válido.");
            }
        }

        long inicio, fin, tiempoBurbujaMejorada, tiempoInsercion;
        System.out.println("\nN° de elementos | Burbuja Mejorada (ns) | Inserción (ns)");

        XYSeries serieBurbujaMejorada = new XYSeries("Burbuja Mejorada");
        XYSeries serieInsercion = new XYSeries("Inserción");

        for (int i = 1; i <= cant; i++) {
            int[] arreglo1 = new int[i];
            int[] arreglo2 = new int[i];
            llenarDatos(arreglo1);
            System.arraycopy(arreglo1, 0, arreglo2, 0, i);

            inicio = System.nanoTime();
            burbujaMejorada(arreglo1);
            fin = System.nanoTime();
            tiempoBurbujaMejorada = fin - inicio;

            inicio = System.nanoTime();
            insercion(arreglo2);
            fin = System.nanoTime();
            tiempoInsercion = fin - inicio;

            serieBurbujaMejorada.add(i, tiempoBurbujaMejorada);
            serieInsercion.add(i, tiempoInsercion);

            System.out.printf("%14d | %20d | %15d %n", i, tiempoBurbujaMejorada, tiempoInsercion);
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(serieBurbujaMejorada);
        dataset.addSeries(serieInsercion);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Comparación: Burbuja Mejorada vs Inserción",
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

        System.out.println("\nVentana de la gráfica abierta. Presione ENTER para cerrar...");
        scanner.nextLine();
    }

    public static void llenarDatos(int[] A) {
        Random rand = new Random();
        for (int i = 0; i < A.length; i++) {
            A[i] = rand.nextInt(1000) + 1;
        }
    }
}
