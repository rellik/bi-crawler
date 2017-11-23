package com.bi.crawler;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.*;

/**
 * Crawler test.
 */
public class AppTest{


    @Test
    public void shouldExtractLinksInDomain() throws Exception {
        String host = "i.com";
        String[] validLinks = {"internal.html"
                ,"internal.pdf"
                ,"/internal1.html"
//                ,"/internal1.html#frag1"
//                ,"#frag2"
                ,"/internal2"
                ,"internal3"
                ,"/internal4?p=v&p1=v1"
                ,"/internal5.html?p=v&p1=v1"
                ,"/internal6.jsp?p=v&p1=v1"
                ,"www.i.com/y"
                ,"www.i.com:8080/y"
                ,"htTp://www.i.com:8080/y"
                ,"i.com/y"
                ,"http://i.com/y"
                ,"/internal9.html"
                ,"https://i.com/en/download/current/"
                ,"http://i.com/who-we-are"
        };

        String[] invalidLinks = {
                " www.x.com/html"
                ,"/internal7.jpg?p=v&p1=v1"
                ,"/internal8.gif"
                ,"https://wwx.i.com/y"
                ,"https://x.com/y"
                ,"www.x.com/y"
                ,"www.x.co.in/y"
                ,"https://twitter.com/pallabdeb?lang=ca"
                ,"https://github.com/i/help/issues"
        };

        StringBuilder validStrings = new StringBuilder();
        for (String validLink : validLinks) {
            validStrings.append("Link valid <a href=\""+validLink+"\">");
            validStrings.append("Link valid <a href='"+validLink+"'>");
        }
        StringBuilder inValidStrings = new StringBuilder();
        for (String inValidLink : invalidLinks) {
            inValidStrings.append("Link invalid <a href='"+inValidLink+"'>");
            inValidStrings.append("Link invalid <img src='"+inValidLink+"'>");
        }

        String html = "<html>"
                + "   <body>"
                + "   <p>"
                + "       This is a dummy html test page. with lot of links."
                + validStrings.toString() + inValidStrings.toString()
                + "   </p>"
                + "</body>"
                + "</html>";

        Collection<String> linkList  = App.extractLinks(new ByteArrayInputStream(html.getBytes()), App.getDomainURLPattern(host));
        ArrayList extraMatch = new ArrayList(linkList);
        extraMatch.removeAll(Arrays.asList(validLinks));
        System.out.println("Found extra :"+ extraMatch.toString());

        List<String> missed = new ArrayList(Arrays.asList(validLinks));
        missed.removeAll(linkList);
        System.out.println("Missed :"+ missed.toString());

        Assert.assertEquals(validLinks.length, linkList.size());
        Assert.assertTrue(linkList.containsAll(Arrays.asList(validLinks)));

    }

    @Test
    public void shouldExtractLinksInSamplePage() throws Exception {

        String html = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "  <meta charset=\"utf-8\">\n" +
                "  <title>Node.js</title>\n" +
                "\n" +
                "  <link rel=\"dns-prefetch\" href=\"http://fonts.googleapis.com\">\n" +
                "  <link rel=\"dns-prefetch\" href=\"http://fonts.gstatic.com\">\n" +
                "  <link rel=\"dns-prefetch\" href=\"http://www.google-analytics.com\">\n" +
                "\n" +
                "  <meta name=\"author\" content=\"Node.js Foundation\">\n" +
                "  <meta name=\"robots\" content=\"index, follow\">\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "\n" +
                "  <link rel=\"apple-touch-icon\" href=\"/static/apple-touch-icon.png\">\n" +
                "  <link rel=\"icon\" sizes=\"32x32\" type=\"image/png\" href=\"/static/favicon.png\">\n" +
                "\n" +
                "  <meta property=\"og:site_name\" content=\"Node.js\">\n" +
                "  <meta property=\"og:title\" content=\"Node.js\">\n" +
                "  <meta name=\"og:image\" content=\"/static/images/logo-hexagon.png\">\n" +
                "  <meta name=\"og:image:type\" content=\"image/png\">\n" +
                "  <meta name=\"og:image:width\" content=\"224\">\n" +
                "  <meta name=\"og:image:height\" content=\"256\">\n" +
                "\n" +
                "  <meta name=\"twitter:card\" content=\"summary\" />\n" +
                "  <meta name=\"twitter:site\" content=\"@nodejs\">\n" +
                "  <meta name=\"twitter:title\" content=\"Node.js\">\n" +
                "  <meta name=\"twitter:image\" content=\"/static/images/logo-hexagon.png\">\n" +
                "  <meta name=\"twitter:image:alt\" content=\"The Node.js Hexagon Logo\">\n" +
                "\n" +
                "  <link rel=\"canonical\" href=\"https://nodejs.org/en/\">\n" +
                "  <link rel=\"alternate\" href=\"/en/feed/blog.xml\" title=\"Node.js Blog\" type=\"application/rss+xml\">\n" +
                "  <link rel=\"alternate\" href=\"/en/feed/releases.xml\" title=\"Node.js Blog: Releases\" type=\"application/rss+xml\">\n" +
                "  <link rel=\"alternate\" href=\"/en/feed/vulnerability.xml\" title=\"Node.js Blog: Vulnerability Reports\" type=\"application/rss+xml\">\n" +
                "  <link rel=\"stylesheet\" href=\"/layouts/css/styles.css\" media=\"all\">\n" +
                "  <link rel=\"stylesheet\" href=\"https://fonts.googleapis.com/css?family=Source+Sans+Pro:400,600\">\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "  <header>\n" +
                "    <div class=\"container\">\n" +
                "  \n" +
                "      <a href=\"/en\" id=\"logo\">\n" +
                "        <img src=\"/static/images/logo.svg\" alt=\"node.js\">\n" +
                "      </a>\n" +
                "  \n" +
                "      <nav>\n" +
                "        <ul class=\"list-divider-pipe\">\n" +
                "          <li class=\"active\">\n" +
                "            <a href=\"/en/\">Home</a>\n" +
                "          </li>\n" +
                "  \n" +
                "            <li>\n" +
                "              <a href=\"/en/about/\">About</a>\n" +
                "            </li>\n" +
                "            <li>\n" +
                "              <a href=\"/en/download/\">Downloads</a>\n" +
                "            </li>\n" +
                "            <li>\n" +
                "              <a href=\"/en/docs/\">Docs</a>\n" +
                "            </li>\n" +
                "            <li>\n" +
                "              <a href=\"/en/foundation/\">Foundation</a>\n" +
                "            </li>\n" +
                "            <li>\n" +
                "              <a href=\"/en/get-involved/\">Get Involved</a>\n" +
                "            </li>\n" +
                "            <li>\n" +
                "              <a href=\"/en/security/\">Security</a>\n" +
                "            </li>\n" +
                "            <li>\n" +
                "              <a href=\"/en/blog/\">News</a>\n" +
                "            </li>\n" +
                "        </ul>\n" +
                "      </nav>\n" +
                "  \n" +
                "    </div>\n" +
                "  </header>\n" +
                "\n" +
                "  <div id=\"main\">\n" +
                "    <div class=\"container\">\n" +
                "\n" +
                "      <div id=\"home-intro\">\n" +
                "\n" +
                "        <p>Node.js® is a JavaScript runtime built on <a href=\"https://developers.google.com/v8/\">Chrome&#39;s V8 JavaScript engine</a>.\n" +
                "Node.js uses an event-driven, non-blocking I/O model that makes it\n" +
                "lightweight and efficient. Node.js&#39; package ecosystem, <a href=\"https://www.npmjs.com/\">npm</a>, is the largest ecosystem of open\n" +
                "source libraries in the world.</p>\n" +
                "\n" +
                "\n" +
                "          <p class=\"home-version home-version-banner\">\n" +
                "            Important <a href=\"https://nodejs.org/en/blog/vulnerability/oct-2017-dos/\">security releases</a>, please update now!\n" +
                "          </p>\n" +
                "\n" +
                "        <h2 id=\"home-downloadhead\" data-dl-local=\"Download for\">Download</h2>\n" +
                "\n" +
                "        <div class=\"home-downloadblock\">\n" +
                "\n" +
                "          <a href=\"https://nodejs.org/dist/v8.9.1/\" class=\"home-downloadbutton\" title=\"Download 8.9.1 LTS\" data-version=\"v8.9.1\">\n" +
                "            8.9.1 LTS\n" +
                "            <small>Recommended For Most Users</small>\n" +
                "          </a>\n" +
                "\n" +
                "          <ul class=\"list-divider-pipe home-secondary-links\">\n" +
                "            <li>\n" +
                "              <a href=\"https://nodejs.org/en/download/\">Other Downloads</a>\n" +
                "            </li>\n" +
                "            <li>\n" +
                "              <a href=\"https://github.com/nodejs/node/blob/master/doc/changelogs/CHANGELOG_V8.md#8.9.1\">Changelog</a>\n" +
                "            </li>\n" +
                "            <li>\n" +
                "              <a href=\"https://nodejs.org/dist/latest-v8.x/docs/api/\">API Docs</a>\n" +
                "            </li>\n" +
                "          </ul>\n" +
                "\n" +
                "        </div>\n" +
                "\n" +
                "          <div class=\"home-downloadblock\">\n" +
                "\n" +
                "            <a href=\"https://nodejs.org/dist/v9.2.0/\" class=\"home-downloadbutton\"  title=\"Download 9.2.0 Current\"  data-version=\"v9.2.0\">\n" +
                "              9.2.0 Current\n" +
                "              <small>Latest Features</small>\n" +
                "            </a>\n" +
                "\n" +
                "            <ul class=\"list-divider-pipe home-secondary-links\">\n" +
                "              <li>\n" +
                "                <a href=\"https://nodejs.org/en/download/current/\">Other Downloads</a>\n" +
                "              </li>\n" +
                "              <li>\n" +
                "                <a href=\"https://github.com/nodejs/node/blob/master/doc/changelogs/CHANGELOG_V9.md#9.2.0\">Changelog</a>\n" +
                "              </li>\n" +
                "              <li>\n" +
                "                <a href=\"https://nodejs.org/dist/latest-v9.x/docs/api/\">API Docs</a>\n" +
                "              </li>\n" +
                "            </ul>\n" +
                "\n" +
                "          </div>\n" +
                "        <p>\n" +
                "          Or have a look at the <a href=\"https://github.com/nodejs/LTS#release-schedule\">LTS schedule.</a>\n" +
                "        </p>\n" +
                "        <p>\n" +
                "          Sign up for <a href=\"https://newsletter.nodejs.org/\">Node.js Everywhere</a>, the official Node.js Weekly Newsletter.\n" +
                "        </p>\n" +
                "      </div>\n" +
                "\n" +
                "    </div>\n" +
                "  </div>\n" +
                "\n" +
                "  <a href=\"#\" id=\"scroll-to-top\">&uarr; <span>Scroll to top</span></a>\n" +
                "  \n" +
                "  <footer class=\"no-margin-top\" role=\"contentinfo\">\n" +
                "  \n" +
                "    <div class=\"container\">\n" +
                "      <div class=\"linuxfoundation-footer\">\n" +
                "        <div class=\"issue-link-container\">\n" +
                "          <a class=\"linuxfoundation-logo\" href=\"http://collabprojects.linuxfoundation.org\">\n" +
                "            <img alt=\"Linux Foundation Collaborative Projects\" src=\"/static/images/lfcp.png\">\n" +
                "          </a>\n" +
                "          <ul class=\"list-divider-pipe issue-link\">\n" +
                "            <li><a href=\"https://github.com/nodejs/node/issues\">Report Node.js issue</a></li>\n" +
                "            <li><a href=\"https://github.com/nodejs/nodejs.org/issues\">Report website issue</a></li>\n" +
                "            <li><a href=\"https://github.com/nodejs/help/issues\">Get Help</a></li>\n" +
                "          </ul>\n" +
                "        </div>\n" +
                "  \n" +
                "        <p>© 2017 Node.js Foundation. All Rights Reserved. Portions of this site originally © 2017 Joyent. </p>\n" +
                "        <p>Node.js is a trademark of Joyent, Inc. and is used with its permission. Please review the <a href=\"/static/documents/trademark-policy.pdf\">Trademark Guidelines of the Node.js Foundation</a>.</p>\n" +
                "        <p>Linux Foundation is a registered trademark of The Linux Foundation.</p>\n" +
                "        <p>Linux is a registered <a href=\"http://www.linuxfoundation.org/programs/legal/trademark\" title=\"Linux Mark Institute\">trademark</a> of Linus Torvalds.</p>\n" +
                "        <p>\n" +
                "          <a href=\"https://raw.githubusercontent.com/nodejs/node/master/LICENSE\">Node.js Project Licensing Information</a>.\n" +
                "        </p>\n" +
                "      </div>\n" +
                "    </div>\n" +
                "  \n" +
                "  </footer>\n" +
                "  \n" +
                "  <link rel=\"stylesheet\" href=\"/static/css/prism-tomorrow.css\" media=\"all\">\n" +
                "  <script type=\"text/javascript\">\n" +
                "    var $scrollToTop = document.getElementById('scroll-to-top');\n" +
                "    (window.onscroll = function() {\n" +
                "      $scrollToTop.style.display = (window.pageYOffset > window.innerHeight) ? 'block' : 'none';\n" +
                "    })();\n" +
                "    $scrollToTop.onclick = function() {\n" +
                "      window.scrollTo(0, 0);\n" +
                "      return false;\n" +
                "    };\n" +
                "  </script>\n" +
                "  \n" +
                "  <script>\n" +
                "    (function(d,e,m,s){\n" +
                "      if (!/(MSIE|Trident)/.test(navigator.userAgent)){return;}\n" +
                "      m=d.createElement(e);\n" +
                "      s=d.getElementsByTagName(e)[0];m.async=1;m.src='/static/js/modernizr.custom.js';\n" +
                "      m.onload=function(){Modernizr.addTest('flexboxtweener', Modernizr.testAllProps('flexAlign'));};\n" +
                "      s.parentNode.insertBefore(m,s);\n" +
                "    })(document,'script');\n" +
                "  </script>\n" +
                "  \n" +
                "  <script src=\"/static/js/dnt_helper.js\"></script>\n" +
                "  <script>\n" +
                "    (function(){\n" +
                "      if (!_dntEnabled()) {\n" +
                "        !function(n,o,d,e,j,s){n.GoogleAnalyticsObject=d;n[d]||(n[d]=function(){\n" +
                "        (n[d].q=n[d].q||[]).push(arguments)});n[d].l=+new Date;j=o.createElement(e);\n" +
                "        s=o.getElementsByTagName(e)[0];j.async=1;j.src='//www.google-analytics.com/analytics.js';\n" +
                "        s.parentNode.insertBefore(j,s)}(window,document,'ga','script');\n" +
                "  \n" +
                "        if (!ga) return;\n" +
                "  \n" +
                "        ga('create', 'UA-67020396-1', 'auto');\n" +
                "        ga('send', 'pageview');\n" +
                "  \n" +
                "        document.documentElement.addEventListener('click', function(e) {\n" +
                "  \n" +
                "          // Track case studies\n" +
                "          if(!e.target || !e.target.dataset || !e.target.dataset.casestudy) return;\n" +
                "          ga('send', 'event', {\n" +
                "            eventCategory: 'casestudy',\n" +
                "            eventAction: 'click',\n" +
                "            eventLabel: e.target.dataset.casestudy,\n" +
                "            eventValue: 0\n" +
                "          });\n" +
                "        });\n" +
                "      }\n" +
                "    })();\n" +
                "  </script>\n" +
                "  <script src=\"/static/js/download.js\" async defer></script>\n" +
                "\n" +
                "</body>\n" +
                "</html>";

        Collection<String> linkList  = App.extractLinks(new ByteArrayInputStream(html.getBytes()), App.getDomainURLPattern("nodejs.org/en/"));
        System.out.println(" Extracted links size:"+linkList.size()+" contents => "+linkList);
        Assert.assertEquals(18, linkList.size());

    }




}
