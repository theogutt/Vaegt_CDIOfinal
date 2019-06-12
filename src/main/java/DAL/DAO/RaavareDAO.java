package DAL.DAO;

import DAL.ConnectionController;
import DAL.DTO.Raavare;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RaavareDAO implements IDAO<Raavare> {

    private ConnectionController connectionController = new ConnectionController();

    @Override
    public int create(Raavare raavare) throws SQLException {
        Connection connection = connectionController.createConnection();

        try{
            connection.setAutoCommit(false);

            PreparedStatement statement = connection.prepareStatement
                    ("INSERT INTO raavare (raavareID, raavareNavn) VALUES (?,?);");
            statement.setInt(1, raavare.getId());
            statement.setString(2,raavare.getNavn());
            statement.executeUpdate();

            connection.commit();
        }catch (SQLException e){
            connection.rollback();
            e.printStackTrace();
        }
        connection.close();

        return 0;
    }

    @Override
    public Raavare get(int id) throws SQLException {
        Connection connection = connectionController.createConnection();
        Raavare raavare = new Raavare();
        raavare.setId(id);

        try{
            connection.setAutoCommit(false);

            PreparedStatement statement = connection.prepareStatement
                    ("SELECT * FROM raavare WHERE raavareID = ?;");
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()){
                raavare.setNavn(resultSet.getString(2));
            }

            connection.commit();
        }catch (SQLException e){
            connection.rollback();
            e.printStackTrace();
        }
        connection.close();
        return raavare;
    }

    @Override
    public Raavare[] getList() throws SQLException {
        List<Raavare> raavareList = new ArrayList<>();
        Connection connection = connectionController.createConnection();
        Raavare[] raavareArray;
        int lastElement;

        try{
            connection.setAutoCommit(false);

            PreparedStatement statement = connection.prepareStatement
                    ("SELECT * FROM raavare;");
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                raavareList.add(new Raavare());
                lastElement = raavareList.size() - 1;
                raavareList.get(lastElement).setId(resultSet.getInt(1));
                raavareList.get(lastElement).setNavn(resultSet.getString(2));
            }
            connection.commit();
        }catch (SQLException e){
            connection.rollback();
            e.printStackTrace();
        }
        connection.close();
        raavareArray = raavareList.toArray(new Raavare[raavareList.size()]);

        return raavareArray;
    }

    @Override
    public void update(Raavare raavare) throws SQLException {
        Connection connection = connectionController.createConnection();

        try{
            connection.setAutoCommit(false);

            PreparedStatement statement = connection.prepareStatement
                    ("UPDATE raavare SET raavareNavn = ? WHERE raavareID = ?;");
            statement.setString(1, raavare.getNavn());
            statement.setInt(2,raavare.getId());
            statement.executeUpdate();

            connection.commit();
        }catch (SQLException e){
            e.printStackTrace();
            connection.rollback();
        }
        connection.close();
    }

    @Override
    public void delete(int id) throws SQLException {
        Connection connection = connectionController.createConnection();

        try{
            connection.setAutoCommit(false);

            PreparedStatement statement = connection.prepareStatement
                    ("DELETE FROM raavare WHERE raavareID = ?;");
            statement.setInt(1,id);
            statement.executeUpdate();

            connection.commit();
        }catch(SQLException e){
            e.printStackTrace();
            connection.rollback();
        }
        connection.close();
    }
}
