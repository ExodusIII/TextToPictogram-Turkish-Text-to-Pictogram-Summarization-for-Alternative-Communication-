import java.sql.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.SplittableRandom;
import java.util.stream.Stream;
import java.util.ArrayList;

public class Main {
    static String URL = "jdbc:mysql://localhost/Picto2Text";
    static String USER = "root";
    static String PASS = "248163264:Hakan";
    static String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static String STOP_WORDS_ABSOLUTE_PATH = "D:\\Text to Pictoram for all\\trstop-master\\dosyalar\\turkce-stop-words.txt";
    static String TOP20K_WORDS_ABSOLUTE_PATH = "D:\\Text to Pictoram for all\\Litarature\\top-20K-words.txt";

    public static void main(String[] args) {
        File pictoFolder = new File("Pictos");

        Main listFiles = new Main();
        //ArrayList<String> stopWordsList = listFiles.listAllFiles(STOP_WORDS_ABSOLUTE_PATH); //Return ArrayList: given path of file content
        //ArrayList<String> top20kWordsList = listFiles.listAllFiles(TOP20K_WORDS_ABSOLUTE_PATH);
        ArrayList<String> fileNamesList = listFiles.listAllFiles(pictoFolder); //Return ArrayList: given path of file names

        //Inserting values or creating corresponding databases, tables, columns
        InsertDB(fileNamesList, "pictos", "pictoName"); //Database inserter

    }

    //File name displayer
    static public ArrayList<String> listAllFiles(File pictoFolder) {
        File[] fileNames = pictoFolder.listFiles();
        String fileNameTemp;
        ArrayList<String> fileNameArray = new ArrayList<>();
        for (File file : fileNames) {
            // if directory call the same method again
            if (file.isDirectory()) {
                listAllFiles(file);
            } else {
                fileNameTemp = file.getName().replace(".png", "");
                fileNameTemp = fileNameTemp.replace("'", "");
                fileNameArray.add(fileNameTemp);
            }
        }
        System.out.println("File names listed");
        return fileNameArray;
    }

    // Uses Files.walk method
    static public ArrayList<String> listAllFiles(String path){
        ArrayList<String> fileContent = new ArrayList<>();
        try(Stream<Path> paths = Files.walk(Paths.get(path))) {
            paths.forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    try {
                        fileContent.addAll(readContent(filePath));
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return fileContent;
    }

    static public List<String> readContent(Path filePath) throws IOException {
        List<String> fileList = Files.readAllLines(filePath);
        return fileList;
    }

    static public void InsertDB(ArrayList<String> val, String tableName, String columnName) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(URL, USER, PASS);
            //execute
            stmt = conn.createStatement();
            for (int i = 0; i < val.size(); i++) {
                String fileNameTemp = val.get(i);
                String sqlQuery_Insert = "INSERT INTO " + tableName + " (" + columnName + ")  VALUES ('" +  fileNameTemp + "')";
                stmt.executeUpdate(sqlQuery_Insert);
            }
            rs.close();
            stmt.close();
            conn.close();
            System.out.println("Insert Completed!");
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se2) {
            }// nothing we can do
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try
    }
}
