package sample;

public class StockSeries {
    private String series;
    private double value;

    public StockSeries(String series, double value)
    {
        this.series = series;
        this.value = value;
    }

    public String getSeries() {
        return series;
    }

    public double getValue() {
        return value;
    }
}
