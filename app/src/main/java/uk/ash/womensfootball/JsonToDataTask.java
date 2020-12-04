package uk.ash.womensfootball;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import uk.ash.womensfootball.fixture.FixtureData;
import uk.ash.womensfootball.league.LeagueData;

public class JsonToDataTask {

    public List<LeagueData> getLeagueFromJSON(Context context, String jsonString) {
        List<LeagueData> league = new ArrayList<>();
        try {
            JSONObject jsonObj = new JSONObject(jsonString);
            //get the standings array for each team
            JSONArray standingsJsonObj = jsonObj.getJSONObject("api").getJSONArray("standings").getJSONArray(0);
            String name;
            int played, wins, draws, losses, goaldif, points, id;

            for (int x = 0; x < standingsJsonObj.length(); x++) {
                //loop through each team entry and extract data
                JSONObject teamJsonObj = standingsJsonObj.getJSONObject(x);
                name = teamJsonObj.getString("teamName");
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

    public List<FixtureData> getFixtureFromJSON(Context context, String jsonString) {
        List<FixtureData> fixture = new ArrayList<>();
        try {
            JSONObject jsonObj = new JSONObject(jsonString);
            //get the fixtures array
            JSONArray fixturesJsonArray = jsonObj.getJSONObject("api").getJSONArray("fixtures");

            int fixtureId, leagueId, teamIdH, teamIdA;
            Integer homeScore = 0, awayScore = 0;
            String teamNameH, teamNameA;
            LocalDateTime dateTime;

            for (int x = 0; x < fixturesJsonArray.length(); x++) {
                //loop through each fixture entry and extract data
                JSONObject fixtureJsonObj = fixturesJsonArray.getJSONObject(x);
                fixtureId = fixtureJsonObj.getInt("fixture_id");
                leagueId = fixtureJsonObj.getInt("league_id");
                JSONObject homeTeamObj = fixtureJsonObj.getJSONObject("homeTeam");
                teamIdH = homeTeamObj.getInt("team_id");
                teamNameH = homeTeamObj.getString("team_name");
                JSONObject awayTeamObj = fixtureJsonObj.getJSONObject("awayTeam");
                teamIdA = awayTeamObj.getInt("team_id");
                teamNameA = awayTeamObj.getString("team_name");

                try {
                    homeScore = fixtureJsonObj.getInt("goalsHomeTeam");
                } catch (Exception e) {/*ignore because its null, so leave as 0*/}
                try {
                    awayScore = fixtureJsonObj.getInt("goalsAwayTeam");
                } catch (Exception e) {/*ignore because its null, so leave as 0*/}

                dateTime = Converters.ldtFromLong(fixtureJsonObj.getLong("event_timestamp"));

                //add to league list for return
                fixture.add(new FixtureData(fixtureId, leagueId, teamNameH, teamNameA, homeScore, awayScore, dateTime, teamIdH, teamIdA));
            }
            Log.d("JSON TO FIXTURE TASK", "getFixtureFromJSON: " + fixture);
            return fixture;
        } catch (Exception e) {
            Log.d("getFixtureFromJSON ", e.toString());
            return null;
        }
    }
}

