package DAL.DAO;

import DAL.ConnectionController;
import DAL.DTO.RaavareBatch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RaavareBatchDAO implements IDAO<RaavareBatch> {

    ConnectionController connectionController = new ConnectionController();

    @Override
    public int create(RaavareBatch raavareBatch) throws SQLException {
        Connection connection = connectionController.createConnection();

        try{
            connection.setAutoCommit(false);

            PreparedStatement statement = connection.prepareStatement
                    ("INSERT INTO raavareBatch (raavareBatchID, raavareID, maengde, leverandoer) VALUES (?,?,?,?);");
            statement.setInt(1, raavareBatch.getId());
            statement.setInt(2, raavareBatch.getRaavareId());
            statement.setDouble(3, raavareBatch.getMaengde());
            statement.setString(4, raavareBatch.getLeverandoer());
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
    public RaavareBatch get(int id) throws SQLException {
        Connection connection = connectionController.createConnection();
        RaavareBatch raavareBatch = new RaavareBatch();
        raavareBatch.setId(id);

        try{
            connection.setAutoCommit(false);

            PreparedStatement statement = connection.prepareStatement
                    ("SELECT * FROM raavareBatch WHERE raavareBatchID = ?;");
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()){
                raavareBatch.setRaavareId(resultSet.getInt(2));
                raavareBatch.setMaengde(resultSet.getDouble(3));
                raavareBatch.setLeverandoer(resultSet.getString(4));
            }

            connection.commit();
        }catch (SQLException e){
            connection.rollback();
            e.printStackTrace();
        }
        connection.close();
        return raavareBatch;
    }

    @Override
    public RaavareBatch[] getList() throws SQLException {
        List<RaavareBatch> raavareBatchArrayList = new ArrayList<>();
        Connection connection = connectionController.createConnection();
        try  {
            connection.setAutoCommit(false);//transaction

            PreparedStatement statement = connection.prepareStatement("SELECT * FROM raavareBatch;");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                raavareBatchArrayList.add(new RaavareBatch(
                        resultSet.getInt(1),
                        resultSet.getInt(2),
                        resultSet.getDouble(3),
                        resultSet.getString(4)));
            }
            connection.commit();//transaction
        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
        }
        connection.close();
        RaavareBatch[] raavareBatchList = raavareBatchArrayList.toArray(new RaavareBatch[raavareBatchArrayList.size()]);
        return raavareBatchList;
    }

    @Override
    public void update(RaavareBatch raavareBatch) throws SQLException {
        Connection connection = connectionController.createConnection();

        try {
            connection.setAutoCommit(false);//transaction

            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE raavareBatch SET raavareBatchId = ?, raavareId = ?, maengde = ?, leverandoer = ? WHERE raavareBatchID = ?;");

            statement.setInt(1, raavareBatch.getId());
            statement.setInt(2, raavareBatch.getRaavareId());
            statement.setDouble(3, raavareBatch.getMaengde());
            statement.setString(4, raavareBatch.getLeverandoer());
            statement.setInt(5, raavareBatch.getId());
            statement.executeUpdate();

            connection.commit();//transaction
        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
        }
        connection.close();
    }

    @Override
    public void delete(int id) throws SQLException {

        Connection connection = connectionController.createConnection();

        try {
            connection.setAutoCommit(false);//transaction

            PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM raavareBatch WHERE raavareBatchID = ?;");

            statement.setInt(1, id);
            statement.executeUpdate();

            connection.commit();//transaction
        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
        }
        connection.close();
    }
}
