import javax.annotation.Resource;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author : Sandun Induranga
 * @since : 0.1.0
 **/

@WebServlet(urlPatterns = "/customer")
public class CustomerServlet extends HttpServlet {

    @Resource(name = "java:comp/env/jdbc/pool")
    DataSource dataSource;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String cusId = req.getParameter("cusId");
        String cusName = req.getParameter("cusName");
        String cusAddress = req.getParameter("cusAddress");
        String cusSalary = req.getParameter("cusSalary");

        try {
            Connection connection = dataSource.getConnection();

            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Customer VALUES (?,?,?,?)");

            preparedStatement.setString(1,cusId);
            preparedStatement.setString(2,cusName);
            preparedStatement.setString(3,cusAddress);
            preparedStatement.setDouble(4,Double.parseDouble(cusSalary));

            boolean b = preparedStatement.executeUpdate() > 0;
            if (b){
                resp.setStatus(200);
                resp.getWriter().write("Success");
            }else {
                resp.setStatus(500);
                resp.getWriter().write("Failed");
            }

            connection.close();

        } catch (SQLException e) {

        }

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Connection connection = dataSource.getConnection();

            PreparedStatement pstm = connection.prepareStatement("SELECT * FROM Customer");
            ResultSet resultSet = pstm.executeQuery();

            JsonArrayBuilder allCustomers = Json.createArrayBuilder();

            while (resultSet.next()){
                JsonObjectBuilder customer = Json.createObjectBuilder();
                customer.add("id",resultSet.getString(1));
                customer.add("name",resultSet.getString(2));
                customer.add("address",resultSet.getString(3));
                customer.add("salary",resultSet.getString(4));

                allCustomers.add(customer.build());
            }

            connection.close();

            resp.getWriter().print(allCustomers.build());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
