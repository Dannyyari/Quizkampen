package ServerSide;

import Questions.DAO;
import Questions.QuestionsAndAnswers;
import Properties.RoundSettings;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread implements Serializable{
    ServerSidePlayer playerOneSocket;
    ServerSidePlayer playerTwoSocket;

    private ObjectOutputStream toPlayerOne;
    private ObjectOutputStream toPlayerTwo;
    private ObjectInputStream fromPlayerOne;
    private ObjectInputStream fromPlayerTwo;

    private List<QuestionsAndAnswers> questions;
    private int currentQuestionIndex=0; // FRÅGA! Ska inte index börja från 0 för att veta var man är?
    private String pathToSport = "src/Questions/textfiles/SportQuestions";
    private String pathToGeo = "src/Questions/textfiles/GeoQuestions";
    private String pathToAnatomy = "src/Questions/textfiles/AnatomyQuestions";
    private String pathToHistory= "src/Questions/textfiles/HistoryQuestions";

    private DAO sportQuestions= new DAO("Sport", pathToSport);
    private DAO anatomyQuestions= new DAO("Anatomy", pathToAnatomy);
    private DAO geoQuestions=new DAO("Geo", pathToGeo);
    private DAO historyQuestions= new DAO("History", pathToHistory);


    static RoundSettings settings = settings = new RoundSettings();

    //variabler för resten av logiken
    private int playerOneScore;
    private int playerTwoScore;
    private final static int totalQuestions = settings.getQuestions();
    private final static int totalRounds = settings.getRounds();

    // dessa två måste någ justeras då vi kommer skicka Seraliserade objekt?
    // Titta igenom denna fråga och se ifall vi behöver ändra

    // try(PrintWriter toUser= new PrintWriter(socket.getOutputStream(), true);
    // BufferedReader fromUser=new BufferedReader(new InputStreamReader(socket.getInputStream()));


    //kopplar två spelare
    public Server(ServerSidePlayer PlayerOne, ServerSidePlayer PlayerTwo) throws IOException {
        this.playerOneSocket = PlayerOne;
        this.playerTwoSocket = PlayerTwo;

            try {
                toPlayerOne = new ObjectOutputStream(playerOneSocket.getSock().getOutputStream());
                toPlayerTwo = new ObjectOutputStream(playerTwoSocket.getSock().getOutputStream());
                fromPlayerOne = new ObjectInputStream(playerOneSocket.getSock().getInputStream());
                fromPlayerTwo = new ObjectInputStream(playerTwoSocket.getSock().getInputStream());

            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
    }

      //---------------ALTERNATIV METOD FÖR RUN----------------
      //Detta är mer ifall det kanske går bättre med protokoll där vi skickar och hämtar baserat på olika states vi är i

      // Skicka kategorier till båda spelarna
//      sendCategoriesToClient(toPlayerOne, getListOfDAOS());
//    sendCategoriesToClient(toPlayerTwo, getListOfDAOS());
//
//    // Läs kategori från båda spelarna
//    String categoryPlayerOne = (String) fromPlayerOne.readObject();
//    String categoryPlayerTwo = (String) fromPlayerTwo.readObject();
//
//    // Skicka frågor baserat på valda kategorier
//    sendQuestionsToClient(toPlayerOne, categoryPlayerOne, getListOfDAOS());
//    sendQuestionsToClient(toPlayerTwo, categoryPlayerTwo, getListOfDAOS());
            //-------------------------------------------------

    //Frågan är ifall detta är allt som behövs?
    public void run(){
        //här ska då metoder som vi skickar och hämtar från användaren. programmets "hjärna"
        try {
            //skickar kategorier
            sendCategoriesToClient(toPlayerOne,getListOfDAOS());

            //tar emot från klient
            Object inputFromUser= fromPlayerOne.readObject();
            if (inputFromUser instanceof String chosenCatagory){
                switch (chosenCatagory){
                    case "Sport" -> sendQuestionsToClient(toPlayerOne, "Sport", getListOfDAOS());
                    case "Geo" -> sendQuestionsToClient(toPlayerOne, "Geo", getListOfDAOS());
                    case "Anatomy" -> sendQuestionsToClient(toPlayerOne, "Anatomy", getListOfDAOS());
                    case "History" -> sendQuestionsToClient(toPlayerOne, "History", getListOfDAOS());
                    default -> throw new IllegalArgumentException("Ogiltig kategori: " + inputFromUser);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public List<DAO> getListOfDAOS(){
        List<DAO> listOfDAO=new ArrayList<>();
        listOfDAO.add(sportQuestions);
        listOfDAO.add(anatomyQuestions);
        listOfDAO.add(geoQuestions);
        listOfDAO.add(historyQuestions);
        return listOfDAO;
    }

    public void sendCategoriesToClient(ObjectOutputStream oos, List<DAO> DAOS) throws IOException {
        String cat= "";
        oos.writeObject("CATEGORY");
        for (DAO dao : DAOS) {
            oos.writeObject( cat=dao.getCategory());

        }
        oos.flush();
    }

    //vi måste skicka STRING från användaren för att veta vilka frågor vi ska ge
    public void sendQuestionsToClient(ObjectOutputStream oos, String categoryNameinputFromUser, List<DAO> DAOS) throws IOException {
        for (DAO dao : DAOS) {
            if (dao.getCategory().equals(categoryNameinputFromUser)) {
                oos.writeObject("QUESTIONS");
                oos.writeObject(dao.getQuestionsAndAnswers()); // Serialiserar och skickar frågorna
                oos.flush();
                return;
            }
        }
        throw new IllegalArgumentException("Kategori ej hittad: " + categoryNameinputFromUser);
    }

}


