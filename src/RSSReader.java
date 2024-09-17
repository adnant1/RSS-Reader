import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;
import components.xmltree.XMLTree;
import components.xmltree.XMLTree1;

/**
 * Program to convert an XML RSS (version 2.0) feed from a given URL into the
 * corresponding HTML output file.
 *
 * @author Adnan T
 *
 */
public final class RSSReader {

    /**
     * Private constructor so this utility class cannot be instantiated.
     */
    private RSSReader() {
    }

    /**
     * Outputs the "opening" tags in the generated HTML file. These are the
     * expected elements generated by this method:
     *
     * <html> <head> <title>the channel tag title as the page title</title>
     * </head> <body>
     * <h1>the page title inside a link to the <channel> link</h1>
     * <p>
     * the channel description
     * </p>
     * <table border="1">
     * <tr>
     * <th>Date</th>
     * <th>Source</th>
     * <th>News</th>
     * </tr>
     *
     * @param channel
     *            the channel element XMLTree
     * @param out
     *            the output stream
     * @updates out.content
     * @requires [the root of channel is a <channel> tag] and out.is_open
     * @ensures out.content = #out.content * [the HTML "opening" tags]
     */
    private static void outputHeader(XMLTree channel, SimpleWriter out) {
        assert channel != null : "Violation of: channel is not null";
        assert out != null : "Violation of: out is not null";
        assert channel.isTag() && channel.label().equals("channel") : ""
                + "Violation of: the label root of channel is a <channel> tag";
        assert out.isOpen() : "Violation of: out.is_open";

        out.println("<html>");
        out.println("<head>");

        String title = channel.child(getChildElement(channel, "title")).child(0)
                .label();

        if (title.equals("")) {
            out.println("<title>Empty Title</title>");
        } else {
            out.println("<title>" + title + "</title>");
        }

        out.println("<head>");
        out.println("<body>");
        out.print("<h1>");

        String link = channel.child(getChildElement(channel, "link")).child(0)
                .label();
        out.println("<a href=\"" + link + "\">" + title + "</a></h1>");

        String description = channel
                .child(getChildElement(channel, "description")).child(0)
                .label();
        out.println("<p>" + description + "</p>");
        out.println("<table border=\"1\">");
        out.println("<tr>");
        out.println("<th>Date</th>");
        out.println("<th>Source</th>");
        out.println("<th>News</th>");
        out.println("</tr>");

    }

    /**
     * Outputs the "closing" tags in the generated HTML file. These are the
     * expected elements generated by this method:
     *
     * </table>
     * </body> </html>
     *
     * @param out
     *            the output stream
     * @updates out.contents
     * @requires out.is_open
     * @ensures out.content = #out.content * [the HTML "closing" tags]
     */
    private static void outputFooter(SimpleWriter out) {
        assert out != null : "Violation of: out is not null";
        assert out.isOpen() : "Violation of: out.is_open";

        out.println("</table>");
        out.println("</body>");
        out.println("</html>");
    }

    /**
     * Finds the first occurrence of the given tag among the children of the
     * given {@code XMLTree} and return its index; returns -1 if not found.
     *
     * @param xml
     *            the {@code XMLTree} to search
     * @param tag
     *            the tag to look for
     * @return the index of the first child of type tag of the {@code XMLTree}
     *         or -1 if not found
     * @requires [the label of the root of xml is a tag]
     * @ensures <pre>
     * getChildElement =
     *  [the index of the first child of type tag of the {@code XMLTree} or
     *   -1 if not found]
     * </pre>
     */
    private static int getChildElement(XMLTree xml, String tag) {
        assert xml != null : "Violation of: xml is not null";
        assert tag != null : "Violation of: tag is not null";
        assert xml.isTag() : "Violation of: the label root of xml is a tag";

        int child = -1;

        int i = 0;
        while (i < xml.numberOfChildren()
                && !xml.child(i).label().equals(tag)) {
            i++;
        }
        if (i < xml.numberOfChildren()) {
            child = i;
        }

        return child;
    }

    /**
     * Processes one news item and outputs one table row. The row contains three
     * elements: the publication date, the source, and the title (or
     * description) of the item.
     *
     * @param item
     *            the news item
     * @param out
     *            the output stream
     * @updates out.content
     * @requires [the label of the root of item is an <item> tag] and
     *           out.is_open
     * @ensures <pre>
     * out.content = #out.content *
     *   [an HTML table row with publication date, source, and title of news item]
     * </pre>
     */
    private static void processItem(XMLTree item, SimpleWriter out) {
        assert item != null : "Violation of: item is not null";
        assert out != null : "Violation of: out is not null";
        assert item.isTag() && item.label().equals("item") : ""
                + "Violation of: the label root of item is an <item> tag";
        assert out.isOpen() : "Violation of: out.is_open";

        out.println("<tr>");
        String pubDate = item.child(getChildElement(item, "pubDate")).child(0)
                .label();
        if (pubDate.equals("")) {
            out.println("<td>No Date Available</td>");
        } else {
            out.println("<td>" + pubDate + "</td>");
        }

        int sourceIndex = getChildElement(item, "source");
        String source = "";

        if (sourceIndex >= 0) {
            source = item.child(sourceIndex).child(0).label();
            if (source.equals("")) {
                out.println("<td>No Source Available</td>");
            } else {
                String sourceUrl = item.child(sourceIndex)
                        .attributeValue("url");
                out.println("<td><a href=\"" + sourceUrl + "\">" + source
                        + "</a></td>");
            }
        } else {
            out.println("<td>No Source Available</td>");
        }

        String title = item.child(getChildElement(item, "title")).child(0)
                .label();

        String link = item.child(getChildElement(item, "link")).child(0)
                .label();

        if (title.equals("")) {
            String description = item
                    .child(getChildElement(item, "description")).child(0)
                    .label();
            if (description.equals("")) {
                out.println("<td><a href=\"" + link
                        + "\">No Title Available</a></td>");
            } else {
                out.println("<td><a href=\"" + link + "\">" + description
                        + "</a></td>");
            }
        } else {
            out.println("<td><a href=\"" + link + "\">" + title + "</a></td>");
        }

        out.println("</tr>");
    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments; unused here
     */
    public static void main(String[] args) {
        SimpleReader in = new SimpleReader1L();
        SimpleWriter out = new SimpleWriter1L();

        out.print("Enter the URL of an RSS 2.0 news feed: ");
        String url = in.nextLine();
        XMLTree xml = new XMLTree1(url);

        while (!(xml.label().equals("rss"))
                || !(xml.attributeValue("version").equals("2.0"))) {
            out.print("Enter the URL of an RSS 2.0 news feed: ");
            url = in.nextLine();
            xml = new XMLTree1(url);
        }

        XMLTree channel = xml.child(0);

        out.print(
                "Enter the name of the output file including the .html extension: ");
        String outFile = in.nextLine();

        SimpleWriter outHtml = new SimpleWriter1L(outFile);

        outputHeader(channel, outHtml);

        for (int i = 0; i < channel.numberOfChildren(); i++) {

            if (channel.child(i).label().equals("item")) {

                XMLTree item = channel.child(i);
                processItem(item, outHtml);
            }

        }

        outputFooter(outHtml);

        in.close();
        out.close();
        outHtml.close();
    }

}