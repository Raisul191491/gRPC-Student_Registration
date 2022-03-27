package Service;

import com.project.grpc.register.Student;
import com.project.grpc.register.studentGrpc;
import io.grpc.stub.StreamObserver;

import java.sql.*;

import static java.sql.DriverManager.getConnection;

public class registration extends studentGrpc.studentImplBase{

    //MySQL info
    String url = "jdbc:mysql://localhost:3306/student_registration";
    String user = "root";
    String pass = "";

    @Override
    public void login(Student.LoginRequest request, StreamObserver<Student.Response> responseObserver) throws SQLException, ClassNotFoundException {
        String userName = request.getUserName();
        String password = request.getPassword();

        //Checking database
        ResultSet resultSet = checkLoginInfo(userName, password);

        //Creating response
        Student.Response.Builder response = new Student.Response.Builder();
        while(resultSet.next()) {
            if (resultSet.getInt(1) == 1) {
                response.setResponseCode(200).
                        setResponse("Successfully logged in");
            } else {
                response.setResponseCode(404).setResponse("Wrong username or password");
            }
            break;
        }
        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    private ResultSet checkLoginInfo(String userName, String password) throws ClassNotFoundException, SQLException {
        //Connecting to MySQL database
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = getConnection(url, user, pass);
        PreparedStatement statement = connection.prepareStatement("SELECT EXISTS(SELECT * FROM login_info" +
                " WHERE username = ? && pass = ?)");
        statement.setString(1, userName);
        statement.setString(2, password);
        ResultSet rs = statement.executeQuery();
        return rs;
    }

    @Override
    public void register(Student.RegisterRequest request, StreamObserver<Student.RegResponse> responseObserver) throws SQLException {
        long regID = request.getRegistrationID();
        String studentName = request.getStudentName();

        //Checking database
        ResultSet resultSet = checkRegInfo(regID);

        //Creating response
        Student.RegResponse.Builder regResponse = new Student.RegResponse.Builder();
        while(resultSet.next()){
            if(resultSet.getInt(1) == 1){
                regResponse
                        .setResponse("Registration ID " + regID + " is already registered")
                        .setResponseCode(500);
            }else{
                Connection connection = getConnection(url, user, pass);
                //Adding new student
                PreparedStatement statement = connection.prepareStatement
                        ("INSERT INTO registration_list VALUES('"+regID+"', '"+studentName+"')");
                statement.executeUpdate();
                regResponse.setResponse(studentName +
                                " with registration ID " + regID + " is now registered successfully").
                        setResponseCode(300);
            }
            break;
        }
        responseObserver.onNext(regResponse.build());
        responseObserver.onCompleted();
    }

    private ResultSet checkRegInfo(long regID) throws SQLException {
        //Connecting to MySQL database
        Connection connection = getConnection(url, user, pass);
        PreparedStatement statement = connection.prepareStatement
                ("SELECT EXISTS(SELECT * FROM registration_list WHERE Reg_ID = ?)");
        statement.setInt(1, (int) regID);
        ResultSet rs = statement.executeQuery();
        return rs;
    }

    @Override
    public void logout(Student.LogoutRequest request, StreamObserver<Student.Response> responseObserver) {
    }
}
