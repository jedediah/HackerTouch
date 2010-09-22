package ws.extension.android.hackertouch.scraper;

import android.content.Context;
import android.widget.TextView;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.ccil.cowan.tagsoup.Parser;
import org.xml.sax.*;
import ws.extension.android.log.Lg;

import java.io.IOException;

public class HttpScraper extends TextView implements ContentHandler {

    public HttpScraper(Context context) {
        super(context);
    }

    public void scrape(String url) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(url);
            HttpResponse res = client.execute(get);
            HttpEntity entity = res.getEntity();
            if (entity != null) {
                Parser parser = new Parser();
                parser.setContentHandler(this);
                parser.parse(new InputSource(entity.getContent()));
                setText(dump.toString());
            }
        }
        catch (IOException e) {
            Lg.e(e);
        }
        catch (SAXException e) {
            Lg.e(e);
        }
    }

    private StringBuilder dump = new StringBuilder(),
                          line = new StringBuilder();
    private int level = 0;
    private boolean charsFlag = false;

    public void print(String text) { line.append(text); dump.append(text); }
    public void print(char c) { line.append(c); dump.append(c); }

    public void startLine() {
        if (charsFlag) {
            endLine();
            charsFlag = false;
        }
        for (int i = 0; i < level; i++) print("  ");
    }

    public void endLine() {
        Lg.i(line.toString());
        line.setLength(0);
        dump.append('\n');
    }

    public void println(String text) {
        startLine();
        print(text);
        endLine();
    }

    public void indent() { level++; }
    public void dedent() { level--; }

    @Override
    public void setDocumentLocator(Locator locator) {
    }

    @Override
    public void startDocument() throws SAXException {
        println("startDocument");
        indent();
    }

    @Override
    public void endDocument() throws SAXException {
        dedent();
        println("endDocument");
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
        startLine();
        print('<');
        print(localName);
        for (int i = 0; i < attrs.getLength(); i++) {
            print(' ');
            print(attrs.getLocalName(i));
            print('=');
            print(attrs.getValue(i));
        }
        print('>');
        endLine();
        indent();
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        dedent();
        startLine();
        print("</");
        print(localName);
        print('>');
        endLine();
    }

    @Override
    public void characters(char[] chars, int off, int len) throws SAXException {
        if (!charsFlag) {
            startLine();
            charsFlag = true;
        }
        print(String.valueOf(chars,off,len));
    }

    @Override
    public void ignorableWhitespace(char[] chars, int off, int len) throws SAXException {
        print(String.valueOf(chars,off,len));
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
    }
}
