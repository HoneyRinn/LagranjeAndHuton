package org.example;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import javax.swing.*;
import java.awt.*;

public class LagrangeNewtonPlot extends JFrame {

    public LagrangeNewtonPlot(String title) {
        super(title);

        // Создание графика
        XYSeriesCollection dataset = createDataset();
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Интерполяция полиномов Лагранжа и Ньютона",
                "X",
                "Y",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        // Настройка панели с графиком
        ChartPanel panel = new ChartPanel(chart);
        panel.setPreferredSize(new Dimension(800, 600));
        setContentPane(panel);
    }

    // Метод для вычисления полинома Лагранжа
    public static double lagrangeInterpolation(double[] xPoints, double[] yPoints, double x) {
        int n = xPoints.length;
        double result = 0.0;

        for (int i = 0; i < n; i++) {
            double term = yPoints[i];
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    term *= (x - xPoints[j]) / (xPoints[i] - xPoints[j]);
                }
            }
            result += term;
        }

        return result;
    }

    // Метод для вычисления разделённых разностей (для Ньютона)
    public static double[] dividedDifferences(double[] xPoints, double[] yPoints) {
        int n = xPoints.length;
        double[] coef = new double[n];
        double[][] divDiffTable = new double[n][n];

        // Инициализируем первую колонку таблицы разделённых разностей значениями yPoints
        for (int i = 0; i < n; i++) {
            divDiffTable[i][0] = yPoints[i];
        }

        // Заполняем таблицу разделённых разностей
        for (int j = 1; j < n; j++) {
            for (int i = 0; i < n - j; i++) {  //f[Xi, Xj]
                divDiffTable[i][j] = (divDiffTable[i + 1][j - 1] - divDiffTable[i][j - 1]) / (xPoints[i + j] - xPoints[i]);
            }
        }

        // Коэффициенты полинома Ньютона (первая строка таблицы)
        for (int i = 0; i < n; i++) {
            coef[i] = divDiffTable[0][i];
        }

        return coef;
    }

    // Метод для вычисления значения полинома Ньютона в точке x
    public static double newtonInterpolation(double[] xPoints, double[] coef, double x) {
        int n = coef.length;
        double result = coef[0];
        double term = 1.0;

        for (int i = 1; i < n; i++) {
            term *= (x - xPoints[i - 1]);
            result += coef[i] * term;
        }

        return result;
    }

    // Метод для создания данных для графиков Лагранжа и Ньютона
    private XYSeriesCollection createDataset() {
        XYSeries lagrangeSeries = new XYSeries("Полином Лагранжа");
        XYSeries newtonSeries = new XYSeries("Полином Ньютона");

        // Заданные точки
        double[] xPoints = {-1, 2, 5};
        double[] yPoints = {4, 3, 4};

        // Вычисляем коэффициенты полинома Ньютона
        double[] newtonCoef = dividedDifferences(xPoints, yPoints);

        // Заполняем значения для графиков
        for (double x = xPoints[0]; x <= xPoints[xPoints.length - 1]; x += 0.1) {
            // Вычисляем значения полиномов Лагранжа и Ньютона
            double lagrangeY = lagrangeInterpolation(xPoints, yPoints, x);
            double newtonY = newtonInterpolation(xPoints, newtonCoef, x);

            // Добавляем точки в серии
            lagrangeSeries.add(x, lagrangeY);
            newtonSeries.add(x, newtonY);
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(lagrangeSeries);
        dataset.addSeries(newtonSeries);

        // Добавляем исходные точки
        XYSeries points = new XYSeries("Заданные точки");
        for (int i = 0; i < xPoints.length; i++) {
            points.add(xPoints[i], yPoints[i]);
        }
        dataset.addSeries(points);

        return dataset;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LagrangeNewtonPlot example = new LagrangeNewtonPlot("Полиномы Лагранжа и Ньютона");
            example.setSize(800, 600);
            example.setLocationRelativeTo(null);
            example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            example.setVisible(true);
        });
    }
}
