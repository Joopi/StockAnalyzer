package sample;

import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;


public class JSONHandler {

    private JSONObject data;


    public JSONHandler(){}

    public boolean fetchURL(String address, String key)
    {

        try {
            URL url = new URL(address);
            try {
                JSONTokener tokener = new JSONTokener(url.openStream());
                JSONObject root = new JSONObject(tokener);
                if (root.length() > 1) {
                    data = root.getJSONObject(key);
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<StockEntry> getHistory()
    {
        if (data == null || data.length() == 0)
            return null;

        List<StockEntry> result = new ArrayList<>();

        data.keys().forEachRemaining(key -> {
            JSONObject object = data.getJSONObject(key);
            result.add(new StockEntry(key));

            //probably should not cut corners by using lambda expressions and returning last item each iteration..
            object.keys().forEachRemaining(s -> result.get(result.size()-1).addSeries(new StockSeries(s, object.getDouble(s))));
        });

        return result;
    }

    /**
        alternative to getHistory, used for speed improvement with larger datasets.
        downsamples data entries to the canvas width.
        since x axis represents time/date, there shouldn't be more than one entry per column.
     **/
    public List<StockEntry> getHistory(double canvasWidth){
        int len = data.length();
        if (len <= canvasWidth)
            return getHistory();

        if (data == null || len == 0)
            return null;

        List<StockEntry> result = new ArrayList<>();

        double skip = len/canvasWidth;
        //skip = Math.Max(skip,1); is no longer required since we already ensure there are more data entries than the width of the canvas.

        Object[] keys = data.keySet().toArray();
        double l = (double) keys.length;
        for (double i = 0; i < l; i += skip) {
            String key = (String)keys[(int) i];
            JSONObject object = data.getJSONObject(key);
            result.add(new StockEntry(key));

            object.keys().forEachRemaining(s -> result.get(result.size()-1).addSeries(new StockSeries(s, object.getDouble(s))));
        }

        return result;
    }




}
