package ws.extension.android.hackertouch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.*;
import android.widget.*;
import ws.extension.android.log.Lg;

public class BrowserView extends LinearLayout implements View.OnTouchListener, TextView.OnEditorActionListener {

    private class LocationView extends LinearLayout {

        private static final int LOCATION_EDIT_TYPE = EditorInfo.TYPE_CLASS_TEXT |
                                                      EditorInfo.TYPE_TEXT_VARIATION_URI;
        private ImageView iconView;
        private EditText textView;

        public LocationView(Context context) {
            super(context);
            setOrientation(HORIZONTAL);

            textView = new EditText(context);
            textView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
            textView.setSingleLine();
            textView.setImeOptions(EditorInfo.IME_ACTION_GO);
            textView.setInputType(LOCATION_EDIT_TYPE);
            textView.setSelectAllOnFocus(true);
            textView.setFocusable(false);
            textView.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    textView.setFocusableInTouchMode(true);
                    return false;
                }
            });

            iconView = new ImageView(context);
            iconView.setImageResource(ws.extension.android.hackertouch.R.drawable.y);
            iconView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            setBackgroundColor(Color.rgb(255,101,0));
            setPadding(0,5,0,0);

            addView(iconView);
            addView(textView);
        }

        public EditText getTextView() {
            return textView;
        }

        @Override
        protected void onMeasure(int w, int h) {
            super.onMeasure(w, h);
            h = textView.getMeasuredHeight();
            setMeasuredDimension(getMeasuredWidth(),h+getPaddingTop());
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            int h = textView.getMeasuredHeight();
            t += getPaddingTop();
            iconView.layout(l,t,l+h,t+h);
            textView.layout(l+h,t,r,t+h);
            Lg.d("onLayout h="+h+" l="+l+" t="+t+" r="+r+" b="+b);
        }

        @Override
        public void clearFocus() {
            super.clearFocus();
            textView.clearFocus();
            textView.setFocusable(false);
        }
    }

    private WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            Lg.w("Failed to load "+failingUrl+" ("+description+')');
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            //Lg.d("onPageStarted url="+url+" cookie="+CookieManager.getInstance().getCookie(url));
            locationView.getTextView().setText(url);
        }

        @Override
        public void onReceivedHttpAuthRequest(WebView view, final HttpAuthHandler handler, String host, String realm) {
            //Lg.d("onReceivedHttpAuthRequest host="+host+" realm="+realm+" handler="+handler);
            handler.proceed("jedediah","apophis");
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            //Lg.d("onPageFinished url="+url+" cookie="+CookieManager.getInstance().getCookie(url));
            String cookie = CookieManager.getInstance().getCookie("http://news.ycombinator.com/");
            Lg.d("yc cookie="+cookie);
            if (cookie != null && cookie.contains("user=")) {
                Lg.i("YC cookie detected");
            }
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
            //Lg.d("onLoadResource url="+url+" cookie="+CookieManager.getInstance().getCookie(url));
        }
    };

    private WebView webView;
    private LocationView locationView;

    public BrowserView(Context context) {
        super(context);
        setOrientation(VERTICAL);

        locationView = new LocationView(context);
        locationView.getTextView().setOnEditorActionListener(this);

        webView = new WebView(context);
        webView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
        webView.setWebViewClient(webViewClient);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.clearCache(true);
        webView.clearFormData();
        webView.clearHistory();
        CookieSyncManager.createInstance(context);
        CookieManager.getInstance().removeAllCookie();
        WebViewDatabase db = WebViewDatabase.getInstance(context);
        db.clearFormData();
        db.clearHttpAuthUsernamePassword();
        db.clearUsernamePassword();

        webView.setOnTouchListener(this);

        webView.loadUrl("http://news.ycombinator.com/");
        //webView.loadUrl("http://192.168.0.13:9999");

        addView(locationView);
        addView(webView);
    }
    //
    //@Override
    //public boolean onTouchEvent(MotionEvent event) {
    //    locationView.clearFocus();
    //    return super.onTouchEvent(event);
    //}
    //
    //@Override
    //public boolean onKeyDown(int keyCode, KeyEvent event) {
    //    locationView.clearFocus();
    //    return super.onKeyDown(keyCode, event);
    //}

    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        String url = URLUtil.guessUrl(textView.getText().toString());
        Lg.d("Loading URL: "+url);
        textView.setText(url);
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(textView.getWindowToken(),0);
        locationView.clearFocus();
        webView.loadUrl(url);
        return true;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Lg.d("BAD_TOUCH");
        locationView.clearFocus();
        return false;
    }

    public boolean onBackKey() {
        if (webView.canGoBack()) {
            webView.goBack();
            return true;
        } else {
            return false;
        }
    }
}
