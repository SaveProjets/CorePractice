package fr.edminecoreteam.corepractice.data;

import fr.edminecoreteam.corepractice.edorm.MySQL;
import fr.edminecoreteam.corepractice.gui.LeaderBoardGui;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LeaderBoardData
{

    private final String table;
    private String p;

    public LeaderBoardData()
    {
        this.table = "ed_practice";
    }

    public LeaderBoardData(String p) {
        this.p = p;
        this.table = "ed_practice";
    }

    public List<String> getTopPlayers(String getValue) {
        List<String> topPlayers = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = MySQL.getConnection().prepareStatement("SELECT player_name FROM " + table + " ORDER BY " + getValue + " DESC LIMIT 10");

            String id = "";
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next())
            {
                id = rs.getString(getValue);
                topPlayers.add(id);
            }
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return topPlayers;
    }

    public int getGameData(String valueToGet)
    {
        try
        {
            PreparedStatement preparedStatement = MySQL.getConnection().prepareStatement("SELECT " + valueToGet + " FROM " + table + " WHERE player_name = ?");
            preparedStatement.setString(1, p);
            int id = 0;
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next())
            {
                id = rs.getInt(valueToGet);
            }
            preparedStatement.close();
            return id;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return 0;
        }
    }
}
