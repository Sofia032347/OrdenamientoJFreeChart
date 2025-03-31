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
import java.io.*;
import java.util.*;

public class InsercionVsIntercalacion extends JFrame {

    public InsercionVsIntercalacion(XYSeries serie1, XYSeries serie2) {
        // Crear el dataset con ambas series
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(serie1);
        dataset.addSeries(serie2);

        // Crear la gráfica
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Comparación: Inserción vs Intercalación",
                "Cantidad de Datos",
                "Tiempo en nanosegundos",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        // Personalización de la gráfica
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        plot.setRenderer(renderer);

        // Configurar ejes
        NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
        domainAxis.setAutoRangeIncludesZero(false);
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setAutoRangeIncludesZero(false);

        // Mostrar la ventana con la gráfica
        ChartPanel panel = new ChartPanel(chart);
        setContentPane(panel);
        setTitle("Gráfica - Inserción vs Intercalación");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int cant;

        while (true) {
            try {
                System.out.print("Ingrese la cantidad de elementos para ordenar: ");
                cant = Integer.parseInt(scanner.nextLine());
                if (cant > 0) break;
                else System.out.println("Debe ingresar un número mayor a 0.");
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingrese un número válido.");
            }
        }

        XYSeries serieInsercion = new XYSeries("Inserción");
        XYSeries serieIntercalacion = new XYSeries("Intercalación");

        System.out.println("\nN° de elementos | Inserción (ns) | Intercalación (ns)");

        for (int i = 1; i <= cant; i++) {
            int[] arreglo1 = new int[i];
            int[] arreglo2 = new int[i];
            llenarDatos(arreglo1);
            System.arraycopy(arreglo1, 0, arreglo2, 0, i);

            long inicio = System.nanoTime();
            insercion(arreglo1);
            long fin = System.nanoTime();
            long tiempoInsercion = fin - inicio;

            inicio = System.nanoTime();
            intercalacion(arreglo2);
            fin = System.nanoTime();
            long tiempoIntercalacion = fin - inicio;

            serieInsercion.add(i, tiempoInsercion);
            serieIntercalacion.add(i, tiempoIntercalacion);

            // Imprimir los resultados en la consola
            System.out.printf("%14d | %13d | %20d %n", i, tiempoInsercion, tiempoIntercalacion);
        }

        // Crear y mostrar la gráfica
        new InsercionVsIntercalacion(serieInsercion, serieIntercalacion);
    }

    public static void insercion(int[] arreglo) {
        for (int i = 1; i < arreglo.length; i++) {
            int clave = arreglo[i];
            int j = i - 1;
            while (j >= 0 && arreglo[j] > clave) {
                arreglo[j + 1] = arreglo[j];
                j--;
            }
            arreglo[j + 1] = clave;
        }
    }

    public static void intercalacion(int[] arreglo) {
        if (arreglo.length < 2) return;
        int mitad = arreglo.length / 2;
        int[] izquierda = Arrays.copyOfRange(arreglo, 0, mitad);
        int[] derecha = Arrays.copyOfRange(arreglo, mitad, arreglo.length);

        intercalacion(izquierda);
        intercalacion(derecha);
        merge(arreglo, izquierda, derecha);
    }

    public static void merge(int[] arreglo, int[] izquierda, int[] derecha) {
        int i = 0, j = 0, k = 0;
        while (i < izquierda.length && j < derecha.length) {
            if (izquierda[i] <= derecha[j]) {
                arreglo[k++] = izquierda[i++];
            } else {
                arreglo[k++] = derecha[j++];
            }
        }
        while (i < izquierda.length) {
            arreglo[k++] = izquierda[i++];
        }
        while (j < derecha.length) {
            arreglo[k++] = derecha[j++];
        }
    }

    public static void llenarDatos(int[] A) {
        Random rand = new Random();
        for (int i = 0; i < A.length; i++) {
            A[i] = rand.nextInt(1000) + 1;
        }
    }
}