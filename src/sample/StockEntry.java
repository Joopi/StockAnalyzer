package sample;

import java.util.ArrayList;
import java.util.List;

public class StockEntry {
    private String date;
    private List<StockSeries> series;

    public StockEntry(String date)
    {
        this.date = date;
        series = new ArrayList<>();
    }

    public String getDate() {
        return date;
    }

    public List<StockSeries> getSeries() {
        return series;
    }

    public void addSeries(StockSeries serie)
    {
        series.add(serie);
    }
}
