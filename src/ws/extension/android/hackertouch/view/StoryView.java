package ws.extension.android.hackertouch.view;

import android.content.Context;
import android.graphics.Color;
import android.widget.LinearLayout;
import android.widget.TextView;
import ws.extension.android.hackertouch.scraper.Story;

public class StoryView extends LinearLayout {

    private Story story;
    private TextView topText;
    private TextView subText;

    public StoryView(Context context, Story s) {
        super(context);
        setOrientation(VERTICAL);

        topText = new TextView(context);
        topText.setTextColor(Color.WHITE);

        subText = new TextView(context);
        subText.setTextSize(12);
        
        addView(topText);
        addView(subText);

        setStory(s);
    }

    public void update() {
        topText.setText(story.getHeadline());
        subText.setText(story.getScore()+" points | "+
                        story.getTimeDescription()+
                        " by "+story.getUser()+
                        " | "+story.getCommentCount()+" comments");
    }

    public void setStory(Story s) {
        this.story = s;
        update();
    }

    public Story getStory() {
        return story;
    }
}
