import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuyuan on 4/11/16.
 */
public class TweetBean {
    private String userid;
    private String username;
    private String timestamp;
    private String text;
    private String hashtag;
    private String ip;
    private String coordinates;
    private String repliedby;
    private String reply_count;
    private String mentioned;
    private String mentioned_count;
    private String favoritedby;
    private String favorite_count;
    private String useragent;
    private String filter_level;
    private String lang;

    private static final String USERID = "userid";
    private static final String USERNAME = "username";
    private static final String TIMESTAMP = "timestamp";
    private static final String TEXT = "text";
    private static final String HASHTAG = "hashtag";
    private static final String IP = "ip";
    private static final String COORDINATES = "coordinates";
    private static final String REPLIEDBY = "repliedby";
    private static final String REPLY_COUNT = "reply_count";
    private static final String MENTIONED = "mentioned";
    private static final String MENTIONED_COUNT = "mentioned_count";
    private static final String FAVORITEDBY = "favoritedby";
    private static final String FAVORITE_COUNT = "favorite_count";
    private static final String USERAGENT = "useragent";
    private static final String FILTER_LEVEL = "filter_level";
    private static final String LANG = "lang";

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getHashtag() {
        return hashtag;
    }

    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public String getRepliedby() {
        return repliedby;
    }

    public void setRepliedby(String repliedby) {
        this.repliedby = repliedby;
    }

    public String getReply_count() {
        return reply_count;
    }

    public void setReply_count(String reply_count) {
        this.reply_count = reply_count;
    }

    public String getMentioned() {
        return mentioned;
    }

    public void setMentioned(String mentioned) {
        this.mentioned = mentioned;
    }

    public String getMentioned_count() {
        return mentioned_count;
    }

    public void setMentioned_count(String mentioned_count) {
        this.mentioned_count = mentioned_count;
    }

    public String getFavoritedby() {
        return favoritedby;
    }

    public void setFavoritedby(String favoritedby) {
        this.favoritedby = favoritedby;
    }

    public String getFavorite_count() {
        return favorite_count;
    }

    public void setFavorite_count(String favorite_count) {
        this.favorite_count = favorite_count;
    }

    public String getUseragent() {
        return useragent;
    }

    public void setUseragent(String useragent) {
        this.useragent = useragent;
    }

    public String getFilter_level() {
        return filter_level;
    }

    public void setFilter_level(String filter_level) {
        this.filter_level = filter_level;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }


    public void setCache (String column, String value) {
        switch (column) {
            case USERID: setUserid(value);
                break;
            case USERNAME: setUsername(value);
                break;
            case TIMESTAMP: setTimestamp(value);
                break;
            case TEXT: setText(value);
                break;
            case HASHTAG: setHashtag(value);
                break;
            case IP: setIp(value);
                break;
            case COORDINATES: setCoordinates(value);
                break;
            case REPLIEDBY: setRepliedby(value);
                break;
            case REPLY_COUNT: setReply_count(value);
                break;
            case MENTIONED: setMentioned(value);
                break;
            case MENTIONED_COUNT: setMentioned_count(value);
                break;
            case FAVORITEDBY: setFavoritedby(value);
                break;
            case FAVORITE_COUNT: setFavorite_count(value);
                break;
            case USERAGENT: setUseragent(value);
                break;
            case FILTER_LEVEL: setFilter_level(value);
                break;
            case LANG: setLang(value);
                break;
            default : System.out.println("Yao Shou La!");
        }
    }

    public String getCache (String column) {
        switch (column) {
            case USERID: return getUserid();
            case USERNAME: return getUsername();
            case TIMESTAMP: return getTimestamp();
            case TEXT: return getText();
            case HASHTAG: return getHashtag();
            case IP: return getIp();
            case COORDINATES: return getCoordinates();
            case REPLIEDBY: return getRepliedby();
            case REPLY_COUNT: return getReply_count();
            case MENTIONED: return getMentioned();
            case MENTIONED_COUNT: return getMentioned_count();
            case FAVORITEDBY: return getFavoritedby();
            case FAVORITE_COUNT: return getFavorite_count();
            case USERAGENT: return getUseragent();
            case FILTER_LEVEL: return getFilter_level();
            case LANG: return getLang();
            default : return null;
        }
    }

    public List<Content> getFromBuffer () {
        List<Content> list = new ArrayList<>();
        String userid = getUserid();
        if (userid != null) {
            list.add(new Content(USERID, userid));
        }
        String username = getUsername();
        if (username != null) {
            list.add(new Content(USERNAME, username));
        }
        String timestamp = getTimestamp();
        if (timestamp != null) {
            list.add(new Content(TIMESTAMP, timestamp));
        }
        String text = getText();
        if (text != null) {
            list.add(new Content(TEXT, text));
        }
        String hashtag = getHashtag();
        if (hashtag != null) {
            list.add(new Content(HASHTAG, hashtag));
        }
        String ip = getIp();
        if (ip != null) {
            list.add(new Content(IP, ip));
        }
        String coordinates = getCoordinates();
        if (coordinates != null) {
            list.add(new Content(COORDINATES, coordinates));
        }
        String repliedby = getRepliedby();
        if (repliedby != null) {
            list.add(new Content(REPLIEDBY, repliedby));
        }
        String reply_count = getReply_count();
        if (reply_count != null) {
            list.add(new Content(REPLY_COUNT, reply_count));
        }
        String mentioned = getMentioned();
        if (mentioned != null) {
            list.add(new Content(MENTIONED, mentioned));
        }
        String mentioned_count = getMentioned_count();
        if (mentioned_count != null) {
            list.add(new Content(MENTIONED_COUNT, mentioned_count));
        }
        String favoritedby = getFavoritedby();
        if (favoritedby != null) {
            list.add(new Content(FAVORITEDBY, favoritedby));
        }
        String favorite_count = getFavorite_count();
        if (favorite_count != null) {
            list.add(new Content(FAVORITE_COUNT, favorite_count));
        }
        String useragent = getUseragent();
        if (useragent != null) {
            list.add(new Content(USERAGENT, useragent));
        }
        String filter_level = getFilter_level();
        if (filter_level != null) {
            list.add(new Content(FILTER_LEVEL, filter_level));
        }
        String lang = getLang();
        if (lang != null) {
            list.add(new Content(LANG, lang));
        }
        return list;
    }
}
