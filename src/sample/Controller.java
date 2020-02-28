package sample;




import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;


import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    @FXML
    private ComboBox<String> Time_Series;
    @FXML
    private ComboBox<String> Data_Series;
    @FXML
    private ComboBox<String> Output_Size;
    @FXML
    private ComboBox<String> Time_Interval;

    Map<String, String> timeSeriesMap  = new HashMap<String, String>() {{
        put("TIME_SERES_INTRADAY", "Time Series ");
        put("TIME_SERIES_DAILY", "Time Series (Daily)");
        put("TIME_SERIES_DAILY_ADJUSTED", "Time Series (Daily)");
        put("TIME_SERIES_WEEKLY", "Weekly Time Series");
        put("TIME_SERIES_WEEKLY_ADJUSTED", "Adjusted Weekly Time Series");
        put("TIME_SERIES_MONTHLY", "Monthly Time Series");
        put("TIME_SERIES_MONTHLY_ADJUSTED", "Adjusted Monthly Time Series");
    }};
    private int metric;


    @FXML
    protected void handleQuery(ActionEvent event) {
        if (!API_KEY.isEmpty()) {
            JSONHandler jsonHandler = new JSONHandler();

            //TODO fill URL parameters with comboboxes
            if (jsonHandler.fetchURL("https://www.alphavantage.co/query?function=" +
                    Time_Series.getValue() +"&symbol=" +
                    "MSFT&interval=" +
                    Time_Interval.getValue()+"&outputsize=" +
                    Output_Size.getValue() + "&apikey=" +
                    API_KEY,
                    timeSeriesMap.get(Time_Series.getValue()))) {
                area.clear();
                chart.getData().clear();

                //metric change depending on combobox
                metric = Data_Series.getItems().indexOf(Data_Series.getValue());

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

    public String getRoots()
    {
        int index = Time_Series.getItems().indexOf(Time_Series.getValue());
        String[] jsonRoots = {"Time Series (15min)", "Time Series (Daily)",
                        "Time Series (Daily)", "Weekly Time Series", "Weekly Adjusted Time Series",
                        "Monthly Time Series", "Monthly Adjusted Time Series"};
        return jsonRoots[2];
    }



}
