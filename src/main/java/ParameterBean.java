/**
 * Created by liuyuan on 4/11/16.
 */
public class ParameterBean {
    private String tweetid;
    private String op;
    private int seq;
    private String fields;
    private String payload;

    public String getTweetid() {
        return tweetid;
    }

    public void setTweetid(String tweetid) {
        this.tweetid = tweetid;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
