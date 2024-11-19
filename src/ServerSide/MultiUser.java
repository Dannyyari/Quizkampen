package ServerSide;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class MultiUser {

    //klass som gör att vi kan ha flera användare som ansluter sig

    //kanske onödig?

    //Ska vi ändra detta till en resultat arraylist som vi kan placera resultat?
    List<PrintWriter> writers= new ArrayList<PrintWriter>();

    //metod för att lägga till en instans av printwriter
    public void addToWriter(PrintWriter toWrite){
        writers.add(toWrite);
    }

    public void removeWrite(PrintWriter toRemove){
        writers.remove(toRemove);
    }

    public void printWriter(String s){
        for (PrintWriter writer : writers) {
            writer.println(writer);
        }
    }


}
