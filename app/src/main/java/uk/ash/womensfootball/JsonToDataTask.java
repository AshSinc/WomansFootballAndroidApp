package uk.ash.womensfootball;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import uk.ash.womensfootball.event.EventData;
import uk.ash.womensfootball.fixture.FixtureData;
import uk.ash.womensfootball.league.LeagueData;

public class JsonToDataTask {

    private boolean checkForError(JSONObject jsonObj){
        try {
            if(jsonObj.getJSONObject("api").has("error"))
                return true;
        } catch (JSONException e) {
            Log.d("DEBUGAPI", "checkForError: " + e.toString());
        }
        return false;
    }

    public List<LeagueData> getLeagueFromJSON(String jsonString) {
        List<LeagueData> league = new ArrayList<>();
        try {
            JSONObject jsonObj = new JSONObject(jsonString);

            if(checkForError(jsonObj))
                return league;
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

    public List<FixtureData> getFixtureFromJSON(String jsonString) {
        List<FixtureData> fixture = new ArrayList<>();
        try {
            JSONObject jsonObj = new JSONObject(jsonString);

            if(checkForError(jsonObj))
                return fixture;
            //get the fixtures array
            JSONArray fixturesJsonArray = jsonObj.getJSONObject("api").getJSONArray("fixtures");

            int fixtureId, leagueId, teamIdH, teamIdA;
            Integer homeScore = 0, awayScore = 0;
            String teamNameH, teamNameA;
            LocalDateTime dateTime;
            boolean gameComplete = false;

            for (int x = 0; x < fixturesJsonArray.length(); x++) {
                //loop through each fixture entry and extract data
                JSONObject fixtureJsonObj = fixturesJsonArray.getJSONObject(x);
                fixtureId = fixtureJsonObj.getInt("fixture_id");
                leagueId = fixtureJsonObj.getInt("league_id");

                String statusShort = fixtureJsonObj.getString("statusShort");
                if(statusShort.contains("FT") || statusShort.contains("AET") || statusShort.contains("PEN") ||
                statusShort.contains("SUSP") || statusShort.contains("INT") || statusShort.contains("PST") ||
                statusShort.contains("CANC") || statusShort.contains("ABD") || statusShort.contains("AWD"))
                    gameComplete = true;
                else
                    gameComplete = false;

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
                fixture.add(new FixtureData(fixtureId, leagueId, teamNameH, teamNameA, homeScore, awayScore, dateTime, teamIdH, teamIdA, gameComplete));
            }
            Log.d("JSON TO FIXTURE TASK", "getFixtureFromJSON: " + fixture);
            return fixture;
        } catch (Exception e) {
            Log.d("getFixtureFromJSON ", e.toString());
            return null;
        }
    }

    public List<EventData> getEventFromJSON(String jsonString, int fixtureId, int homeTeamId) {
        List<EventData> events = new ArrayList<>();
        try {
            JSONObject jsonObj = new JSONObject(jsonString);

            if(checkForError(jsonObj))
                return events;

            //get the events array
            JSONArray eventsJsonObj = jsonObj.getJSONObject("api").getJSONArray("events");
            for (int x = 0; x < eventsJsonObj.length(); x++) {
                boolean away; //tracks if home or away
                if (homeTeamId == eventsJsonObj.getJSONObject(x).getInt("team_id"))
                    away = false;
                else
                    away = true;

                int time = eventsJsonObj.getJSONObject(x).getInt("elapsed"); //stores time of event in minutes
                String type = eventsJsonObj.getJSONObject(x).getString("type");
                String player = eventsJsonObj.getJSONObject(x).getString("player");
                String detail = eventsJsonObj.getJSONObject(x).getString("detail");
                String[] typeAndDescription = getTypeAndDescOfEvent(type, player, detail);
                events.add(new EventData(typeAndDescription[0], fixtureId, away, time, typeAndDescription[1]));
            }
            return events;
        } catch (Exception e) {
            Log.d("getLeagueFromJSON ", e.toString());
            return null;
        }
    }

    public String[] getTypeAndDescOfEvent(String type, String player, String detail) {
        String[] returnStringArray = new String[2];
        if (type.matches("Goal")) {
            if (detail.matches("Missed Penalty")) {
                returnStringArray[0] = "Miss";
                returnStringArray[1] = player + " missed a penalty!";
            } else if (detail.matches("Normal Goal")) {
                returnStringArray[0] = "Goal";
                returnStringArray[1] = player + " scored!";
            } else if (detail.matches("Penalty")) {
                returnStringArray[0] = "Goal";
                returnStringArray[1] = player + " scored a penalty";
            } else if (detail.matches("Own Goal")) {
                returnStringArray[0] = "Goal";
                returnStringArray[1] = player + " scored own Goal!";
            } else { //if we dont know what it is, just add it raw
                returnStringArray[0] = "Goal";
                returnStringArray[1] = player + " " + detail;
            }
        } else if (type.matches("subst")) {
            returnStringArray[0] = "Sub";
            returnStringArray[1] = player + " subbed for " + detail;
        } else if (type.matches("Card")) {
            if (detail.matches("Yellow Card")) {
                returnStringArray[0] = "Yellow";
                returnStringArray[1] = player + " given a Yellow";
            } else if (detail.matches("Red Card")) {
                returnStringArray[0] = "Red";
                returnStringArray[1] = player + " given a Red!";
            } else {//if we dont know what it is, just add it raw
                returnStringArray[0] = "Red";
                returnStringArray[1] = player + " " + detail;
            }
        }
        else{ //if we dont know what it is, just add it raw
            returnStringArray[0] = "Miss";
            returnStringArray[1] = player + " " + detail;
        }

        return returnStringArray;
    }
}

