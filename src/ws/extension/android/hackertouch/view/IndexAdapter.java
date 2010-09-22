package ws.extension.android.hackertouch.view;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import ws.extension.android.hackertouch.scraper.*;

public class IndexAdapter extends BaseAdapter implements PageListener {

    private IndexPage scraper;

    public IndexAdapter(IndexPage scraper) {
        this.scraper = scraper;
        this.scraper.setListener(this);
    }

    @Override
    public int getCount() {
        return scraper.getStoryCount();
    }

    @Override
    public Object getItem(int i) {
        return scraper.getStory(i);
    }

    @Override
    public long getItemId(int i) {
        return scraper.getStory(i).getId();
    }

    @Override
    public View getView(int i, View oldView, ViewGroup viewGroup) {
        Story story = scraper.getStory(i);
        if (oldView instanceof StoryView) {
            StoryView storyView = (StoryView) oldView;
            storyView.setStory(story);
            return storyView;
        } else {
            return new StoryView(viewGroup.getContext(),story);
        }
    }

    @Override
    public void stateChanged(int state) {
        notifyDataSetChanged();
    }
}
