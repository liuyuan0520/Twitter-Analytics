package unused;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

/**
 * Created by liuyuan on 3/31/16.
 */
public class ScoreBoard {

    private static ArrayList<TeamInfo> list = new ArrayList<>();

    public static void main(String[] args) {

        try {

            FileReader fileReader = new FileReader("ScoreBoard.csv");
            BufferedReader br = new BufferedReader(fileReader);
            String input;

            while ((input = br.readLine()) != null) {
                String[] parts = input.split(",");

                String teamName = parts[1];
                double score = Double.parseDouble(parts[3]);


                boolean flag = false;
                for (TeamInfo t : list) {
                    if (teamName.equals(t.name)) {
                        t.score = t.score + score;
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    list.add(new TeamInfo(teamName, score));
                }
            }

            for (TeamInfo t : list) {
                if (t.name.equals("Hardship")) {
                    t.score = t.score * 1.03;
                    continue;
                }
                if (t.name.equals("Sugoyi")) {
                    t.score = t.score * 1.04;
                    continue;
                }
                if (t.name.equals("MyLittlePony")) {
                    t.score = t.score * 1.07;
                    continue;
                }
                if (t.name.equals("RenRenYouOffer")) {
                    t.score = t.score * 1.01;
                    continue;
                }
                if (t.name.equals("MIB")) {
                    t.score = t.score * 1.02;
                    continue;
                }
                if (t.name.equals("ccfighter")) {
                    t.score = t.score * 1.04;
                    continue;
                }
                if (t.name.equals("Apollo")) {
                    t.score = t.score * 1.05;
                    continue;
                }
                if (t.name.equals("elder")) {
                    t.score = t.score * 1.04;
                    continue;
                }
                if (t.name.equals("MyHeartIsInTheWork")) {
                    t.score = t.score * 1.03;
                    continue;
                }
                if (t.name.equals("OnePiece")) {
                    t.score = t.score * 1.01;
                    continue;
                }

            }

            Collections.sort(list, new Comparator<TeamInfo>() {
                @Override
                public int compare(TeamInfo o1, TeamInfo o2) {
                    if (o2.score > o1.score) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });

            int i = 0;
            for (TeamInfo t : list) {
                i++;
                System.out.println(i + "\t" + t.name + "\t" + t.score);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class TeamInfo {
        private String name;
        private double score = 0;

        public TeamInfo(String name, double score) {
            this.name = name;
            this.score = score;
        }
    }
}
