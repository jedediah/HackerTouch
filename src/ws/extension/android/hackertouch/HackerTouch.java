package ws.extension.android.hackertouch;

import android.app.Activity;
import android.os.Bundle;
import ws.extension.android.hackertouch.view.IndexView;
import ws.extension.android.log.Lg;

public class HackerTouch extends Activity
{
    BrowserView webView;
    IndexView scraperView;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Lg.setTag("HackerTouch");
        scraperView = new IndexView(this);
        setContentView(scraperView);
    }

    @Override
    public void onBackPressed() {
        if (webView == null || !webView.onBackKey())
            super.onBackPressed();
    }
}
