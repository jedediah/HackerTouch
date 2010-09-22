package ws.extension.android.hackertouch.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.*;
import ws.extension.android.hackertouch.scraper.IndexPage;

public class IndexView extends ListView implements AdapterView.OnItemClickListener {

    private IndexPage scraper;
    private TextView emptyView;

    public IndexView(Context context) {
        super(context);
        scraper = new IndexPage();
        setAdapter(new IndexAdapter(scraper));

        setOnItemClickListener(this);

        emptyView = new TextView(context);
        emptyView.setText("Loading...");
        setEmptyView(emptyView);

        scraper.get();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if (view instanceof StoryView) {
            StoryView sv = (StoryView) view;
            getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(sv.getStory().getExternalLink())));
        }
    }
}
