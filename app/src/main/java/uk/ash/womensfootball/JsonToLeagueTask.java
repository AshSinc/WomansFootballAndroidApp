package uk.ash.womensfootball;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonToLeagueTask {

    private List<LeagueData> league = new ArrayList<>();

    public List<LeagueData> getLeagueFromJSON(Context context, String jsonString) {
        try {
            JSONObject jsonObj = new JSONObject(jsonString);
            //get the standings array for each team
            JSONArray standingsJsonObj = jsonObj.getJSONObject("api").getJSONArray("standings").getJSONArray(0);
            String name;
            int played,wins,draws,losses,goaldif,points,id;

            for (int x = 0 ; x < standingsJsonObj.length(); x++){
                //loop through each team entry and extract data
                JSONObject teamJsonObj = standingsJsonObj.getJSONObject(x);
                name = teamJsonObj.getString("teamName");
                //Drawable badge = ActivityBase.getBadgeForTeam(context ,0);
                JSONObject allJsonObj = teamJsonObj.getJSONObject("all");

                played = allJsonObj.getInt("matchsPlayed");
                wins = allJsonObj.getInt("win");
                draws = allJsonObj.getInt("draw");
                losses = allJsonObj.getInt("lose");
                goaldif = teamJsonObj.getInt("goalsDiff");
                points = teamJsonObj.getInt("points");
                id = teamJsonObj.getInt("team_id");
                //add to league list for return
                league.add(new LeagueData(name, played, wins, draws, losses, goaldif, points, id));
            }
            return league;
        } catch (Exception e) {
            Log.d("getLeagueFromJSON ", e.toString());
            return null;
        }
    }
}
