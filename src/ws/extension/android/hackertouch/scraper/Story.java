package ws.extension.android.hackertouch.scraper;

import android.net.Uri;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import ws.extension.android.log.Lg;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Story extends Fragment {

    private int id,
                rank,
                score,
                comments,
                age;

    private String auth,
                   headline,
                   link,
                   source,
                   poster,
                   time;

    private boolean internal; // a story without an external link, such as "Ask HN"

    private static final Pattern
        RANK_PATTERN = Pattern.compile("([0-9]+)\\."),
        SCORE_PATTERN = Pattern.compile("([0-9]+)\\s+points"),
        COMMENTS_PATTERN = Pattern.compile("(?:([0-9]+)\\s+comments?|discuss)"),
        TIME_PATTERN = Pattern.compile("(([0-9]+)\\s+(seconds?|minutes?|hours?|days?)\\s+ago).*");

    String lastContent, lastLink;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri,localName,qName,attributes);

        if ("span".equals(localName)) {
            if ("comhead".equals(attributes.getValue("class"))) {
                this.headline = lastContent;
                this.link = lastLink;
            }

        } else if ("a".equals(localName)) {
            String id = attributes.getValue("id");
            String href = attributes.getValue("href");

            if (href != null) {
                Uri hrefUri = Uri.parse(href);
                if (hrefUri != null) {

                    lastLink = href;

                    String auth = hrefUri.getQueryParameter("auth");
                    if (auth != null) {
                        this.auth = auth;

                    } else {
                        String action = hrefUri.getLastPathSegment();

                        if ("user".equals(action)) {
                            this.poster = hrefUri.getQueryParameter("id");

                        } else if ("item".equals(action)) {
                            try {
                                this.id = Integer.parseInt(hrefUri.getQueryParameter("id"));
                            } catch (NumberFormatException e) {
                                Lg.e("failed to parse story id from href '"+href+'\'',e);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void characters(String text) throws SAXException {
        text = text.trim();
        if (text.length() != 0) {
            lastContent = text;
        
            Matcher m;
            if (withinClass("subtext")) {

                if ((m = SCORE_PATTERN.matcher(text)).matches()) {
                    this.score = Integer.parseInt(m.group(1));

                } else if ((m = COMMENTS_PATTERN.matcher(text)).matches()) {
                    String g1 = m.group(1);
                    this.comments = (g1 == null ? 0 : Integer.parseInt(g1));

                } else if ((m = TIME_PATTERN.matcher(text)).matches()) {
                    this.time = m.group(1);
                    this.age = Integer.parseInt(m.group(2));
                    String units = m.group(3);
                    if (units.startsWith("minute")) {
                        this.age *= 60;
                    } else if (units.startsWith("hour")) {
                        this.age *= 60*60;
                    } else if (units.startsWith("day")) {
                        this.age *= 60*60*24;
                    }
                }

            } else if ((m = RANK_PATTERN.matcher(text)).matches()) {
                this.rank = Integer.parseInt(m.group(1));

            } else if (directlyWithinClass("comhead")) {
                source = text;
            }
        }
    }

    @Override
    public String toString() {
        return getClass().getName()+
               " id="+id+
               " rank="+rank+
               " score="+score+
               " age="+age+
               " comments="+comments+
               " auth="+auth+
               " poster="+poster+
               " time="+time+
               " source="+source+
               " headline="+headline+
               " link="+link;
    }

    public int getId() {
        return id;
    }

    public int getRank() {
        return rank;
    }

    public int getScore() {
        return score;
    }

    public int getCommentCount() {
        return comments;
    }

    public int getAgeInSeconds() {
        return age;
    }

    public String getVoteAuthToken() {
        return auth;
    }

    public String getHeadline() {
        return headline;
    }

    public String getExternalLink() {
        return link;
    }

    public String getExternalOrigin() {
        return source;
    }

    public String getUser() {
        return poster;
    }

    public String getTimeDescription() {
        return time;
    }

    public boolean isInternal() {
        return internal;
    }
}
