package Questions.Sport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SportQ implements Serializable {
    private String Question;
    private String correctanswer;
    private String answer2;
    private String answer3;
    private String answer4;
    private final String WelcomeMessage= "Questions.Sport!";

    public SportQ(String question, String correctanswer, String answer2, String answer3, String answer4) {
        Question = question;
        this.correctanswer = correctanswer;
        this.answer2 = answer2;
        this.answer3 = answer3;
        this.answer4 = answer4;
    }
    public List<String> getAllAnswersInListFormat(){
        List<String> answers=new ArrayList<>();
        answers.add(correctanswer);
        answers.add(answer2);
        answers.add(answer3);
        answers.add(answer4);
        return answers;
    }

    public String getWelcomeMessage() {
        return WelcomeMessage;
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

    public String getQuestion() {
        return Question;
    }

    public String getCorrectanswer() {
        return correctanswer;
    }


    @Override
    public String toString() {
        return  Question +
                ", correctanswer " + correctanswer +
                ", " + answer2 +
                ", " + answer3 +
                ", " + answer4;
    }
}
