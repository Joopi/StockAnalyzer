package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;


import java.util.*;


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
    private ComboBox<String> timeSeries;
    @FXML
    private ComboBox<String> dataSeries;
    @FXML
    private ComboBox<String> outputSize;
    @FXML
    private ComboBox<String> timeInterval;
    @FXML
    private CheckBox quickDraw; //improves speed significantly by downsampling, will lose some data.

    private boolean firstQuery = false;

    private Map<String, String> timeSeriesMap = new HashMap<String, String>() {{
        put("TIME_SERIES_DAILY", "Time Series (Daily)");
        put("TIME_SERIES_DAILY_ADJUSTED", "Time Series (Daily)");
        put("TIME_SERIES_WEEKLY", "Weekly Time Series");
        put("TIME_SERIES_WEEKLY_ADJUSTED", "Weekly Adjusted Time Series");
        put("TIME_SERIES_MONTHLY", "Monthly Time Series");
        put("TIME_SERIES_MONTHLY_ADJUSTED", "Monthly Adjusted Time Series");
    }};

    private int metric;

    @FXML
    private ObservableList<String> dataSeriesListDefault =
            FXCollections.observableArrayList("1.open","2.high","3.low","4.close","5.volume");
    @FXML
    private ObservableList<String> dataSeriesListDailyAdjusted =
            FXCollections.observableArrayList("1.open","2.high","3.low","4.close","5.adjusted close", "6.volume", "7.dividend amount", "8.split coefficient");
    @FXML
    private ObservableList<String> dataSeriesListWMAdjusted =
            FXCollections.observableArrayList("1.open","2.high","3.low","4.close","5.adjusted close", "6.volume", "7.dividend amount");


    @FXML
    protected void handleQuery(ActionEvent event) {//On button press
        if (query(event))
            firstQuery = true;
    }

    public void update(ActionEvent event) {//on dataSeries change
        if(firstQuery && dataSeries.getValue() != null) query(event);
        //when switching from /ADJUSTED/ timeSeries to some other that doesn't
        //necessarily have the same data metrics the value in dataSeries will reset to null and have to be manually input again
    }

    public boolean query(ActionEvent event) {
        if (!API_KEY.isEmpty()) {
            if ((timeSeries.getValue() != null) && (dataSeries.getValue() != null) && ((timeInterval.getValue() != null) || (timeInterval.isDisabled()))) {

                JSONHandler jsonHandler = new JSONHandler();
                String key = timeSeries.getValue().matches("TIME_SERIES_INTRADAY") ? "Time Series (" + timeInterval.getValue() + ")" : timeSeriesMap.get(timeSeries.getValue());

                if (jsonHandler.fetchURL("https://www.alphavantage.co/query?function=" +
                                timeSeries.getValue() + "&symbol=" +
                                "MSFT&interval=" +
                                timeInterval.getValue() + "&outputsize=" +
                                outputSize.getValue() + "&apikey=" +
                                API_KEY,
                        key))
                {
                    area.clear();
                    chart.getData().clear();

                    metric = dataSeries.getItems().indexOf(dataSeries.getValue());
                    List<StockEntry> history = quickDraw.isSelected() ? jsonHandler.getHistory(xAxis.getWidth()) : jsonHandler.getHistory();

                    StringBuilder stringBuilder = new StringBuilder();
                    XYChart.Series series = new XYChart.Series<>();
                    Collection<XYChart.Data<String, Double>> samples = new ArrayList<>(history.size());
                    //significant speed improvement by using StringBuilder and a Collection.

                    for (int i = history.size() - 1; i > 0; i--) {
                        StockEntry entry = history.get(i);
                        double value = entry.getSeries().get(metric).getValue();
                        stringBuilder.append(entry.getDate() + " " + value + '\n');
                        samples.add(new XYChart.Data<>(entry.getDate(), value));
                    }
                    area.appendText(stringBuilder.toString());
                    series.getData().setAll(samples);

                    //JavaFX chart is a huge bottleneck for speed, could consider switching over to https://github.com/GSI-CS-CO/chart-fx.
                    //other solution could be to instead draw on a canvas.
                    chart.getData().add(series);
                    return true;
                }
            } else {
                System.out.println("Please ensure that you have a selected a valid metric");
            }
        } else {
            System.out.println("Please set an API_KEY from alphavantage.co");
        }
        return false;
    }

    public void setDataSeriesList(ActionEvent actionEvent)
    {
        String choice = timeSeries.getValue();
        if (choice.matches("TIME_SERIES_INTRADAY")){
            dataSeries.setItems(dataSeriesListDefault);
            timeInterval.setDisable(false);
        } else {
            if (choice.matches("TIME_SERIES_DAILY_ADJUSTED"))
                dataSeries.setItems(dataSeriesListDailyAdjusted);
             else if (choice.matches("TIME_SERIES_WEEKLY_ADJUSTED") || choice.matches("TIME_SERIES_MONTHLY_ADJUSTED"))
                dataSeries.setItems(dataSeriesListWMAdjusted);
            else
                dataSeries.setItems(dataSeriesListDefault);

            timeInterval.setDisable(true); //Time interval is irrelevant here, so we can disable it.
        }
    }



}
