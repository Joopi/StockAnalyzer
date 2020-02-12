package sample;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JSONHandler {

    private JSONObject data;

    public JSONHandler(){}

    public boolean fetchURL(String url)
    {
        try {
            URI uri = new URI(url);
            try {
                JSONTokener tokener = new JSONTokener(uri.toURL().openStream());
                JSONObject root = new JSONObject(tokener);
                if (root.length() > 1) {
                    //TODO change JSON key depending on combobox.
                    data = root.getJSONObject("Monthly Adjusted Time Series");
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (URISyntaxException e) {
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
}
