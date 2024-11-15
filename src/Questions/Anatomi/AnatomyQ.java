package Questions.Anatomi;

import java.util.ArrayList;
import java.util.List;

public class AnatomyQ {
    private String question;
    private String correctanswer;
    private String answer2;
    private String answer3;
    private String answer4;
    private final String welcomeMessage= "Anatomi!";

    public AnatomyQ(String question, String correctanswer, String answer2, String answer3, String answer4) {
        this.question = question;
        this.correctanswer = correctanswer;
        this.answer2 = answer2;
        this.answer3 = answer3;
        this.answer4 = answer4;
    }

    public String getWelcomeMessage() { return welcomeMessage; }

    public String getQuestion() {
        return question;
    }

    public String getCorrectanswer() {
        return correctanswer;
    }

    public String getAnswer2() {
        return answer2;
    }

    public String getAnswer3() {
        return answer3;
    }

    public String getAnswer4() {
        return answer4;
    }


    public List<String> getAllAnswersInLIst(){
        List <String>answers=new ArrayList<>();
        answers.add(correctanswer);
        answers.add(answer2);
        answers.add(answer3);
        answers.add(answer4);
        return answers;
    }

    @Override
    public String toString() {
        return  question +
                ", " + correctanswer +
                ", " + answer2 +
                ", " + answer3 +
                ", " + answer4;
    }
}
