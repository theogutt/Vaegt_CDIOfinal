package DAL.DAO;

import DAL.ConnectionController;
import DAL.DTO.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO implements IDAO<User> {

    private ConnectionController connectionController = new ConnectionController();

    @Override
    public int create(User user) throws DALException, SQLException {
        Connection connection = connectionController.createConnection();

        try{
            connection.setAutoCommit(false);

            PreparedStatement statement = connection.prepareStatement
                    ("INSERT INTO bruger (brugerID, brugerNavn, ini, cpr, aktiv) VALUES (?,?,?,?,?);");
            statement.setInt(1, user.getId());
            statement.setString(2,user.getNavn());
            statement.setString(3,user.getIni());
            statement.setString(4,user.getCpr());
            statement.setBoolean(5,true);
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
    public User get(int id) throws DALException, SQLException {
        Connection connection = connectionController.createConnection();
        User user = new User();
        user.setId(id);

        try{
            connection.setAutoCommit(false);

            PreparedStatement statement = connection.prepareStatement
                    ("SELECT * FROM bruger WHERE brugerID = ?;");
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()){
                user.setNavn(resultSet.getString(2));
                user.setIni(resultSet.getString(3));
                user.setCpr(resultSet.getString(4));
                user.setAktiv(resultSet.getBoolean(5));
            }

            connection.commit();
        }catch (SQLException e){
            connection.rollback();
            e.printStackTrace();
        }
        connection.close();
        return user;
    }

    @Override
    public User[] getList() throws DALException, SQLException {
        List<User> userList = new ArrayList<>();
        Connection connection = connectionController.createConnection();
        User[] userArray;
        int lastElement;

        try{
            connection.setAutoCommit(false);

            PreparedStatement statement = connection.prepareStatement
                    ("SELECT * FROM bruger;");
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                userList.add(new User());
                lastElement = userList.size() - 1;
                userList.get(lastElement).setId(resultSet.getInt(1));
                userList.get(lastElement).setNavn(resultSet.getString(2));
                userList.get(lastElement).setIni(resultSet.getString(3));
                userList.get(lastElement).setCpr(resultSet.getString(4));
                userList.get(lastElement).setAktiv(resultSet.getBoolean(5));
            }
            connection.commit();
        }catch (SQLException e){
            connection.rollback();
            e.printStackTrace();
        }
        connection.close();
        userArray = userList.toArray(new User[userList.size()]);

        return userArray;
    }

    @Override
    public void update(User user) throws DALException, SQLException {
        Connection connection = connectionController.createConnection();

        try{
            connection.setAutoCommit(false);

            PreparedStatement statement = connection.prepareStatement
                    ("UPDATE bruger SET brugerNavn = ?, ini = ?, cpr = ?, aktiv = ? WHERE brugerID = ?;");
            statement.setString(1, user.getNavn());
            statement.setString(2, user.getIni());
            statement.setString(3, user.getCpr());
            statement.setBoolean(4, user.isAktiv());
            statement.setInt(5, user.getId());
            statement.executeUpdate();

            connection.commit();

        }catch (SQLException e){
            e.printStackTrace();
            connection.rollback();
        }
        connection.close();
    }

    @Override
    public void delete(int id) throws DALException, SQLException {
        Connection connection = connectionController.createConnection();
        boolean aktiv = false;

        try{
            connection.setAutoCommit(false);

            PreparedStatement statement = connection.prepareStatement
                    ("SELECT aktiv FROM bruger WHERE brugerID = ?;");
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next())
                aktiv = resultSet.getBoolean("aktiv");
            statement = connection.prepareStatement
                    ("UPDATE bruger SET aktiv = ? WHERE brugerID = ?;");
            statement.setBoolean(1, !aktiv);
            statement.setInt(2, id);
            statement.executeUpdate();

            connection.commit();
        }catch(SQLException e){
            e.printStackTrace();
            connection.rollback();
        }
        connection.close();
    }
}
