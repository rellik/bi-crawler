package com.bi.crawler;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
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

    public static void main(String[] args) throws IOException {
        if(args.length == 0) throw new IllegalArgumentException("Please pass the domain URL as the argument ex: \"http://wiprodigital.com/\"");
        String domain = args[0];
        URL url = new URL(domain);
        raf = new RandomAccessFile("bi-crawler.output", "rw");
        raf.setLength(0);

        HashSet<String> indexOfUrls = new HashSet<>();
        HashMap pageIndex = new HashMap();
        pageIndex.put(domain, index(domain, url, indexOfUrls, getDomainURLPattern(url.getHost())));

        prettyPrint(pageIndex, raf, TABS);
        raf.close();
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

    private static HashMap index(String host, URL url, HashSet<String> indexOfUrls, Pattern domainURLPattern) throws IOException {
        System.out.println("INDEXING... "+url.getHost()+url.getPath());
        return indexSubLinks(host, url, indexOfUrls, domainURLPattern, extractSublinks(url, domainURLPattern));
    }

    private static Collection<String> extractSublinks(URL url, Pattern domainURLPattern) {
        Collection<String> sublinks = null;

        try {
            InputStream inputStream = url.openConnection().getInputStream();
            sublinks = extractLinks(inputStream, domainURLPattern);
        } catch (IOException e) {
            System.err.println("Could not connect to "+ url + " mesg " + e.getMessage());
        }
        return sublinks;
    }

    private static HashMap indexSubLinks(String host, URL url, HashSet<String> indexOfUrls, Pattern domainURLPattern, Collection<String> sublinks) throws MalformedURLException {
        if(sublinks == null || sublinks.isEmpty())return null;
        System.out.println(" FOUND sublinks for " + host + " = " + sublinks.toString());
        HashMap pageIndex = new HashMap();

        for (String sublink : sublinks) {
            pageIndex.put(sublink, null);// else we dont get all the sub links
            if(!indexOfUrls.contains(sublink) && !sublink.startsWith("#")){
                indexOfUrls.add(sublink);
                URL subURL = new URL(url, sublink);
                try {
                    pageIndex.put(sublink, index(host, subURL, indexOfUrls, domainURLPattern));
                } catch (IOException e) {
                    System.err.println("Could not connect to "+ subURL + e.getMessage());
                }
            }
        }

        return pageIndex;
    }

    static Collection<String> extractLinks(InputStream inputStream, Pattern linkPattern) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String s = reader.readLine();
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

    static Pattern getDomainURLPattern(String host) {
        String scheme = "(\\s*(http|https):\\/\\/)";
        String port = "(:\\d{2,5})?";
        //TODO handle IP address
        String authority = "(www\\.)?"+ host.replace(".", "\\.").toLowerCase().replace("www\\.", "") + port + "(\\/)?";
        String pathLiteral = "[\\w\\d\\-\\_\\/\\+~\\&%$@!^=~`]+";
        String ignoreFileTypes = "gif|jpg|ico|png" ;
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
