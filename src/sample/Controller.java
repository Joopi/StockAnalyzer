package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TextArea;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Event handler for the button
 * JavaFX uses Reflection to invoke handleQuery method.
 */
public class Controller {
    //set API_KEY before you run!
    final String API_KEY = "";

    @FXML
    private TextArea area;
    @FXML
    private LineChart chart;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;

    //TODO add handlers for comboboxes to change data metric
    private int metric = 0;

    @FXML
    protected void handleQuery(ActionEvent event) {
        if (!API_KEY.isEmpty()) {
            JSONHandler jsonHandler = new JSONHandler();

            //TODO fill URL parameters with comboboxes
            if (jsonHandler.fetchURL("https://www.alphavantage.co/query?function=TIME_SERIES_MONTHLY_ADJUSTED&symbol=MSFT&interval=15min&outputsize=full&apikey=" + API_KEY)) {
                area.clear();
                chart.getData().clear();

                List<StockEntry> history = jsonHandler.getHistory();
                XYChart.Series series = new XYChart.Series<>();

                for (int i = history.size()-1; i > 0; i--) {
                    StockEntry entry = history.get(i);
                    double value = entry.getSeries().get(metric).getValue();
                    area.appendText(entry.getDate() + " " + value + '\n');
                    series.getData().add(new XYChart.Data<>(entry.getDate(), value));
                }
                chart.setCreateSymbols(false);
                chart.getData().add(series);
            }
        } else {
            System.out.println("Please set an API_KEY from alphavantage.co");
        }
    }
}
