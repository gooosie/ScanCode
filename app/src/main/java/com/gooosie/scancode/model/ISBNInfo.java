package com.gooosie.scancode.model;

import com.gooosie.scancode.util.StringAppendUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Arrays;

/**
 * ISBNInfo
 */

public class ISBNInfo extends BaseModel{

    private static final String ORIGIN_TITLE = "origin_title";
    private static final String SUBTITLE = "subtitle";
    private static final String TITLE = "title";
    private static final String AUTHOR = "author";
    private static final String AUTHOR_INTRO = "author_intro";
    private static final String TRANSLATOR = "translator";
    private static final String CATALOG = "catalog";
    private static final String PAGES = "pages";
    private static final String PUBDATE = "pubdate";
    private static final String PUBLISHER = "publisher";
    private static final String BINDING = "binding";
    private static final String SUMMARY = "summary";
    private static final String PRICE = "price";

    private String title;
    private String subtitle;
    private String originTitle;
    private String[] author;
    private String authorIntro;
    private String[] translator;
    private String catalog;
    private String pages;
    private String pubdate;
    private String publisher;
    private String binding;
    private String summary;
    private String price;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getOriginTitle() {
        return originTitle;
    }

    public void setOriginTitle(String originTitle) {
        this.originTitle = originTitle;
    }

    public String[] getAuthor() {
        return author;
    }

    public void setAuthor(String[] author) {
        this.author = author;
    }

    public String getAuthorIntro() {
        return authorIntro;
    }

    public void setAuthorIntro(String authorIntro) {
        this.authorIntro = authorIntro;
    }

    public String[] getTranslator() {
        return translator;
    }

    public void setTranslator(String[] translator) {
        this.translator = translator;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public String getPubdate() {
        return pubdate;
    }

    public void setPubdate(String pubdate) {
        this.pubdate = pubdate;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getBinding() {
        return binding;
    }

    public void setBinding(String binding) {
        this.binding = binding;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public static ISBNInfo parse(String jsonString) {
        ISBNInfo info = null;
        try {
            Object o = new JSONTokener(jsonString).nextValue();
            if (!(o instanceof JSONObject)) {
                return null;
            }
            JSONObject json = (JSONObject) o;
            info = new ISBNInfo();

            info.setTitle(json.optString(TITLE));
            info.setSubtitle(json.optString(SUBTITLE));
            info.setOriginTitle(json.optString(ORIGIN_TITLE));

            JSONArray authorArray = json.optJSONArray(AUTHOR);
            if (authorArray != null) {
                int length = authorArray.length();
                String[] authors = new String[length];
                for (int i = 0; i < length; i++) {
                    authors[i] = authorArray.optString(i);
                }
                info.setAuthor(authors);
            }

            info.setAuthorIntro(json.optString(AUTHOR_INTRO));

            JSONArray translatorArray = json.optJSONArray(TRANSLATOR);
            if (translatorArray != null) {
                int length = translatorArray.length();
                String[] translator = new String[length];
                for (int i = 0; i < length; i++) {
                    translator[i] = translatorArray.optString(i);
                }
                info.setTranslator(translator);
            }

            info.setCatalog(json.optString(CATALOG));

            info.setPages(json.optString(PAGES));

            info.setPubdate(json.optString(PUBDATE));

            info.setPublisher(json.optString(PUBLISHER));

            info.setBinding(json.optString(BINDING));

            info.setSummary(json.optString(SUMMARY));

            info.setPrice(json.optString(PRICE));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return info;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(1024);
        StringAppendUtil.appendDetail(builder, "标题", title);
        StringAppendUtil.appendDetail(builder, "副标题", subtitle);
        StringAppendUtil.appendDetail(builder, "原标题", originTitle);
        StringAppendUtil.appendDetail(builder, "作者", Arrays.toString(author).replace(',', '\n'));
        StringAppendUtil.appendDetail(builder, "译者", Arrays.toString(translator).replace(',', '\n'));
        StringAppendUtil.appendDetail(builder, "出版社", publisher);
        StringAppendUtil.appendDetail(builder, "封装", binding);
        StringAppendUtil.appendDetail(builder, "页数", pages);
        StringAppendUtil.appendDetail(builder, "价格", price);
        StringAppendUtil.appendDetail(builder, "简介", summary);
        StringAppendUtil.appendDetail(builder, "作者简介", authorIntro);
        StringAppendUtil.appendDetail(builder, "目录", catalog);
        return builder.toString();
    }
}
