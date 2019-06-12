package DAL.DAO;

import DAL.ConnectionController;
import DAL.DTO.ProduktBatchKomp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProduktBatchKompDAO implements IKompDAO<ProduktBatchKomp> {
    ConnectionController connectionController = new ConnectionController();

    @Override
    public int create(ProduktBatchKomp produktBatchKomp) throws SQLException {
        Connection connection = connectionController.createConnection();

        try{
            connection.setAutoCommit(false);

            PreparedStatement statement = connection.prepareStatement
                    ("INSERT INTO produktBatchKomp (produktBatchID, raavareBatchID, tara, netto, brugerID) VALUES (?,?,?,?,?);");
            statement.setInt(1, produktBatchKomp.getProduktBatchID());
            statement.setInt(2, produktBatchKomp.getRaavareBatchID());
            statement.setDouble(3, produktBatchKomp.getTara());
            statement.setDouble(4, produktBatchKomp.getNetto());
            statement.setInt(5, produktBatchKomp.getBrugerID());
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
    public ProduktBatchKomp get(int produktBatchID, int raavareBatchID) throws SQLException {
        Connection connection = connectionController.createConnection();
        ProduktBatchKomp produktBatchKomp = new ProduktBatchKomp();
        produktBatchKomp.setProduktBatchID(produktBatchID);
        produktBatchKomp.setRaavareBatchID(raavareBatchID);

        try{
            connection.setAutoCommit(false);

            PreparedStatement statement = connection.prepareStatement
                    ("SELECT * FROM produktBatchKomp WHERE (produktBatchID = ? AND raavareBatchID = ?);");
            statement.setInt(1, produktBatchID);
            statement.setInt(2, raavareBatchID);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()){
                produktBatchKomp.setProduktBatchID(resultSet.getInt(1));
                produktBatchKomp.setRaavareBatchID(resultSet.getInt(2));
                produktBatchKomp.setTara(resultSet.getDouble(3));
                produktBatchKomp.setNetto(resultSet.getDouble(4));
                produktBatchKomp.setBrugerID(resultSet.getInt(5));
            }

            connection.commit();
        }catch (SQLException e){
            connection.rollback();
            e.printStackTrace();
        }
        connection.close();
        return produktBatchKomp;
    }

    @Override
    public ProduktBatchKomp[] getList() throws SQLException {
        List<ProduktBatchKomp> produktBatchKompArrayList = new ArrayList<>();
        Connection connection = connectionController.createConnection();
        try  {
            connection.setAutoCommit(false);//transaction

            PreparedStatement statement = connection.prepareStatement("SELECT * FROM produktBatchKomp");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                produktBatchKompArrayList.add(new ProduktBatchKomp(
                        resultSet.getInt(1),
                        resultSet.getInt(2),
                        resultSet.getInt(3),
                        resultSet.getDouble(4),
                        resultSet.getInt(5)));
            }
            connection.commit();//transaction
        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
        }
        connection.close();
        ProduktBatchKomp[] produktBatchKompList = produktBatchKompArrayList.toArray(new ProduktBatchKomp[produktBatchKompArrayList.size()]);
        return produktBatchKompList;
    }

    @Override
    public ProduktBatchKomp[] getList(int id) throws SQLException {
        List<ProduktBatchKomp> produktBatchKompArrayList = new ArrayList<>();
        Connection connection = connectionController.createConnection();
        try  {
            connection.setAutoCommit(false);//transaction

            PreparedStatement statement = connection.prepareStatement("SELECT * FROM produktBatchKomp WHERE produktBatchID = ?");
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                produktBatchKompArrayList.add(new ProduktBatchKomp(
                        resultSet.getInt(1),
                        resultSet.getInt(2),
                        resultSet.getInt(3),
                        resultSet.getDouble(4),
                        resultSet.getInt(5)));
            }
            connection.commit();//transaction
        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
        }
        connection.close();
        ProduktBatchKomp[] produktBatchKompList = produktBatchKompArrayList.toArray(new ProduktBatchKomp[produktBatchKompArrayList.size()]);
        return produktBatchKompList;
    }

    @Override
    public void update(ProduktBatchKomp produktBatchKomp) throws SQLException {
        Connection connection = connectionController.createConnection();

        try{
            connection.setAutoCommit(false);

            PreparedStatement statement = connection.prepareStatement
                    ("UPDATE produktBatchKomp SET produktBatchID = ?, raavareBatchID = ?, tara = ?, netto = ?, brugerID = ? WHERE (produktBatchID = ? AND raavareBatchID = ?);");
            statement.setInt(1, produktBatchKomp.getProduktBatchID());
            statement.setInt(2, produktBatchKomp.getRaavareBatchID());
            statement.setDouble(3, produktBatchKomp.getTara());
            statement.setDouble(4, produktBatchKomp.getNetto());
            statement.setInt(5, produktBatchKomp.getBrugerID());
            statement.setInt(6, produktBatchKomp.getProduktBatchID());
            statement.setInt(7, produktBatchKomp.getRaavareBatchID());
            statement.executeUpdate();

            connection.commit();
        }catch (SQLException e){
            connection.rollback();
            e.printStackTrace();
        }
        connection.close();
    }

    @Override
    public void delete(int produktBatchKompID, int raavareBatchID) throws SQLException {
        Connection connection = connectionController.createConnection();

        try {
            connection.setAutoCommit(false);//transaction

            PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM produktBatchKomp WHERE  (produktBatchID = ? AND raavareBatchID = ?);");

            statement.setInt(1, produktBatchKompID);
            statement.setInt(2, raavareBatchID);
            statement.executeUpdate();

            connection.commit();//transaction
        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
        }
        connection.close();
    }
}
