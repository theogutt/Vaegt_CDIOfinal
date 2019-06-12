package DAL.DAO;

import java.sql.SQLException;

public interface IDAO<E> {
    int create(E objekt) throws DALException, SQLException;
    E get(int id) throws DALException, SQLException;
    E[] getList() throws DALException, SQLException;
    void update(E objekt) throws DALException, SQLException;
    void delete(int id) throws DALException, SQLException;


    public class DALException extends Exception {
        //Til Java serialisering...
        private static final long serialVersionUID = 7355418246336739229L;

        public DALException(String msg, Throwable e) {
            super(msg, e);
        }
        public DALException(String msg) {
            super(msg);
        }

    }
}
