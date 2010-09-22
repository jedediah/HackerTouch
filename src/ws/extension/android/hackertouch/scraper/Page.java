package ws.extension.android.hackertouch.scraper;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.ccil.cowan.tagsoup.Parser;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import ws.extension.android.log.Lg;

import java.io.IOException;

public class Page extends DefaultHandler {

    public static final int UNLOADED = 1, LOADING = 2, LOADED = 3;

    private String uri;
    private int state = UNLOADED;
    private PageListener listener;

    public Page(String uri) {
        this.uri = uri;
    }

    protected HttpClient getHttpClient() {
        return new DefaultHttpClient();
    }

    protected Parser getParser() {
        Parser p = new Parser();
        p.setContentHandler(this);
        return p;
    }

    public String getUri() {
        return uri;
    }

    protected void setState(int state) {
        if (this.state != state) {
            this.state = state;
            if (listener != null)
                listener.stateChanged(state);
        }
    }

    public void get() {
        try {
            String uri = getUri();
            setState(LOADING);
            Lg.i("Starting load of "+uri);
            HttpEntity entity = getHttpClient().execute(new HttpGet(uri)).getEntity();
            if (entity == null) {
                Lg.w("No entity body for "+uri);
                setState(UNLOADED);
            } else {
                getParser().parse(new InputSource(entity.getContent()));
                Lg.i("Finished loading "+uri);
                setState(LOADED);
            }
        }
        catch (IOException e) {
            Lg.e(e);
            setState(UNLOADED);
        }
        catch (SAXException e) {
            Lg.e(e);
            setState(UNLOADED);
        }
    }

    public int getState() { return state; }
    public boolean isLoaded() { return state == LOADED; }
    public boolean isLoading() { return state == LOADING; }

    public void setListener(PageListener l) {
        this.listener = l;
    }
}
