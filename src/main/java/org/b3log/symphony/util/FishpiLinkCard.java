/*
 * Rhythm - A modern community (forum/BBS/SNS/blog) platform written in Java.
 * Modified version from Symphony, Thanks Symphony :)
 * Copyright (C) 2012-present, b3log.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.b3log.symphony.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class FishpiLinkCard {

    // HTML转义，防止XSS
    public static String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    // 字符串缩短
    public static String shorten(String text, int maxLen) {
        if (text == null) return "";
        text = text.trim();
        if (text.length() <= maxLen) return text;
        return text.substring(0, maxLen) + "...";
    }

    // 去除标题后缀
    public static String cleanTitle(String title) {
        if (title == null) return "";
        // 可扩展：去除常见后缀
        return title.replaceAll("\\s*-\\s*摸鱼派\\s*-\\s*白与画科技\\s*$", "")
                .replaceAll("\\s*-\\s*摸鱼派\\s*$", "")
                .replaceAll("\\s*-\\s*白与画科技\\s*$", "");
    }

    public static final Map<String, String> cardCache = Collections.synchronizedMap(new LinkedHashMap<String, String>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > 2000;
        }
    });
    // 生成卡片HTML
    public static String generateCard(String url) {
        if (cardCache.containsKey(url)) {
            return cardCache.get(url);
        }
        String icon = null;
        String title = url;
        String desc = "";
        String apiKey = Symphonys.get("linkCard.apiKey");
        String fetchUrl;
        try {
            if (url.contains("?")) {
                fetchUrl = url + "&apiKey=" + apiKey;
            } else {
                fetchUrl = url + "?apiKey=" + apiKey;
            }
            fetchUrl = fetchUrl.replaceFirst("fishpi.cn", "127.0.0.1");
            Document doc = Jsoup.connect(fetchUrl).timeout(5000).header("Host", "fishpi.cn").get();

            // 优先用第一张图片
            Elements imgs = doc.select("img[src]");
            if (!imgs.isEmpty()) {
                icon = imgs.get(0).absUrl("src");
            }

            // 没有图片再用favicon
            if ((icon == null || icon.isEmpty())) {
                Element iconEl = doc.selectFirst("link[rel~=(?i)^(shortcut|icon|apple-touch-icon)]");
                if (iconEl != null) {
                    icon = iconEl.absUrl("href");
                }
            }

            // 标题
            title = doc.title();
            title = cleanTitle(title);

            // 描述
            Element descEl = doc.selectFirst("meta[name=description],meta[property=og:description]");
            if (descEl != null) {
                desc = descEl.attr("content");
            }
        } catch (Exception e) {
            try {
                fetchUrl = url;
                fetchUrl = fetchUrl.replaceFirst("fishpi.cn", "127.0.0.1");
                Document doc = Jsoup.connect(fetchUrl).timeout(5000).header("Host", "fishpi.cn").cookie("sym-ce", Symphonys.get("linkCard.symce")).get();

                // 优先用第一张图片
                Elements imgs = doc.select("img[src]");
                if (!imgs.isEmpty()) {
                    icon = imgs.get(0).absUrl("src");
                }

                // 没有图片再用favicon
                if ((icon == null || icon.isEmpty())) {
                    Element iconEl = doc.selectFirst("link[rel~=(?i)^(shortcut|icon|apple-touch-icon)]");
                    if (iconEl != null) {
                        icon = iconEl.absUrl("href");
                    }
                }

                // 标题
                title = doc.title();
                title = cleanTitle(title);

                // 描述
                Element descEl = doc.selectFirst("meta[name=description],meta[property=og:description]");
                if (descEl != null) {
                    desc = descEl.attr("content");
                }
            } catch (Exception f) {
                f.printStackTrace();
            }
        }

        // 如果没有图片，直接返回原样
        if (icon == null || icon.isEmpty()) {
            return null;
        }

        // 缩短标题和描述
        String safeTitle = escapeHtml(shorten(title, 40));      // 标题最多40字
        String safeDesc = escapeHtml(shorten(desc, 60));        // 描述最多60字

        // 卡片HTML，整张卡片可点击
        StringBuilder card = new StringBuilder();
        card.append("<div class='link-card' style='display:flex;border:1px solid #eee;border-radius:8px;padding:10px;margin:10px 0;background:#fafafa;max-width:420px;font-family:Segoe UI,PingFang SC,Helvetica Neue,Arial,sans-serif;cursor:pointer;box-shadow:0 0 0 rgba(0,0,0,0);transition:box-shadow 0.2s;'")
                .append(" onclick=\"window.open('").append(escapeHtml(url)).append("','_blank')\"")
                .append(" onmouseover=\"this.style.boxShadow='0 4px 16px rgba(0,0,0,0.12)'\"")
                .append(" onmouseout=\"this.style.boxShadow='0 0 0 rgba(0,0,0,0)'\">");
        card.append("<img src='").append(escapeHtml(icon)).append("' style='width:56px;height:56px;margin-right:14px;object-fit:cover;border-radius:8px;'/>");
        card.append("<div style='flex:1;display:flex;flex-direction:column;justify-content:center;'>")
                .append("<div style='font-weight:600;font-size:16px;line-height:1.3;margin-bottom:4px;'>").append(safeTitle).append("</div>");
        if (!safeDesc.isEmpty()) {
            card.append("<div style='color:#666;font-size:13px;margin-bottom:6px;line-height:1.4;'>").append(safeDesc).append("</div>");
        }
        card.append("<div style='color:#333;font-size:12px;text-decoration:none;word-break:break-all;'>").append(escapeHtml(url)).append("</div>")
                .append("</div></div>");

        cardCache.put(url, card.toString());
        return card.toString();
    }

    // 处理HTML，替换站内链接<a>为卡片
    public static String processHtml(String rawHtml) {
        Document doc = Jsoup.parse(rawHtml);

        // 只处理 <p> 里面只有一个 a 标签且没有其他内容的情况
        for (Element p : doc.select("p")) {
            // 检查是否只有一个子节点，且是 <a>
            if (p.children().size() == 1 && p.child(0).tagName().equals("a")) {
                Element a = p.child(0);

                // 检查 <p> 是否只有 <a>，没有其他文本
                String pText = p.text();
                String aText = a.text();
                // p.text() 只会返回 a 的文本，如果没有其他文本则相等
                if (pText.equals(aText)) {
                    String href = a.attr("href");
                    if (href.startsWith("https://fishpi.cn")) {
                        String cardHtml = generateCard(href);
                        if (cardHtml != null) {
                            Element cardElement = Jsoup.parse(cardHtml).body().child(0);
                            // 用卡片替换整个 <p>
                            p.replaceWith(cardElement);
                        }
                    }
                }
            }
        }

        return doc.body().html();
    }
}


