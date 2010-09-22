package ws.extension.android.hackertouch.scraper;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import ws.extension.android.log.Lg;

import java.util.Vector;

public class IndexPage extends Page {

    public static final String URI = "http://news.ycombinator.com/news";

    private Vector<Story> stories = new Vector<Story>();
    private int tableCount;
    private boolean inStories, firstRow;
    private Story story;

    public IndexPage() {
        super(URI);
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        stories.clear();
        tableCount = 0;
        inStories = false;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);

        if ("table".equals(localName) && ++tableCount == 3) {
            inStories = true;
            firstRow = false;
            Lg.i("Start story table");

        } else if (inStories) {
            if ("tr".equals(localName) && attributes.getValue("style") == null) {
                firstRow = !firstRow;
                if (firstRow) story = new Story();
            }

            if (story != null) {
                story.startElement(uri,localName,qName,attributes);
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        if (inStories && story != null) {
            story.characters(ch,start,length);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);

        if (inStories) {
            if ("table".equals(localName)) {
                inStories = false;
                Lg.i("End story table");

            } else if (story != null) {
                story.endElement(uri,localName,qName);

                if (!firstRow && "tr".equals(localName)) {
                    //Lg.i("Adding story: "+story);
                    stories.add(story);
                    story = null;
                }
            }
        }
    }

    @Override
    public String toString() {
        return getClass().getName()+": "+stories.size()+" stories";
    }

    public int getStoryCount() {
        return isLoaded() ? stories.size() : 0;
    }

    public Story getStory(int i) {
        return isLoaded() ? stories.get(i) : null;
    }
}
