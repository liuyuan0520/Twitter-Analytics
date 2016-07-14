package unused;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by liuyuan on 3/16/16.
 */
public class JustRun {
    public static void main(final String[] args) {

        String string = ", b,, c, ,";
        String[] payload = string.split(",");
        System.out.println(payload.length);
        for (int i = 0; i < payload.length; i++) {
            System.out.println("The " + i + " element is" + payload[i]);
        }

//        HttpURLConnection connection = null;
//        try {
//            URL url =new URL("http://ec2-52-91-212-186.compute-1.amazonaws.com/q1?key=54263079947829329549076360910327924204147994810870153634581370422284193934077582988036880968951267328&message=JHUABZPJHSSTAYHUFLLMOJKZTULZPHZVJFSS");
//            connection = (HttpURLConnection) url.openConnection();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            System.out.println("Inputted " + System.nanoTime());
//            connection.getResponseCode();
//            System.out.println("Received " + System.nanoTime());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
        String[] query = {"text", "123"};
        String sql = new StringBuilder().append("SELECT ").append(query[0]).append(" FROM q4 WHERE `tweetid` = '").append(query[1]).append("'").toString();
        System.out.println(sql);

        List<Content> list = new ArrayList<>();
        list.add(new Content("c1", "v1"));
        list.add(new Content("c2", "v2"));
        list.add(new Content("c3", "v3"));
        list.add(new Content("c4", "v4"));

        StringBuilder columnBuilder = new StringBuilder();
        StringBuilder valueBuilder = new StringBuilder();
        StringBuilder updateBuilder = new StringBuilder();
        StringBuilder primaryKeyBuilder = new StringBuilder();

        int size = list.size();
        columnBuilder = columnBuilder.append("`tweetid`").append(",");
        valueBuilder = valueBuilder.append(list.get(0).getValue()).append(", ");
        for (int i = 1; i < list.size() - 1; i++) {
            String column = list.get(i).getColumn();
            String value = list.get(i).getValue();
            columnBuilder = columnBuilder.append("`").append(column).append("`,");
            valueBuilder = valueBuilder.append("'").append(value).append("',");
            updateBuilder = updateBuilder.append("`").append(column).append("`=").append("'").append(value).append("',");
        }
        String column = list.get(size - 1).getColumn();
        String value = list.get(size - 1).getValue();
        columnBuilder.append("`").append(column).append("`");
        valueBuilder.append("'").append(value).append("'");
        updateBuilder.append("`").append(column).append("`=").append("'").append(value).append("'");
        sql = new StringBuilder().append("INSERT INTO q4 (").append(columnBuilder).append(") VALUES (").append(valueBuilder).append(") ON DUPLICATE KEY UPDATE ").append(updateBuilder).toString();
        System.out.println(sql);

        primaryKeyBuilder.append(" WHERE `tweetid` = ").append(list.get(0).getValue());
        sql = new StringBuilder().append("UPDATE q4 SET ").append(updateBuilder).append(primaryKeyBuilder).toString();
        System.out.println(sql);
    }
}
