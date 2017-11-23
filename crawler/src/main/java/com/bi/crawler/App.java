package com.bi.crawler;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Crawler
 */
public class App{

    private static final String TABS = "\t";
    private static RandomAccessFile raf;
    private static RandomAccessFile logger;

    public static void main(String[] args) throws IOException {
        String host = "http://staysomewherenice.com/";
        URL url = new URL(host);
        URLConnection urlConnection = url.openConnection();
        logger = new RandomAccessFile("bi-crawler.log", "rwd");
        raf = new RandomAccessFile("bi-crawler.output", "rw");
        logger.setLength(0);
        raf.setLength(0);

        HashSet<String> indexOfUrls = new HashSet<>();
        HashMap pageIndex = new HashMap();
        String indents = TABS;
        pageIndex.put(host, index(host, urlConnection, indexOfUrls, indents));

        prettyPrint(pageIndex, raf, TABS);
        raf.close();
        logger.close();
        //print page Index
    }

    private static void prettyPrint(HashMap<String, HashMap> pageIndex, RandomAccessFile raf, String indents) throws IOException {
        String tabs = indents + TABS;

        for (Entry entry : pageIndex.entrySet()) {
            raf.writeBytes(tabs+entry.getKey()+"\n");
            if(entry.getValue() != null){
                prettyPrint((HashMap<String, HashMap>) entry.getValue(), raf, tabs);
            }
        }
    }

    private static HashMap index(String host, URLConnection urlConnection, HashSet<String> indexOfUrls, String indents) throws IOException {
            URL url = urlConnection.getURL();
            System.out.println("INDEXING... "+url.getHost()+url.getPath());
            InputStream inputStream = null;
            Collection<String> sublinks = null;

            try {
                inputStream = urlConnection.getInputStream();
                sublinks = extractLinks(inputStream, url.getHost());
            } catch (IOException e) {
                System.out.println("Could not connect to "+ url + " mesg " + e.getMessage());
            }

            if(sublinks == null || sublinks.isEmpty())return null;
            String tabs = indents + TABS;


            System.out.println(" FOUND sublinks for " + host + " = " + sublinks.toString());
            HashMap pageIndex = new HashMap();
            for (String sublink : sublinks) {
                pageIndex.put(sublink, null);
                if(!indexOfUrls.contains(sublink) && !sublink.startsWith("#")){
                    indexOfUrls.add(sublink);
                    URL subURL = new URL(url, sublink);
                    URLConnection urlConnectionSub = null;
                    try {
                        urlConnectionSub = subURL.openConnection();
                        pageIndex.put(sublink, index(host, urlConnectionSub, indexOfUrls, tabs));
                    } catch (IOException e) {
                        System.out.println("Could not connect to "+ subURL + e.getMessage());
                    }
                }else{
                    System.out.print("... INDEXED");
                }
//                logger.writeBytes(tabs+sublink+"\n");
            }
            indexOfUrls.addAll(sublinks);
            return pageIndex;
    }

    static Collection<String> extractLinks(InputStream inputStream, String host) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String s = reader.readLine();
        Pattern linkPattern = getDomainURLPattern(host);
        Matcher matcher;
        Collection<String> linkList = new HashSet<>();
        String link;
        while(s != null){
            matcher = linkPattern.matcher(s);
            while(matcher.find()){
                link = matcher.group(2);
                linkList.add(link);
            }
            s = reader.readLine();
        }
        return linkList;
    }

    private static Pattern getDomainURLPattern(String host) {
        String domainType = null;
        if(host.indexOf(".") > -1){
            domainType = host.substring(host.lastIndexOf(".")+1);
        }


        String scheme = "(\\s*(http|https):\\/\\/)";
        String port = "(:\\d{2,5})?";
        //TODO handle IP address
        String authority = "(www\\.)?"+ host.replace(".", "\\.").toLowerCase().replace("www\\.", "") + port + "(\\/)?";
        String pathLiteral = "[\\w\\d\\-\\_\\/\\+~\\&%$@!^=~`]+";
        String ignoreFileTypes = "gif|jpg|ico|png" + ((domainType != null)? "|"+domainType:"");
        String path =  pathLiteral + "(\\.(?!(" + ignoreFileTypes + "))" + pathLiteral + ")?";
        String fragment = "\\s*(#" + pathLiteral + ")?";
        String query = "(\\?[^'\"]*)?";
        String quotes = "(\"|')";
        String htmlHref = "href\\s*=\\s*";
        String url = "(((" + scheme + ")?" + authority + ")?" + path + "(" /*+ fragment + "|" */+ query + ")?)";
        String ignoreCase = "(?i)";
        return Pattern.compile(ignoreCase +htmlHref + quotes + url + quotes);
    }
}
