package ws.extension.android.hackertouch.scraper;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import ws.extension.android.log.Lg;

import java.util.Stack;

public class Fragment extends DefaultHandler {

    protected static class TagContext {

        public TagContext(String tagName, String tagClass, String tagId) {
            this.tagName = tagName;
            this.tagClass = tagClass;
            this.tagId = tagId;
        }

        public String tagName, tagClass, tagId;
    }

    protected Stack<TagContext> tagStack = new Stack<TagContext>();

    protected String enclosingTagName() {
        if (tagStack.isEmpty()) {
            return null;
        } else {
            return tagStack.peek().tagName;
        }
    }

    protected boolean directlyWithinTag(String tagName) {
        return !tagStack.isEmpty() && tagName.equals(tagStack.peek().tagName);
    }

    protected boolean directlyWithinClass(String className) {
        return !tagStack.isEmpty() && className.equals(tagStack.peek().tagClass);
    }

    protected boolean directlyWithinId(String id) {
        return !tagStack.isEmpty() && id.equals(tagStack.peek().tagId);
    }

    protected boolean withinClass(String className) {
        for (int i = tagStack.size()-1; i >= 0; i--) {
            if (className.equals(tagStack.get(i).tagClass))
                return true;
        }
        return false;
    }

    protected boolean withinId(String id) {
        for (int i = tagStack.size()-1; i >= 0; i--) {
            if (id.equals(tagStack.get(i).tagId))
                return true;
        }
        return false;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        tagStack.push(new TagContext(localName,
                                     attributes.getValue("class"),
                                     attributes.getValue("id")));
        resetCharacters();
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (tagStack.empty()) {
            Lg.w("superfluous endElement call: localName="+localName);
        } else {
            tagStack.pop();
        }
        resetCharacters();
    }

    private StringBuilder sb = new StringBuilder();

    @Override
    public final void characters(char[] ch, int start, int length) throws SAXException {
        if (sb == null) sb = new StringBuilder();
        sb.append(ch,start,length);
    }

    private void resetCharacters() throws SAXException {
        String text = sb.toString();
        sb.setLength(0);
        characters(text);
    }

    public void characters(String text) throws SAXException {

    }
}
